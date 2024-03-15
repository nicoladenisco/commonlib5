/*
 * CommonFileUtils.java
 *
 * Created on 23 marzo 2006, 12.26
 */
package org.commonlib5.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.commonlib5.exec.ExecHelper;
import org.commonlib5.io.ByteBufferInputStream;
import org.commonlib5.io.ByteBufferOutputStream;

/**
 * Funzioni statiche per la gestione di files su disco.
 * @author Nicola De Nisco
 */
public class CommonFileUtils
{
  public static final long KILOBYTE = 1024l;
  public static final long MEGABYTE = 1024l * 1024l;
  public static final long GIGABYTE = 1024l * 1024l * 1024l;
  public static final long TERABYTE = 1024l * 1024l * 1024l * 1024l;
  public static final int BUFFER_SIZE = 8192;

  /**
   * Sposta un file in una nuova posizione.
   * La directory destinazione deve gia' esistere.
   * Cerca di usare un rename, altrimenti copia
   * il file e quindi cancella l'originale.
   * @param in file di ingresso
   * @param out file di uscita
   * @return true
   * @throws java.lang.Exception
   */
  public static boolean moveFile(File in, File out)
     throws Exception
  {
    if(out.isDirectory())
      deleteDir(out);
    else
      out.delete();

    if(in.isDirectory())
    {
      if(in.renameTo(out) == false)
      {
        copyDir(in, out);
        deleteDir(in);
      }
    }
    else if(in.isFile())
    {
      if(in.renameTo(out) == false)
      {
        copyFile(in, out);
        in.delete();
      }
    }
    else
      return false;

    return true;
  }

  /**
   * Sposta un file da una directory ad un'altra.
   * @param dirIn directory dove il file esiste
   * @param dirOut directory dove collocare il file
   * @param fileName il solo nome del file
   * @return vero se l'operazione è riuscita
   * @throws Exception
   */
  public static boolean moveFileDirToDir(File dirIn, File dirOut, String fileName)
     throws Exception
  {
    if(!dirIn.isDirectory() || !dirOut.isDirectory())
      return false;

    File in = new File(dirIn, fileName);
    File out = new File(dirOut, fileName);

    if(!in.exists() || !in.isFile())
      return false;

    return moveFile(in, out);
  }

  /**
   * Sposta tutti o un gruppo di files fra directory.
   * @param dirIn directory di input
   * @param dirOut directory di output
   * @param filter filtro per i files (null = tutti i files)
   * @return vero se l'operazione è riuscita
   * @throws Exception
   */
  public static boolean moveAllFileDirToDir(File dirIn, File dirOut, FileFilter filter)
     throws Exception
  {
    if(!dirIn.isDirectory() || !dirOut.isDirectory())
      return false;

    File[] inFiles = filter == null ? dirIn.listFiles() : dirIn.listFiles(filter);

    for(int i = 0; i < inFiles.length; i++)
    {
      File in = inFiles[i];
      File out = new File(dirOut, in.getName());
      moveFile(in, out);
    }

    return true;
  }

  /**
   * Copia un file in una nuova posizione.
   * La directory destinazione deve gia' esistere.
   * @param in file di ingresso
   * @param out file di uscita
   * @return vero se l'operazione è riuscita
   * @throws java.lang.Exception
   */
  public static boolean copyFile(File in, File out)
     throws Exception
  {
    try(FileInputStream fis = new FileInputStream(in.getAbsolutePath());
       FileOutputStream fos = new FileOutputStream(out.getAbsolutePath()))
    {
      copyStream(fis, fos);
    }

    return true;
  }

  /**
   * Copia file di testo applicando una conversione dell'encoding.
   * @param in file da leggere
   * @param encodingInput encoding input (null per il default di sistema)
   * @param out file da scrivere
   * @param encodingOutput encoding di output (null per il default di sistema)
   * @return vero se l'operazione è riuscita
   * @throws Exception
   */
  public static boolean copyTxtFile(File in, String encodingInput, File out, String encodingOutput)
     throws Exception
  {
    try(Reader r = encodingInput == null ? new InputStreamReader(new FileInputStream(in))
                      : new InputStreamReader(new FileInputStream(in), encodingInput);
       Writer w = encodingOutput == null ? new OutputStreamWriter(new FileOutputStream(out))
                     : new OutputStreamWriter(new FileOutputStream(out), encodingOutput))
    {
      copyChar(r, w);
    }

    return true;
  }

  /**
   * Copia un file da una directory ad un'altra.
   * @param dirIn directory dove il file esiste
   * @param dirOut directory dove collocare il file
   * @param fileName il solo nome del file
   * @return vero se l'operazione è riuscita
   * @throws Exception
   */
  public static boolean copyFileDirToDir(File dirIn, File dirOut, String fileName)
     throws Exception
  {
    if(!dirIn.isDirectory() || !dirOut.isDirectory())
      return false;

    File in = new File(dirIn, fileName);
    File out = new File(dirOut, fileName);

    if(!in.exists() || !in.isFile())
      return false;

    return copyFile(in, out);
  }

  /**
   * Come la copyFile ma la copia viene eseguita solo se
   * fOrig e' piu' nuovo di fDest o fDest non esiste.
   * @param in file di ingresso
   * @param out file di uscita
   * @return true
   * @throws java.lang.Exception
   */
  public static boolean copyFileIfNew(File fOrig, File fDest)
     throws Exception
  {
    if(!fDest.exists() || fOrig.lastModified() > fDest.lastModified())
      return copyFile(fOrig, fDest);
    return false;
  }

  /**
   * Come la copyFile ma la copia viene eseguita solo se
   * fOrig e' piu' nuovo di fDest o fDest non esiste.
   * @param in file di ingresso
   * @param out file di uscita
   * @return true
   * @throws java.lang.Exception
   */
  public static boolean copyFileIfDifferent(File fOrig, File fDest)
     throws Exception
  {
    if(!fDest.exists() || !isFilesEquals(fOrig, fDest))
      return copyFile(fOrig, fDest);
    return false;
  }

  /**
   * Confronto binario fra files.
   * Effettua un confronto binario fra due files su disco.
   * Se uno o entrambi non esistono ritorna sempre false.
   * @param fOrig primo file da confrontare
   * @param fDest secondo file da confrontare
   * @return vero se sono uguali
   * @throws Exception
   */
  public static boolean isFilesEquals(File fOrig, File fDest)
     throws Exception
  {
    if(!fOrig.exists() || !fDest.exists())
      return false;

    try(FileInputStream isOrig = new FileInputStream(fOrig);
       FileInputStream isDest = new FileInputStream(fDest))
    {
      byte[] bufOrig = new byte[BUFFER_SIZE];
      byte[] bufDest = new byte[BUFFER_SIZE];

      int nbO, nbD;

      while(true)
      {
        nbO = readStream(bufOrig, isOrig, 0, BUFFER_SIZE);
        nbD = readStream(bufDest, isDest, 0, BUFFER_SIZE);

        if(nbO == 0 && nbD == 0)
          return true;

        if(nbO != nbD)
          return false;

        if(nbO == BUFFER_SIZE)
        {
          if(!Arrays.equals(bufOrig, bufDest))
            return false;
        }
        else
        {
          if(!Arrays.equals(
             Arrays.copyOf(bufOrig, nbO),
             Arrays.copyOf(bufDest, nbD)))
            return false;
        }
      }
    }
  }

  /**
   * Copia l'intero contenuto di uno stream di input in uno di output.
   * La lettura prosegue fino a quando lo stream di input restituisce
   * 0 come numero di bytes letti.
   * @param is stream di input
   * @param os stream di output
   * @return true
   * @throws java.lang.Exception
   */
  public static boolean copyStream(InputStream is, OutputStream os)
     throws Exception
  {
    int n;
    byte[] buffer = new byte[BUFFER_SIZE];
    while((n = is.read(buffer)) > 0)
    {
      os.write(buffer, 0, n);
    }
    return true;
  }

  /**
   * Copia l'intero contenuto di uno reader di input in un writer di output.
   * La lettura prosegue fino a quando lo stream di input restituisce
   * 0 come numero di bytes letti.
   * @param ir reader di input
   * @param ow writer di output
   * @return true
   * @throws java.lang.Exception
   */
  public static boolean copyChar(Reader ir, Writer ow)
     throws Exception
  {
    int n;
    char[] buffer = new char[BUFFER_SIZE];
    while((n = ir.read(buffer)) > 0)
    {
      ow.write(buffer, 0, n);
    }
    return true;
  }

  /**
   * Copia una stream di input sullo stream di output per un
   * massimo di maxSize bytes. La copia si puo' interrompere se
   * i bytes in is sono minori di maxBytes.
   * @param is stream di input
   * @param os stream di output
   * @param size massimo numero di caratteri copiati
   * @return true se i bytes letti sono proprio 'size'
   * @throws java.lang.Exception
   */
  public static boolean copyStream(InputStream is, OutputStream os, long size)
     throws Exception
  {
    return copyStream(is, os, size, null, null);
  }

  /**
   * Copia l'intero contenuto di uno stream di input in uno di output.
   * Questa funziona copia al massimo 'size' bytes fra gli stream.
   * @param is stream di input
   * @param os stream di output
   * @param size numero totale di bytes
   * @param lol listner a cui notificare lo stato di avanzamento
   * @return vero se il numero di bytes letti è propio 'size'
   * @throws java.lang.Exception
   */
  public static boolean copyStream(InputStream is, OutputStream os, long size, LongOperListener lol)
     throws Exception
  {
    return copyStream(is, os, size, null, lol);
  }

  /**
   * Copia l'intero contenuto di uno stream di input in uno di output.
   * Questa funziona copia al massimo 'size' bytes fra gli stream.
   * @param is stream di input
   * @param os stream di output
   * @param size numero totale di bytes
   * @param md eventuale MessageDigest per il calcolo dell'hash (può essere null)
   * @param lol listner a cui notificare lo stato di avanzamento (può essere null)
   * @return vero se il numero di bytes letti è propio 'size'
   * @throws java.lang.Exception
   */
  public static boolean copyStream(InputStream is, OutputStream os,
     long size, MessageDigest md, LongOperListener lol)
     throws Exception
  {
    long nb = 0, total = 0;
    byte[] buffer = new byte[BUFFER_SIZE];

    if(lol != null)
      lol.resetUI();

    do
    {
      nb = size - total;
      if(nb > BUFFER_SIZE)
        nb = BUFFER_SIZE;

      nb = is.read(buffer, 0, (int) nb);

      if(nb <= 0)
        break;

      os.write(buffer, 0, (int) nb);
      total += nb;

      if(md != null)
        md.update(buffer, 0, (int) nb);

      if(lol != null)
        if(!lol.updateUI(total, size))
          return false;
    }
    while(total < size);

    if(lol != null)
      lol.completeUI(size);

    return total == size;
  }

  /**
   * Legge in modo sicuro il numero di byte richiesto dallo stream.
   * La lettura dallo strem prosegue fino a quando l'esatto numero
   * di bytes richiesti viene letto. Se lo stream viene chiuso
   * ritorna il numero di byte effettivamente letti.
   * @param buffer byte letti
   * @param is stream da leggere
   * @param offset indice del buffer dove depositare il primo byte
   * @param size numero di byte da leggere
   * @return il numero di bytes effettivamente letti
   * @throws Exception
   */
  public static int readStream(byte[] buffer, InputStream is, int offset, int size)
     throws Exception
  {
    int n = 0, c = 0;
    for(c = 0; c < size; c += n)
    {
      if((n = is.read(buffer, offset + c, size - c)) <= 0)
        break;
    }
    return c;
  }

  /**
   * Scrive in modo sicuro il numero di byte richiesto dallo stream.
   * La scrittura sullo strem prosegue fino a quando l'esatto numero
   * di bytes richiesti viene scritto. Se lo stream viene chiuso
   * ritorna il numero di byte effettivamente letti.
   * @param buffer byte da scrivere
   * @param os stream da scrivere
   * @param offset indice del buffer dove prelevare il primo byte
   * @param size numero di byte da scrivere
   * @return il numero di bytes effettivamente scritti
   * @throws Exception
   */
  public static int writeStream(byte[] buffer, OutputStream os, int offset, int size)
     throws Exception
  {
    os.write(buffer, offset, size);
    return size;
  }

  /**
   * Copia il contenuto di una directory in una directory nuova.
   * La directory di output viene creata se non esiste.
   * @param dirIn la directory da copiare
   * @param dirOut la directory destinazione (conterrà la copia)
   * @return numero dei files copiati.
   * @throws java.lang.Exception
   */
  public static int copyDir(File dirIn, File dirOut)
     throws Exception
  {
    if(!(dirOut.exists() && dirOut.isDirectory()))
      if(!dirOut.mkdirs())
        throw new IOException("Failed to create the directory " + dirOut.getAbsolutePath());

    int count = 0;
    File[] list = dirIn.listFiles();
    if(list == null || list.length == 0)
      return 0;

    for(int i = 0; i < list.length; i++)
    {
      File f = list[i];
      File outNew = new File(dirOut, f.getName());

      if(f.isDirectory())
      {
        if(!outNew.isDirectory())
          if(!outNew.mkdir())
            throw new IOException("Failed to create the directory " + outNew.getAbsolutePath());

        count += copyDir(f, outNew);
      }
      else
      {
        copyFile(f, outNew);
        count++;
      }
    }

    return count;
  }

  /**
   * Cancella una directory e tutto il suo contenuto.
   * @param dir directory da cancellare
   * @return numero di files cancellati.
   * @throws java.lang.Exception
   */
  public static int deleteDir(File dir)
     throws Exception
  {
    return deleteDir(dir, true);
  }

  /**
   * Cancella il contenuto di una directory ed eventualmente
   * la directory stessa.
   * @param dir directory da cancellare
   * @param delItself se vero cancella anche la directory
   * @return numero di files cancellati.
   * @throws java.lang.Exception
   */
  public static int deleteDir(File dir, boolean delItself)
     throws Exception
  {
    Stack<File> parent = new Stack<File>();
    parent.push(dir);
    return deleteDirJava(parent, dir, delItself, 0);
  }

  /**
   * Cancella il contenuto di una directory ed eventualmente
   * la directory stessa.
   * @param dir directory da cancellare
   * @param delItself se vero cancella anche la directory (solo se è vuota)
   * @param onlyOlderThan se diverso da zero cancella solo i files più vecchi dei milliscondi specificati
   * @return numero di files cancellati.
   * @throws java.lang.Exception
   */
  public static int deleteDir(File dir, boolean delItself, long onlyOlderThan)
     throws Exception
  {
    Stack<File> parent = new Stack<File>();
    parent.push(dir);
    return deleteDirJava(parent, dir, delItself, onlyOlderThan);
  }

  /**
   * Cancella il contenuto di una directory ed eventualmente
   * la directory stessa. Versione Java implementata internamente.
   * @param dir directory da cancellare
   * @param delItself se vero cancella anche la directory
   * @param onlyOlderThan se diverso da zero cancella solo i files più vecchi dei milliscondi specificati
   * @return numero di files cancellati.
   */
  private static int deleteDirJava(Stack<File> parent, File dir, boolean delItself, long onlyOlderThan)
     throws Exception
  {
    int numdel = 0;
    boolean runLoop = true;

    if(!dir.isDirectory())
      return 0;

    while(runLoop)
    {
      // limita il recupero a 1000 files per volta
      // altrimenti si puo' avere una overflow di memoria
      File[] list = dir.listFiles(new FileFilter()
      {
        int count = 0;

        @Override
        public boolean accept(File pathname)
        {
          if(onlyOlderThan != 0)
            if(!isOlderThan(pathname, onlyOlderThan))
              return false;

          return count++ < 1000;
        }
      });

      if(list == null)
        break;

      if(list.length == 0)
        break;

      for(int i = 0; i < list.length; i++)
      {
        File f = list[i];

        if(f.exists())
        {
          if(f.isDirectory())
          {
            // controllo per alluppaggio ricorsivo
            if(parent.contains(f))
            {
              runLoop = false;
              continue;
            }

            parent.push(f);
            numdel += deleteDirJava(parent, f, true, onlyOlderThan);
            parent.pop();
          }
          else
          {
            // se non riesce a cancellare il file
            // deve uscire, altrimenti andiamo in loop
            if(f.delete())
              numdel++;
            else
              runLoop = false;
          }
        }
      }
    }

    if(delItself)
      dir.delete();

    return numdel;
  }

  /**
   * Sospenda il thread chiamante fino a quando sull'imput stream indicato
   * non sono presente il numero di caratteri richieste oppure il timeout
   * e' scaduto.
   * Nessun carattere viene estratto dall'input stream.
   * @param ism input stream da controllare
   * @param Count numero di caratteri da attendere
   * @param Timeout numero di millisecondi massimi di attesa
   * @return true se nello stream di sono Count caratteri validi
   * @throws java.lang.Exception
   */
  public static boolean WaitRxCount(InputStream ism, int Count, int Timeout)
     throws Exception
  {
    int NumBytes = 0;
    long cti = System.currentTimeMillis();
    do
    {
      NumBytes = ism.available();

      if((System.currentTimeMillis() - cti) > Timeout)
        return false;
    }
    while(NumBytes < Count);
    return true;
  }

  /**
   * Parsing di una dimensione di file system.
   * Effettua il parsing utilizzando i modificatori
   * K, M, G, T per semplificare la scrittura del valore.
   * Il valore puo' anche essere espresso in forma decimale:
   * ad esempio 4.7G 1.3T ecc..
   * @param s_dim dimensione sotto forma di stringa
   * @return dimensione in bytes
   * @throws java.lang.Exception
   */
  public static long parseDimension(String s_dim)
     throws Exception
  {
    long dim = 0;
    s_dim = s_dim.toUpperCase().trim();

    if(s_dim.endsWith("K"))
      dim = compute(s_dim, 1, KILOBYTE);
    else if(s_dim.endsWith("M"))
      dim = compute(s_dim, 1, MEGABYTE);
    else if(s_dim.endsWith("G"))
      dim = compute(s_dim, 1, GIGABYTE);
    else if(s_dim.endsWith("T"))
      dim = compute(s_dim, 1, TERABYTE);
    else
      dim = Long.parseLong(s_dim);

    return dim;
  }

  private static double parsePart(String s_dim, int left)
  {
    return Double.parseDouble(s_dim.substring(0, s_dim.length() - left));
  }

  private static long compute(String s_dim, int left, double multiplay)
  {
    return (long) (parsePart(s_dim, left) * multiplay);
  }

  /**
   * Conta le linee all'interno di un file di testo.
   * @param asciiFileName file da interrogare
   * @return il numero di linee ('\n') contenute
   * @throws java.lang.Exception
   */
  public static int countLineesInFile(File asciiFileName)
     throws Exception
  {
    FileInputStream fis = new FileInputStream(asciiFileName);
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

    int count = 0;
    try
    {
      String linea;
      while((linea = br.readLine()) != null)
      {
        count++;
      }
    }
    finally
    {
      br.close();
      fis.close();
    }

    return count;
  }

  /**
   * Cerca un file all'interno di una directory.
   * Vedi FileScanner per una versione più sofisticata.
   * @param dirToSearch directory di partenza per la ricerca
   * @param fileName il file/directory da cercare
   * @param recurse abilita la ricorsione all'interno di dirToSearch
   * @return
   */
  public static File findFile(File dirToSearch, String fileName, boolean recurse)
     throws Exception
  {
    File[] list = dirToSearch.listFiles();

    for(int i = 0; i < list.length; i++)
    {
      File f = list[i];

      if(fileName.equals(f.getName()))
        return f;
    }

    if(recurse)
      for(int i = 0; i < list.length; i++)
      {
        File f = list[i];

        if(f.isDirectory())
          if((f = findFile(f, fileName, recurse)) != null)
            return f;
      }

    return null;
  }

  /**
   * Legge una linea di testo terminata da '\n' da uno stream.
   * La dimensione massima della linea e' 256 caratteri e
   * l'encoding utilizzato e' UNICODE (UTF8).
   * @param is lo stream da cui leggere la linea.
   * @return la stringa letta o null se lo stream e' chiuso
   * @throws java.lang.Exception
   */
  public static String readLine(InputStream is)
     throws Exception
  {
    return readLine(is, '\n', 256, "UTF-8");
  }

  /**
   * Legge una stringa da uno stream di input.
   * La stringa viene delimitata dal delimitatore indicato
   * e comunque da un massimo di byte indicati.
   * I byte vengono convertiti in caratteri secondo
   * l'encoding specificato.
   * @param is lo stream di input
   * @param delimiter il delimitatore della stringa
   * @param dimBuffer dimensione del buffer di byte
   * @param encoding per la conversione della stringa
   * @return la stringa letta o null se lo stream e' chiuso
   * @throws java.lang.Exception
   */
  public static String readLine(InputStream is, int delimiter, int dimBuffer, String encoding)
     throws Exception
  {
    int c = 0, i = 0;
    byte[] buffer = new byte[dimBuffer];

    for(i = 0; i < buffer.length; i++)
    {
      c = is.read();

      if(c == delimiter)
        break;

      if(c == -1)
      {
        if(i == 0)
          return null;
        break;
      }

      buffer[i] = (byte) c;
    }

    return new String(buffer, 0, i, encoding);
  }

  /**
   * Data una URL valida e un nome di file legge la url e deposita
   * il contenuto nel file indicato.
   * @param fWrite oggetto File dove salvare il contenuto
   * @param urlName Url da leggere
   * @throws Exception errori vari
   */
  public static void readUrlToFile(String urlName, File fWrite)
     throws Exception
  {
    URL url = new URL(urlName);
    readUrlToFile(url, fWrite);
  }

  /**
   * Data una URL valida e un nome di file legge la url e deposita
   * il contenuto nel file indicato.
   * @param url oggetto URL da leggere
   * @param fWrite oggetto File dove salvare il contenuto
   * @throws Exception errori vari
   */
  public static void readUrlToFile(URL url, File fWrite)
     throws Exception
  {
    readUrlToFile(url, fWrite, null);
  }

  /**
   * Legge un file ASCII e cerca all'interno una stringa.
   * Una versione avanzata con la possibilità di specificare una regular expression per la ricerca è 'grep'.
   * @param cerca stringa da cercare
   * @param asciiFile file ASCII da esaminare
   * @param encoding per la lettura del file
   * @return la prima linea con la stringa (0 based), -1 se non trovata.
   * @throws java.lang.Exception errori vari
   */
  public static int findStringInFile(String cerca, File asciiFile, String encoding)
     throws Exception
  {
    String linea;
    int found = -1;

    try(FileInputStream fis = new FileInputStream(asciiFile);
       LineNumberReader br = new LineNumberReader(new InputStreamReader(fis, encoding), BUFFER_SIZE))
    {
      while((linea = br.readLine()) != null && found == -1)
      {
        if(linea.contains(cerca))
          found = br.getLineNumber();
      }
    }

    return found;
  }

  /**
   * Legge un file con dati base64 e produce
   * la corrispondente stringa.
   * Usa encoding di default.
   * @param input file con dati base64
   * @return stringa corrispondente
   * @throws java.lang.Exception
   */
  public static String binary_2_Base64(File input)
     throws Exception
  {
    byte[] bi = readFile(input);
    byte[] res = Base64.encodeBase64(bi);
    return new String(res);
  }

  /**
   * Converte da base64 a stringa.
   * Usa encoding di default.
   * @param bi array di byte base64
   * @return stringa corrispondente
   */
  public static String binary_2_Base64(byte[] bi)
  {
    byte[] res = Base64.encodeBase64(bi);
    return new String(res);
  }

  /**
   * Converte una stringa nel formato base64.
   * Viene usato l'encoding di default.
   * @param input stringa da convertire
   * @return array di byte base64
   */
  public static byte[] base64_2_Binary(String input)
  {
    return Base64.decodeBase64(input.getBytes());
  }

  /**
   * Calcola la Hash del file utilizzando l'algoritmo richiesto.
   * @param f file di cui calcolare l'hash
   * @param hashAlgo algoritmo di hashing (ES: "SHA1")
   * @return stringa di hash
   * @throws java.lang.Exception
   */
  public static byte[] calcolaHashFile(File f, String hashAlgo)
     throws Exception
  {
    // Obtain a message digest object.
    MessageDigest md = MessageDigest.getInstance(hashAlgo);
    md.reset();

    // Calculate the digest for the given file.
    try(FileInputStream in = new FileInputStream(f))
    {
      byte[] buffer = new byte[8192];

      int length;
      while((length = in.read(buffer)) != -1)
        md.update(buffer, 0, length);
    }

    return md.digest();
  }

  /**
   * Calcola l'hash della stringa passata come parametro
   * secondo il tipo di algoritmo specificato (di solito SHA1).
   * @param str stringa di cui calcolare l'hash
   * @param hashAlgo algoritmo da applicare
   * @return hash della stringa
   * @throws java.lang.Exception
   */
  public static String calcolaHashStringa(String str, String hashAlgo)
     throws Exception
  {
    MessageDigest md = MessageDigest.getInstance(hashAlgo);
    md.reset();
    md.update(str.getBytes());
    return HexString.bufferToHex(md.digest());
  }

  /**
   * Scrive un file di testo a partire da una stringa.
   * @param f file da scrivere (verra' distrutto se gia' esiste)
   * @param output stringa da scrivere nel file
   * @param encoding se null usa encoding di default
   * @throws java.lang.Exception
   */
  public static void writeFileTxt(File f, String output, String encoding)
     throws Exception
  {
    writeFileTxt(f, output, encoding, false);
  }

  /**
   * Scrive un file di testo a partire da una stringa.
   * @param f file da scrivere (verra' distrutto se gia' esiste)
   * @param output stringa da scrivere nel file
   * @param encoding se null usa encoding di default
   * @param append se vero scrive in coda al file (se esiste)
   * @throws java.lang.Exception
   */
  public static void writeFileTxt(File f, String output, String encoding, boolean append)
     throws Exception
  {
    try(FileOutputStream fos = new FileOutputStream(f, append))
    {
      writeFileTxt(fos, output, encoding);
    }
  }

  /**
   * Scrive un file di testo a partire da una stringa.
   * @param os stream con i dati binari
   * @param output stringa da scrivere nel file
   * @param encoding se null usa encoding di default
   * @throws java.lang.Exception
   */
  public static void writeFileTxt(OutputStream os, String output, String encoding)
     throws Exception
  {
    OutputStreamWriter osw = encoding == null ? new OutputStreamWriter(os) : new OutputStreamWriter(os, encoding);
    osw.write(output);
    osw.flush();
  }

  /**
   * Data una URL valida e un nome di file legge la url e deposita
   * il contenuto nel file indicato.
   * @param url oggetto URL da leggere
   * @param fWrite oggetto File dove salvare il contenuto
   * @param hll listener di notifica dello stato di avanzamento
   * @throws Exception errori vari
   */
  public static void readUrlToFile(URL url, File fWrite, LongOperListener hll)
     throws Exception
  {
    URLConnection connection = url.openConnection();
    connection.connect();

    // totalLen lunghezza file
    int totalLen = connection.getContentLength();

    try(InputStream is = connection.getInputStream();
       OutputStream os = new FileOutputStream(fWrite))
    {
      if(totalLen == -1)
      {
        // la dimensione totale non è stata passata dal server: impossibile aggiornare hll
        if(hll != null)
          hll.resetUI();

        copyStream(is, os);

        if(hll != null)
          hll.completeUI(1);
      }
      else
        copyStream(is, os, totalLen, hll);
      os.flush();
    }
  }

  /**
   * Data una URL valida e un nome di file legge la url e deposita
   * il contenuto nel file indicato.
   * Durante lo scaricamento viene calcolato l'hash del file trasferito.
   * @param url oggetto URL da leggere
   * @param fWrite oggetto File dove salvare il contenuto
   * @param hashAlgo algoritmo da usare per calcolare l'hash
   * @param hll listener di notifica dello stato di avanzamento
   * @return hash del file
   * @throws Exception errori vari
   */
  public static String readUrlToFileCalcHash(URL url, File fWrite,
     String hashAlgo, LongOperListener hll)
     throws Exception
  {
    URLConnection connection = url.openConnection();
    connection.connect();

    // totalLen lunghezza file
    int totalLen = connection.getContentLength();

    // Obtain a message digest object.
    MessageDigest md = MessageDigest.getInstance(hashAlgo);
    md.reset();

    try(InputStream is = connection.getInputStream();
       OutputStream os = new FileOutputStream(fWrite))
    {
      copyStream(is, os, totalLen, md, hll);
      os.flush();
    }

    return HexString.bufferToHex(md.digest());
  }

  /**
   * Copia tutte le linee in ingresso sul writer di uscita.
   * @param in reader da cui leggere le linee
   * @param out writer su cui scrivere le linee
   * @return numero di linee copiate.
   * @throws java.lang.Exception
   */
  public static int copyAllLines(BufferedReader in, PrintWriter out)
     throws Exception
  {
    int count = 0;
    String sBuffer = null;

    while((sBuffer = in.readLine()) != null)
    {
      out.println(sBuffer);
      count++;
    }

    return count;
  }

  /**
   * Come la sua gemella ma legge il file da disco.
   * @param fileIn file di cui copiare le linee
   * @param encoding tipo di encoding del file da leggere
   * @param out writer su cui scrivere le linee
   * @return numero di linee copiate.
   * @throws java.lang.Exception
   */
  public static int copyAllLines(File fileIn, String encoding, PrintWriter out)
     throws Exception
  {
    int rv;
    try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), encoding)))
    {
      rv = copyAllLines(in, out);
    }
    return rv;
  }

  /**
   * Legge un file da disco sotto forma di un array di bytes.
   * @param fileIn
   * @return
   * @throws java.lang.Exception
   */
  public static byte[] readFile(File fileIn)
     throws Exception
  {
    if(!fileIn.canRead())
      return null;

    int length = (int) fileIn.length();
    byte[] rv = new byte[length];
    try(FileInputStream fis = new FileInputStream(fileIn))
    {
      readStream(rv, fis, 0, length);
    }
    return rv;
  }

  /**
   * Legge un file da disco e lo ritorna nella stringa.
   * @param fileIn file da leggere
   * @param encoding tipo di codifica da applicare al file (null=encoding di default)
   * @return stringa con l'intero file
   * @throws Exception
   */
  public static String readFileTxt(File fileIn, String encoding)
     throws Exception
  {
    byte[] b = readFile(fileIn);
    if(b == null)
      return null;

    return encoding == null ? new String(b) : new String(b, encoding);
  }

  /**
   * Scrive un array di bytes come file su disco.
   * @param fileOut
   * @param arr
   * @throws java.lang.Exception
   */
  public static void writeFile(File fileOut, byte[] arr)
     throws Exception
  {
    writeFile(fileOut, arr, false);
  }

  /**
   * Scrive un array di bytes come file su disco.
   * @param fileOut
   * @param arr
   * @param append
   * @throws java.lang.Exception
   */
  public static void writeFile(File fileOut, byte[] arr, boolean append)
     throws Exception
  {
    try(FileOutputStream fos = new FileOutputStream(fileOut, append))
    {
      fos.write(arr);
      fos.flush();
    }
  }

  /**
   * Verifica se la directory indicata e' scrivibile.
   * NOTA: la directory viene creata se non esiste.
   * @param directory dir da testare
   * @return vero se la scrittura di test e' stata eseguita con successo
   */
  public static boolean checkDirectoryWritable(File directory)
  {
    File fTest = null;

    try
    {
      directory.mkdirs();
      fTest = File.createTempFile("test", ".tmp", directory);
      try(FileOutputStream fos = new FileOutputStream(fTest))
      {
        fos.write("prova\n".getBytes());
      }
      fTest.delete();
      return true;
    }
    catch(Exception e)
    {
      if(fTest != null)
        fTest.delete();
      return false;
    }
  }

  /**
   * Cambia l'estensione al nome di un file.
   * L'estensione si intende separata dal nome vero e proprio da un '.':
   * in-pippo.txt out-pippo.bak
   * @param fileName nome originario
   * @param newExtension nuova estensione desiderata
   * @return il nome con la nuova estensione.
   * @throws Exception
   */
  public static String changeFilenameExtension(String fileName, String newExtension)
     throws Exception
  {
    while(newExtension.startsWith("."))
      newExtension = newExtension.substring(1);

    int pos = fileName.lastIndexOf('.');
    if(pos == -1)
      return fileName + "." + newExtension;

    return fileName.substring(0, pos) + "." + newExtension;
  }

  /**
   * Cambia l'estensione al nome di un file.
   * L'estensione si intende separata dal nome vero e proprio da un '.':
   * in-pippo.txt out-pippo.bak
   * @param origin file originario
   * @param newExtension nuova estensione desiderata
   * @return il file con la nuova estensione.
   * @throws Exception
   */
  public static File changeFileExtension(File origin, String newExtension)
     throws Exception
  {
    return new File(changeFilenameExtension(origin.getAbsolutePath(), newExtension));
  }

  /**
   * Da due path assolute ricava la path relativa:
   * file = "/var/data/stuff/xyz.dat";
   * directory = "/var/data";
   * differenza = "stuff/xyz.dat"
   * @param directory
   * @param file
   * @return
   */
  public static String getRelativePath(File directory, File file)
  {
    return directory.toURI().relativize(file.toURI()).getPath();
  }

  public static void getHttpToStream(String httpURI, OutputStream os, LongOperListener lol)
     throws Exception
  {
    URL testServlet = new URL(httpURI);
    HttpURLConnection conn = (HttpURLConnection) testServlet.openConnection();
    conn.setRequestProperty("connection", "Keep-Alive");
    conn.setRequestMethod("GET");

    // inform the connection that we will send output and accept input
    conn.setDoInput(true);
    conn.setDoOutput(false);

    // Don't use a cached version of URL connection.
    conn.setUseCaches(false);
    conn.setDefaultUseCaches(false);

    // Specify the content type that we will send binary data
    conn.setRequestProperty("Content-Type", "application/binary");
    conn.connect();

    long size = conn.getContentLength();
    try(InputStream isb = conn.getInputStream())
    {
      CommonFileUtils.copyStream(isb, os, size, lol);
    }
  }

  public static String pathFromUID(String uid)
  {
    long val = 0;
    char[] arch = uid.toCharArray();
    for(char c : arch)
    {
      val += (long) c;
    }
    long vv = val % 10000;
    return Long.toString(vv);
  }

  public static String fileNameFromUID(String uid)
  {
    StringBuilder sb = new StringBuilder();
    char[] arch = uid.toCharArray();
    for(char c : arch)
    {
      if(c == '.')
        sb.append('_');
      else
        sb.append(c);
    }
    return sb.toString();
  }

  public static File createTempDir(String prefix, File parent)
     throws IOException
  {
    File ftmp = File.createTempFile(prefix, "", parent);
    File fDir = new File(ftmp.getParentFile(), ftmp.getName() + "_" + System.currentTimeMillis());
    if(!fDir.mkdirs())
      throw new IOException("mkdirs failure");
    ftmp.delete();
    return fDir;
  }

  /**
   * Ritorna la dimensione in byte del contenuto di una directory.
   * @param dir directory da misurare
   * @return spazio occupato in byte
   */
  public static long sizeOfDirectory(File dir)
  {
    long rv = 0;

    if(!dir.isDirectory())
      return dir.length();

    File childs[] = dir.listFiles();
    for(int i = 0; i < childs.length; i++)
    {
      File f = childs[i];
      if(f.isDirectory())
        rv += sizeOfDirectory(f);
      else
        rv += f.length();
    }

    return rv;
  }

  /**
   * Cambia attributi del file.
   * E' equivalente al relativo comando Unix.
   * Vale solo per Unix.
   * @param toChange file da cambiare
   * @param mode modo secondo la regola di ugo
   * @throws Exception
   */
  public static void chmod(File toChange, int mode)
     throws Exception
  {
    String cmd = "/bin/chmod 0" + Integer.toOctalString(mode) + " " + toChange.getAbsolutePath();
    ExecHelper.execUsingShell(cmd);
  }

  /**
   * Scrive un oggetto su disco.
   * Usa l'interfaccia serializzable per scrivere in un file
   * il contenuto dell'oggetto.
   * @param toWrite oggetto da scrivere
   * @param file file destinazione
   * @throws Exception
   */
  public static void writeObjectToFile(Serializable toWrite, File file)
     throws Exception
  {
    try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
    {
      oos.writeObject(toWrite);
    }
  }

  /**
   * Legge un oggetto da disco.
   * Usa l'interfaccia serializable per leggere da un file
   * il contenuto dell'oggetto.
   * @param file file sorgente
   * @return oggetto serializzato
   * @throws Exception
   */
  public static Serializable readObjectFromFile(File file)
     throws Exception
  {
    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
    {
      return (Serializable) ois.readObject();
    }
  }

  /**
   * Scrive un oggetto in un array di bytes.
   * Usa l'interfaccia serializzable per scrivere
   * il contenuto dell'oggetto.
   * @param toWrite oggetto da scrivere
   * @return array di bytes dell'oggetto
   * @throws Exception
   */
  public static byte[] writeObjectToBytes(Serializable toWrite)
     throws Exception
  {
    ByteBufferOutputStream fos = new ByteBufferOutputStream();
    try(ObjectOutputStream oos = new ObjectOutputStream(fos))
    {
      oos.writeObject(toWrite);
    }
    return fos.getBytes();
  }

  /**
   * Legge un oggetto da un array di bytes.
   * Usa l'interfaccia serializable per leggere
   * il contenuto dell'oggetto.
   * @param array array di bytes con l'oggetto
   * @return oggetto serializzato
   * @throws Exception
   */
  public static Serializable readObjectFromBytes(byte[] array)
     throws Exception
  {
    ByteBufferInputStream fis = new ByteBufferInputStream();
    fis.addToBuffer(array);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Object or = null;
    try
    {
      or = ois.readObject();
    }
    finally
    {
      ois.close();
      fis.close();
    }
    return (Serializable) or;
  }

  /**
   * Chiusura di uno stream senza sollevamento di eccezione.
   * Controllo che il puntatore non sia nullo e in tal
   * caso chiude esegue il metodo close(). Le eccezioni
   * eventuali vengono ignorate.
   * @param cl stream da chiudere
   */
  public static void safeClose(Closeable cl)
  {
    try
    {
      if(cl != null)
        cl.close();
    }
    catch(Exception e)
    {
      // eccezione ignorata
    }
  }

  /**
   * Join su thread senza sollevamento eccezione.
   * @param t thread su cui fare join
   * @param millis timeout in millisecondi (0 = infinito)
   * @return vero se uscita per interruzione
   */
  public static boolean safeJoin(Thread t, long millis)
  {
    try
    {
      if(t != null)
        t.join(millis);
      return false;
    }
    catch(InterruptedException ex)
    {
      // eccezione ignorata
      return true;
    }
  }

  /**
   * Sleep senza sollevamento eccezione.
   * @param millis timeout in millisecondi (0 = infinito)
   * @return vero se uscita per interruzione
   */
  public static boolean safeSleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
      return false;
    }
    catch(InterruptedException ex)
    {
      // eccezione ignorata
      return true;
    }
  }

  /**
   * Ritorna estensione di un file.
   * Per estensione si intende l'ultima parte del nome
   * file dopo l'ultimo punto. Se il punto non è presente
   * nel nome (non c'è estensione) viene tornato null.
   * @param fileName nome del file (anche path completa)
   * @return la sola estensione
   */
  public static String getFileExtension(String fileName)
  {
    int i = fileName.lastIndexOf('.');
    return i == -1 ? null : fileName.substring(i + 1);
  }

  /**
   * Ritorna nome di file senza estensione.
   * Per estensione si intende l'ultima parte del nome
   * file dopo l'ultimo punto. Se il punto non è presente
   * nel nome (non c'è estensione) viene tornato null.
   * @param fileName nome del file (anche path completa)
   * @return il solo nome del file
   */
  public static String getNameWithoutExtension(String fileName)
  {
    int i = fileName.lastIndexOf('.');
    return i == -1 ? null : fileName.substring(0, i);
  }

  /**
   * Ritorna estensione di un file.
   * Per estensione si intende l'ultima parte del nome
   * file dopo l'ultimo punto. Se il punto non è presente
   * nel nome (non c'è estensione) viene tornato null.
   * @param f file da analizzare
   * @return la sola estensione
   */
  public static String getFileExtension(File f)
  {
    return getFileExtension(f.getName());
  }

  /**
   * Fonde files di testo.
   * L'eventuale separatore viene inserito solo fra i files (non in coda).
   * @param inputFiles lista di files da fondere
   * @param encoding encoding da applicare nella lettura files
   * @param out print writer per l'output della fusione
   * @param separator eventuale separatore fra i files (può essere null)
   * @throws Exception
   */
  public static void mergeFileTxt(List<File> inputFiles, String encoding, PrintWriter out, String separator)
     throws Exception
  {
    for(int i = 0; i < inputFiles.size(); i++)
    {
      File f = inputFiles.get(i);

      copyAllLines(f, encoding, out);

      if(separator != null && i < (inputFiles.size() - 1))
        out.print(separator);
    }
  }

  /**
   * Fonde files binari.
   * L'eventuale separatore viene inserito solo fra i files (non in coda).
   * @param inputFiles lista di files da fondere
   * @param os stream per l'output della fusione
   * @param separator eventuale separatore fra i files (può essere null)
   * @throws Exception
   */
  public static void mergeFileBin(List<File> inputFiles, OutputStream os, byte[] separator)
     throws Exception
  {
    for(int i = 0; i < inputFiles.size(); i++)
    {
      File f = inputFiles.get(i);

      try(FileInputStream is = new FileInputStream(f))
      {
        copyStream(is, os);
      }

      if(separator != null && i < (inputFiles.size() - 1))
        os.write(separator);
    }
  }

  /**
   * Verifica per ultima scrittura su file.
   * @param toTest file da osservare
   * @param millisTimeout millisecondi di timeout
   * @return vero se il file esiste e non è stato modificato negli ultimi millisTimeout millisecondi
   */
  public static boolean isOlderThan(File toTest, long millisTimeout)
  {
    return toTest.exists() && (System.currentTimeMillis() - toTest.lastModified()) > millisTimeout;
  }

  /**
   * Simile all'utility grep di Unix.
   * Una versione semplificata adatta a semplici ricerche di contenimento è 'findStringInFile'.
   * @param asciiFileName file da interrogare
   * @param encoding encoding del file da leggere
   * @param p pattern compilata con la regex da trovare (null=tutte le linee)
   * @return le linee che onorano la regular expression
   * @throws java.lang.Exception
   */
  public static String[] grep(File asciiFileName, String encoding, Pattern p)
     throws Exception
  {
    ArrayList<String> arRv = new ArrayList<String>();

    try(FileInputStream fis = new FileInputStream(asciiFileName);
       BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding)))
    {
      grep(br, p, arRv);
    }

    return StringOper.toArray(arRv);
  }

  /**
   * Simile all'utility grep di Unix.
   * @param br Reader da cui leggere le linee di testo
   * @param p pattern compilata con la regex da trovare (null=tutte le linee)
   * @param arRv popolato con le linee che onorano la regular expression
   * @return numero di linee lette dal reader
   * @throws Exception
   */
  public static int grep(BufferedReader br, Pattern p, List<String> arRv)
     throws Exception
  {
    int count = 0;
    String linea;
    while((linea = br.readLine()) != null)
    {
      count++;

      if(p == null)
      {
        arRv.add(linea);
        continue;
      }

      Matcher m = p.matcher(linea);
      if(m.find())
      {
        if(m.groupCount() > 0)
        {
          for(int i = 0; i < m.groupCount(); i++)
          {
            arRv.add(m.group(i + 1));
          }
        }
        else
        {
          arRv.add(linea);
        }
      }
    }

    return count;
  }

  /**
   * Simile all'utility grep di Unix.
   * @param url oggetto URL da leggere
   * @param p pattern compilata con la regex da trovare (null=tutte le linee)
   * @return le linee che onorano la regular expression
   * @throws java.lang.Exception
   */
  public static String[] grep(URL url, Pattern p)
     throws Exception
  {
    URLConnection connection = url.openConnection();
    connection.connect();

    String enc = connection.getContentEncoding();
    if(enc == null)
      enc = "UTF-8";

    ArrayList<String> arRv = new ArrayList<String>();

    try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), enc)))
    {
      grep(br, p, arRv);
    }

    return StringOper.toArray(arRv);
  }

  /**
   * Ricerca di pattern in uno stream.
   * Lo stream viene letto fino a trovare la pattern indicata.
   * Se la pattern viene trovata la posizione dello stream sarà al byte immediatamente successivo.
   * @param pattern sequenza di byte da trovare
   * @param is stream da leggere
   * @return vero se la pattern è stata trovata
   * @throws IOException
   */
  public static boolean skipPattern(byte[] pattern, InputStream is)
     throws IOException
  {
    int i = 0, c = 0;

    do
    {
      for(i = 0; i < pattern.length; i++)
      {
        if((c = is.read()) == -1)
          return false;

        if(c != pattern[i])
          break;
      }
    }
    while(i < pattern.length);

    return true;
  }

  /**
   * Ricerca di pattern in uno stream con riposizionamento.
   * Lo stream viene letto fino a trovare la pattern indicata.
   * Se la pattern viene trovata la posizione dello stream sarà al byte di inizio della pattern.
   * Simile a skipPattern() ma qui lo stream punta all'inizio della pattern e non alla fine.
   * @param pattern sequenza di byte da trovare
   * @param is stream da leggere (necessario un pushback stream per reinserire la pattern)
   * @return vero se la pattern è stata trovata
   * @throws IOException
   */
  public static boolean skipPatternRepos(byte[] pattern, PushbackInputStream is)
     throws IOException
  {
    if(skipPattern(pattern, is))
    {
      is.unread(pattern);
      return true;
    }
    return false;
  }
}
