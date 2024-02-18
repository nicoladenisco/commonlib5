/*
 * CommonNetUtils.java
 *
 * Created on 18-ott-2010, 13.43.45
 *
 * Copyright (C) WinSOFT di Nicola De Nisco
 */
package org.commonlib5.utils;

import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.util.*;
import org.commonlib5.exec.ExecHelper;

/**
 * Funzioni di utilità legate alla rete.
 *
 * @author Nicola De Nisco
 */
public class CommonNetUtils
{
  // costanti
  public static final int SUBNET_CLASSE_A = 8;
  public static final int SUBNET_CLASSE_B = 16;
  public static final int SUBNET_CLASSE_C = 24;

  /**
   * Utility method that delegates to the methods of NetworkInterface to
   * determine addresses for this machine.
   *
   * @return An Array of <code>InetAddress</code> containig all
   * available IP addresses found on the local network interfaces
   * @throws SocketException if there is a problem determining addresses
   */
  public static InetAddress[] getAllLocalAddress()
     throws SocketException
  {
    HashSet<InetAddress> addresses = new HashSet<InetAddress>();
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

    while(interfaces.hasMoreElements())
    {
      NetworkInterface ni = interfaces.nextElement();
      Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
      while(inetAddresses.hasMoreElements())
        addresses.add(inetAddresses.nextElement());
    }

    InetAddress[] rv = new InetAddress[addresses.size()];
    return addresses.toArray(rv);
  }

  /**
   * Ritorna vero se l'indirizzo specificato è un indirizzo locale
   * ovvero associato ad una delle interfacce di questo computer.
   * @param toTest
   * @return vero se è locale
   * @throws java.net.SocketException
   */
  public static boolean isLocalAddress(InetAddress toTest)
     throws SocketException
  {
    InetAddress[] allAddresses = getAllLocalAddress();
    return testAddresses(toTest, allAddresses);
  }

  /**
   * Verifica l'indirizzo con un array di inidirizzi.
   * @param toTest indirizzo da testare
   * @param allAddresses array di indirizzi da sottoporre a test
   * @return vero se l'indirizzo è compreso nell'array
   */
  public static boolean testAddresses(InetAddress toTest, InetAddress[] allAddresses)
  {
    for(InetAddress iAddr : allAddresses)
    {
      if(iAddr.equals(toTest))
        return true;
    }
    return false;
  }

  /**
   * Ritorna vero se i due indirizzi sono nella stessa subnet.
   * La subnet è espressa sotto forma di bit significativi.
   * Typical IPv4 values would be 8 (255.0.0.0), 16 (255.255.0.0) or 24 (255.255.255.0).
   * Typical IPv6 values would be 128 (::1/128) or 10 (fe80::203:baff:fe27:1243/10)
   * @param addr1 primo indirizzo da confrontare
   * @param addr2 secondo indirizzo da confrontare
   * @param numBitPrefix subnetmask sotto forma di numero di bit significativi.
   * @return
   * @throws Exception
   */
  public static boolean isInSameSubnet(InetAddress addr1, InetAddress addr2, int numBitPrefix)
     throws Exception
  {
    byte[] adb1 = addr1.getAddress();
    byte[] adb2 = addr2.getAddress();

    if(adb1.length != adb2.length)
      throw new Exception("Tentativo di confrontare indirizzi non compatibili (ipv4 e ipv6?).");

    int nb = Math.min(numBitPrefix / 8, adb1.length);
    for(int i = 0; i < nb; i++)
    {
      if(adb1[i] != adb2[i])
        return false;
    }

    return true;
  }

  /**
   * Test per subnet locale.
   * Ritorna vero se l'indirizzo indicato è in una delle subnet immediatamente
   * raggiungibi dalla macchina locale, ovvero non sarà necessario raggiugnere
   * nessun router per mettersi in contatto con l'indirizzo indicato.
   * @param toTest indirizzo da testare
   * @return vero se in una delle subnet locali
   * @throws Exception
   */
  public static boolean isLocalSubnet(InetAddress toTest)
     throws Exception
  {
    return getAddressInSubnet(toTest) != null;
  }

  /**
   * Acquisisce indirizzo locale compatibile con indirizzo remoto.
   * Confronta gli indirizzi locali con quello fornito come test
   * e ritorna il primo indirizzo locale sulla stessa sottorete.
   * @param toTest indirizzo da testare
   * @return indirizzo compatibile (sulla stessa sottorete) altrimenti null
   * @throws Exception
   */
  public static InetAddress getAddressInSubnet(InetAddress toTest)
     throws Exception
  {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

    while(interfaces.hasMoreElements())
    {
      NetworkInterface ni = interfaces.nextElement();
      List<InterfaceAddress> lIadr = ni.getInterfaceAddresses();
      for(InterfaceAddress iadr : lIadr)
      {
        InetAddress adr = iadr.getAddress();

        if(adr instanceof Inet4Address && toTest instanceof Inet4Address)
        {
          // BUG SU WINDOWS (come al solito)
          // su alcune interfacce ritorna come network prefix il valore 64
          // su indirizzo IPV4 (ovviamente impossibile essendo a 32 bit)
          int nb = iadr.getNetworkPrefixLength();
          if(adr instanceof Inet4Address && nb == 64)
            nb = 24;

          if(isInSameSubnet(adr, toTest, nb))
            return adr;
        }

        if(adr instanceof Inet6Address && toTest instanceof Inet6Address)
        {
          if(isInSameSubnet(adr, toTest, iadr.getNetworkPrefixLength()))
            return adr;
        }
      }
    }

    return null;
  }

  /**
   * Esegue un ping all'indirizzo indicato.
   * L'indirizzo può essere espresso come nome dell'host
   * o come indirizzo IPV4 o IPV6.
   * Viene usato un pacchetto ICMP per testare la connessione
   * (proprio come il ping a riga di comando).
   * @param ipOrAddress
   * @return vero se l'host è raggiungibile
   */
  public static boolean ping(String ipOrAddress)
  {
    try
    {
      InetAddress address = InetAddress.getByName(ipOrAddress);
      return address.isReachable(3000);
    }
    catch(Throwable ex)
    {
      return false;
    }
  }

  /**
   * Chiuse socket senza sollevare eccezione.
   * @param s socket da chiudere
   */
  public static void safeClose(Socket s)
  {
    try
    {
      s.close();
    }
    catch(Throwable e)
    {
      // eccezione ignorata
    }
  }

  /**
   * Ritorna la stringa in dotted notation di un indirizzo.
   * @param address indirizzo
   * @return stringa nel formato xx.xx.xx.xx
   */
  public static String toString(InetAddress address)
  {
    if(address == null)
      return "";

    String rv = address.toString();
    int pos = rv.indexOf('/');
    return pos == -1 ? rv : rv.substring(pos + 1);
  }

  /**
   * Scarica files da server CIFS utilizzando smbclient.
   * @param unc splitter di UNC
   * @param moveInfo coppia di nome file remoto e relativo file sul file system locale
   * @param username credenziali di accesso (può essere null)
   * @param password credenziali di accesso ((può essere null)
   * @return helper del processo lanciato
   * @throws Exception
   */
  public static ExecHelper downloadSmbclientFiles(UncSplitter unc, List<Pair<String, File>> moveInfo,
     String username, String password)
     throws Exception
  {
    String share = unc.getShare();
    String dirName = unc.getDirectory();

    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("smbclient");
    cmd.add(share);

    if(username != null)
    {
      cmd.add("-U");
      if(password == null)
        cmd.add(username);
      else
        cmd.add(username + '%' + password);
    }

    StringBuilder sb = new StringBuilder(128);
    if(dirName != null && !dirName.isEmpty())
      sb.append("cd ").append(dirName).append("; ");
    for(Pair<String, File> p : moveInfo)
      sb.append("get ").append(p.first).append(' ').append(p.second.getCanonicalPath()).append("; ");

    cmd.add("-c");
    cmd.add(sb.toString());

    return ExecHelper.exec(cmd);
  }

  /**
   * Invia files a server CIFS utilizzando smbclient.
   * @param unc splitter di UNC
   * @param moveInfo coppia di file sul file system locale e nome file remoto relativo
   * @param username credenziali di accesso (può essere null)
   * @param password credenziali di accesso ((può essere null)
   * @return helper del processo lanciato
   * @throws Exception
   */
  public static ExecHelper uploadSmbclientFiles(UncSplitter unc, List<Pair<File, String>> moveInfo,
     String username, String password)
     throws Exception
  {
    String share = unc.getShare();
    String dirName = unc.getDirectory();

    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("smbclient");
    cmd.add(share);

    if(username != null)
    {
      cmd.add("-U");
      if(password == null)
        cmd.add(username);
      else
        cmd.add(username + '%' + password);
    }

    StringBuilder sb = new StringBuilder(128);
    if(dirName != null && !dirName.isEmpty())
      sb.append("cd ").append(dirName).append("; ");
    for(Pair<File, String> p : moveInfo)
      sb.append("put ").append(p.first.getCanonicalPath()).append(' ').append(p.second).append("; ");

    cmd.add("-c");
    cmd.add(sb.toString());

    return ExecHelper.exec(cmd);
  }

  /**
   * Rinomina files su server CIFS utilizzando smbclient.
   * @param unc splitter di UNC
   * @param toRename collezione di coppia di valori nome vecchio/nome nuovo
   * @param username credenziali di accesso (può essere null)
   * @param password credenziali di accesso ((può essere null)
   * @return helper del processo lanciato
   * @throws Exception
   */
  public static ExecHelper renameSmbclientFiles(UncSplitter unc, Collection<Pair<String, String>> toRename, String username, String password)
     throws Exception
  {
    String share = unc.getShare();
    String dirName = unc.getDirectory();

    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("smbclient");
    cmd.add(share);

    if(username != null)
    {
      cmd.add("-U");
      if(password == null)
        cmd.add(username);
      else
        cmd.add(username + '%' + password);
    }

    StringBuilder sb = new StringBuilder(128);
    if(dirName != null && !dirName.isEmpty())
      sb.append("cd ").append(dirName).append("; ");
    for(Pair<String, String> p : toRename)
      sb.append("rename ").append(p.first).append(' ').append(p.second).append("; ");

    cmd.add("-c");
    cmd.add(sb.toString());

    return ExecHelper.exec(cmd);
  }

  /**
   * Cancella files da server CIFS utilizzando smbclient.
   * @param unc splitter di UNC
   * @param nomeCancella collezione di nomi da cancellare
   * @param username credenziali di accesso (può essere null)
   * @param password credenziali di accesso ((può essere null)
   * @return helper del processo lanciato
   * @throws Exception
   */
  public static ExecHelper deleteSmbclientFiles(UncSplitter unc, Collection<String> nomeCancella, String username, String password)
     throws Exception
  {
    String share = unc.getShare();
    String dirName = unc.getDirectory();

    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("smbclient");
    cmd.add(share);

    if(username != null)
    {
      cmd.add("-U");
      if(password == null)
        cmd.add(username);
      else
        cmd.add(username + '%' + password);
    }

    StringBuilder sb = new StringBuilder(128);
    if(dirName != null && !dirName.isEmpty())
      sb.append("cd ").append(dirName).append("; ");
    for(String nome : nomeCancella)
      sb.append("del ").append(nome).append("; ");

    cmd.add("-c");
    cmd.add(sb.toString());

    return ExecHelper.exec(cmd);
  }

  /**
   * Scarica files e directory ricorsivamente da server CIFS utilizzando smbclient.
   * @param unc splitter di UNC
   * @param dirLocalToSave directory locale dove salvare i files
   * @param username credenziali di accesso (può essere null)
   * @param password credenziali di accesso ((può essere null)
   * @return helper del processo lanciato
   * @throws Exception
   */
  public static ExecHelper downloadSmbclientDirectory(UncSplitter unc, File dirLocalToSave, String username, String password)
     throws Exception
  {
    // produce un comando simile a questo:
    // smbclient '\\server\share' -U user%password -c 'prompt OFF;recurse ON;cd 'path\to\directory\';lcd '~/path/to/download/to/';mget *'

    String share = unc.getShare();
    String dirName = unc.getDirectory();

    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("smbclient");
    cmd.add(share);

    if(username != null)
    {
      cmd.add("-U");
      if(password == null)
        cmd.add(username);
      else
        cmd.add(username + '%' + password);
    }

    StringBuilder sb = new StringBuilder();
    sb.append("prompt OFF; recurse ON; ");
    sb.append("cd ").append(dirName).append("; ");
    sb.append("lcd ").append(dirLocalToSave.getAbsolutePath()).append("; ");
    sb.append("mget *");

    cmd.add("-c");
    cmd.add(sb.toString());

    return ExecHelper.exec(cmd);
  }

  /**
   * Test rapido indirizzo IP.
   * Puo testare sia IPV4 che IPV6 sia per indirizzi che per sottoreti allineate al byte (8/16/24).
   * Esempio: isEqual(toTest, 127, 0, 0, 1), isEqual(toTest, 192, 168)
   * @param toTest
   * @param test
   * @return
   * @throws Exception
   */
  public static boolean isEqual(InetAddress toTest, int... test)
     throws Exception
  {
    byte[] address = toTest.getAddress();
    int num = Math.min(address.length, test.length);
    for(int i = 0; i < num; i++)
    {
      if(address[i] != ((byte) (test[i] & 0xFF)))
        return false;
    }
    return true;
  }

  /**
   * Ritorna vero se l'indirizzo indicato è una rete virtuale Docker.
   * Il test è molto semplice: verifica solo che l'indirizzo sia sulla sottorete 172.17/16.
   * @param toTest indirizzo da verificare
   * @return vero se è una sottorete docker
   * @throws Exception
   */
  public static boolean isDockerSubnet(InetAddress toTest)
     throws Exception
  {
    if(toTest instanceof Inet4Address)
      return isEqual(toTest, 172, 17);

    return false;
  }

  /**
   * Verifica se l'host locale è un container Docker.
   * La verifica avviene attraverso gli indirizzi di interfaccia.
   * @return vero se siamo in un docker
   * @throws Exception
   */
  public static boolean isInsideDocker()
     throws Exception
  {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

    while(interfaces.hasMoreElements())
    {
      NetworkInterface ni = interfaces.nextElement();
      Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
      while(inetAddresses.hasMoreElements())
      {
        InetAddress toTest = inetAddresses.nextElement();

        if(toTest instanceof Inet4Address)
        {
          // scarta l'host ospite dei contenitori
          if(isEqual(toTest, 172, 17, 0, 1))
            return false;

          if(isEqual(toTest, 172, 17))
            return true;
        }
      }
    }

    return false;
  }

  /**
   * Ritorna la porta corretta di una URI per i protocolli conosciuti.
   * @param uri
   * @return
   */
  public static int getCorrectPort(URI uri)
  {
    int port = uri.getPort();

    if(port > 0)
      return port;

    switch(StringOper.okStr(uri.getScheme()).toLowerCase())
    {
      case "http":
        return 80;

      case "https":
        return 443;

      case "ftp":
        return 21;

      case "ftps":
        return 22;
    }

    throw new UnsupportedOperationException("Non riesco a determinare la porta per " + uri);
  }

  public static boolean isTLS(URI uri)
  {
    switch(StringOper.okStr(uri.getScheme()).toLowerCase())
    {
      case "http":
        return false;

      case "https":
        return true;

      case "ftp":
        return false;

      case "ftps":
        return true;
    }

    throw new UnsupportedOperationException("Non riesco a determinare la porta per " + uri);
  }
}
