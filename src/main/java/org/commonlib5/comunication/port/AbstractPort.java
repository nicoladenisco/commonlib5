/* 
 * Copyright (C) 2025 Nicola De Nisco
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.commonlib5.comunication.port;

import java.io.*;
import org.commonlib5.comunication.WaitCharException;
import org.commonlib5.io.MonitorInputStream;
import org.commonlib5.io.MonitorOutputStream;
import org.commonlib5.io.MonitorStreamQueue;
import org.commonlib5.io.MonitorStreamStorage;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.SimpleTimer;

/**
 * Porta astratta di comunicazione.
 * Questa classe implementa le funzionalità di base delle porte
 * di comunicazione seriale. Le sue derivate consentono di aprire
 * e lavorare su porte reali.
 *
 * @author Nicola De Nisco
 */
abstract public class AbstractPort
{
  protected String sPorta = "/dev/ttyS0";
  /** Stream di input dalla porta. */
  protected PushbackInputStream inputStream;
  /** Stream i output verso la porta. */
  protected OutputStream outputStream;
  /** Timeout in millisecondi per Wait... */
  protected int timeout = 10000;
  /** Dimensioni del buffer di pushback dell'InputStream. */
  protected int pushBackSize = 4096;
  /**
   * Flag per l'uso dei monitors:
   * 0=monitor disattivati
   * 1=monitor attivati con coda condivisa
   * 2=monitor attivati con code separate
   * 3=monitor attivati con code custom separate
   */
  protected int useMonitors = 0;
  /** Coda per monitoraggio input. */
  protected MonitorStreamStorage inputQueue = null;
  /** Coda per monitoraggio output. */
  protected MonitorStreamStorage outputQueue = null;

  /**
   * Costruttore vuoto protetto.
   * Questo oggetto deve necessariamente
   * essere derivato.
   */
  protected AbstractPort()
  {
  }

  /**
   * Inizializzazione della porta.
   * I parametri sono stati già impostati; qui
   * la porta seriale viene aperta e gli stream
   * collegati opportunamete.
   * @param sData nome dell'applicazione che sta aprendo la porta
   * @throws Exception
   */
  public void initComm(String sData)
     throws Exception
  {
  }

  /**
   * Chiusura delle comunicazioni.
   * Gli stream e le porte sono chiusi.
   */
  public void closeComm()
  {
    if(inputStream != null)
    {
      CommonFileUtils.safeClose(inputStream);
      inputStream = null;
    }

    if(outputStream != null)
    {
      CommonFileUtils.safeClose(outputStream);
      outputStream = null;
    }
  }

  /**
   * Recupera la porta di comunicazione.
   * @return nome della porta o null se non impostata
   */
  public String getPorta()
  {
    return sPorta;
  }

  /**
   * Imposta la porta di comunicazione.
   * Il nome della porta dipende dalla classe derivata.
   * Se il canale è già aperto viene sollevata IllegalStateException.
   * @param sPorta nome della porta
   */
  public void setPorta(String sPorta)
  {
//    if(isOpen())
//      throw new IllegalStateException("Port is open.");

    this.sPorta = sPorta;
  }

  /**
   * Imposta gli stream.
   * Solo per classi derivate.
   * In base al tipo di connessione le classi derivate
   * chiamano questa funzione per impostare gli stream
   * associati al canale di comunicazione implementato.
   * @param is stream di input
   * @param os stream di output
   */
  protected void setStream(InputStream is, OutputStream os)
  {
    switch(useMonitors)
    {
      case 0:
      default:
        // nessun monitor impostato
        inputStream = new PushbackInputStream(is, pushBackSize);
        outputStream = os;
        break;
      case 1:
        // monitor con coda unica condivisa
        MonitorStreamQueue mq = new MonitorStreamQueue();
        inputStream = new PushbackInputStream(new MonitorInputStream(is, mq), pushBackSize);
        outputStream = new MonitorOutputStream(os, mq);
        inputQueue = outputQueue = mq;
        break;
      case 2:
        // monitor con code separate
        MonitorInputStream mis = new MonitorInputStream(is);
        MonitorOutputStream mos = new MonitorOutputStream(os);
        inputStream = new PushbackInputStream(mis, pushBackSize);
        outputStream = mos;
        inputQueue = mis.getQueue();
        outputQueue = mos.getQueue();
        break;
      case 3:
        // monitor con code separate custom
        inputStream = new PushbackInputStream(new MonitorInputStream(is, inputQueue), pushBackSize);
        outputStream = new MonitorOutputStream(os, outputQueue);
        break;
    }
  }

  /**
   * Recupera stream di lettura dalla porta.
   * @return
   */
  public InputStream getInputStream()
  {
    return inputStream;
  }

  /**
   * Recupera stream di scrittura sulla porta.
   * @return
   */
  public OutputStream getOutputStream()
  {
    return outputStream;
  }

  /**
   * Stato della porta.
   * @return vero se aperta
   */
  public boolean isOpen()
  {
    return inputStream != null && outputStream != null;
  }

  /**
   * Timeout per attesa caratteri.
   * @return numero di secondi di timeout
   */
  final public int getTimeout()
  {
    return timeout;
  }

  /**
   * Timeout per attesa caratteri.
   * @param timeoutSecs numero di secondi di timeout
   */
  final public void setTimeoutSecs(int timeoutSecs)
  {
    setTimeoutMillis(timeoutSecs * 1000);
  }

  /**
   * Timeout per attesa caratteri.
   * @param millis numero di millisecondi di timeout
   */
  public void setTimeoutMillis(int millis)
  {
    timeout = millis;
  }

  /**
   * Flag per l'uso dei monitor.
   * Quando attivo questo flag determina,
   * al momomento della chiamata del motodo open(),
   * la creazione di MonitorInputStream e MonitorOutputStream
   * utilizzati come wrapper degli stream originali.
   * Questo consente un monitoraggio del traffico.
   * ATTENZIONE: gli stream sono bloccanti; attivare solo se necessario.
   * @return stato del flag
   */
  public int getUseMonitors()
  {
    return useMonitors;
  }

  /**
   * Imposta lo stato del flag monitor.
   * <code>
   * 0=monitor disattivati
   * 1=monitor attivati con coda condivisa
   * 2=monitor attivati con code separate
   * 3=monitor attivati con code custom separate
   * </code>
   * @param useMonitors nuovo stato del flag
   */
  public void setUseMonitors(int useMonitors)
  {
    this.useMonitors = useMonitors;
  }

  /**
   * Ritorna la dimensione del buffer di pushback associato all'InputStrem.
   * @return dimensione in bytes
   */
  public int getPushBackSize()
  {
    return pushBackSize;
  }

  /**
   * Imposta la dimensione del buffer di pushback associato all'InputStrem.
   * @param pushBackSize dimensione in bytes
   */
  public void setPushBackSize(int pushBackSize)
  {
    this.pushBackSize = pushBackSize;
  }

  /**
   * Recupera monitor per input.
   * @return coda associata ad input stream.
   */
  public MonitorStreamStorage getInputQueue()
  {
    return inputQueue;
  }

  /**
   * Imposta i monitor custom.
   * Usando il tipo monitor 3 in setUseMonitors() al momento della
   * initComm() vengono attaccati i monitor custom agli stream di IO.
   * E' possibile passare lo stesso monitor sia per l'input che per
   * l'output.
   * @param inputQueue monitor per lo stream di input
   */
  public void setInputQueue(MonitorStreamStorage inputQueue)
  {
    this.inputQueue = inputQueue;
  }

  /**
   * Recuper monitor per output.
   * @return coda associata ad output stream.
   */
  public MonitorStreamStorage getOutputQueue()
  {
    return outputQueue;
  }

  /**
   * Imposta i monitor custom.
   * Usando il tipo monitor 3 in setUseMonitors() al momento della
   * initComm() vengono attaccati i monitor custom agli stream di IO.
   * E' possibile passare lo stesso monitor sia per l'input che per
   * l'output.
   * @param outputQueue monitor per lo stream di output
   */
  public void setOutputQueue(MonitorStreamStorage outputQueue)
  {
    this.outputQueue = outputQueue;
  }

  /**
   * Finalizzazione: chiude le comunicazioni se necessario.
   * Se le comunicazioni sono ancora attive, forza una
   * chiusura della porta.
   * @throws java.lang.Throwable
   */
  @Override
  protected void finalize()
     throws java.lang.Throwable
  {
    closeComm();
    super.finalize();
  }

  /**
   * Attende riempimento del buffer della seriale.
   * L'attesa dura timeout millisecondi
   * @param numChar numero di bytes da attendere nel buffer
   * @throws WaitCharException in caso di timeout
   */
  public void waitRxCount(int numChar)
     throws Exception
  {
    int numAvail = getRxAvail();
    if(numAvail >= numChar)
      return;

    int tw = timeout / 10;
    for(int i = 0; i < tw && numAvail < numChar; i++)
    {
      Thread.sleep(10);
      numAvail = getRxAvail();
    }

    if(numAvail < numChar)
      throw new WaitCharException("WaitRxCount(" + numChar + "): timeout attesa risposta.");
  }

  /**
   * Attende riempimento del buffer della seriale.
   * @param numChar numero di bytes da attendere nel buffer
   * @param timeoutMillis millisecondi da attendere
   * @return vero se ci sono i caratteri attesi nel buffer
   * @throws Exception in caso di errore IO
   */
  public boolean waitRxCount(int numChar, long timeoutMillis)
     throws Exception
  {
    int numAvail = getRxAvail();
    if(numAvail >= numChar)
      return true;

    SimpleTimer st = new SimpleTimer();
    while(!st.isElapsed(timeoutMillis))
    {
      Thread.sleep(10);
      if((numAvail = getRxAvail()) >= numChar)
        return true;
    }

    return false;
  }

  /**
   * Ricerca di pattern nei byte ricevuti dalla seriale.
   * Lo stream viene letto fino a trovare la pattern indicata.
   * Se la pattern viene trovata la posizione dello stream sarà al byte immediatamente successivo.
   * La chiamata è bloccante.
   * @param pattern sequenza di byte da cercare
   * @return vero se la pattern è stata trovata
   * @throws IOException
   */
  public boolean skipPattern(byte[] pattern)
     throws IOException
  {
    int i = 0, c = 0;

    do
    {
      for(i = 0; i < pattern.length; i++)
      {
        if((c = inputStream.read()) == -1)
          return false;

        if(c != pattern[i])
          break;
      }
    }
    while(i < pattern.length);

    return true;
  }

  /**
   * Attende l'arrivo di uno specifico byte sulla porta seriale.
   * Tutti i byte sono scartati.
   * @param charWait byte da attendere
   * @return numero di caratteri scartati
   * @throws java.lang.Exception
   */
  public int waitForChar(int charWait)
     throws Exception
  {
    int count = 0;
    while(getRxCharBlocking() != charWait)
      count++;
    return count;
  }

  /**
   * Attende l'arrivo di uno specifico byte sulla porta seriale.
   * Tutti i byte tranne charWait sono accumulati nel buffer
   * per essere utilizzati.
   * Encoding ASCII.
   * @param charWait byte da attendere
   * @param sb accumulatore dei caratteri
   * @return numero di caratteri letti
   * @throws java.lang.Exception
   */
  public int waitForChar(int charWait, StringBuilder sb)
     throws Exception
  {
    int c;
    while((c = getRxCharBlocking()) != charWait)
      sb.append((char) c);
    return sb.length();
  }

  /**
   * Attende l'arrivo di uno specifico byte sulla porta seriale.
   * Tutti i caratteri compreso charWait sono accumulati nello stream
   * per essere utilizzati.
   * Eventuali caratteri successivi a charWait presenti nel buffer
   * sono reinviati allo stream di input per essere letti nelle
   * read successive.
   * La chiamata può essere bloccante.
   * @param charWait byte da attendere
   * @param buffer buffer di servizio per accumulo dati temporanei
   * @param bb stream accumulatore dei caratteri
   * @return numero di caratteri letti
   * @throws java.lang.Exception
   */
  public int waitForChar(int charWait, byte[] buffer, OutputStream bb)
     throws Exception
  {
    int c, k, nb, count = 0;
    while(true)
    {
      // se lo stream è vuoto gli da l'opportunità di riempirsi un po
      if(inputStream.available() == 0)
        Thread.sleep(100);

      // se c'è almeno un carattere nello stream questa non sarà bloccante
      if((nb = inputStream.read(buffer)) <= 0)
        break;

      k = -1;
      for(c = 0; c < nb; c++)
      {
        if(buffer[c] == charWait)
        {
          k = c + 1;
          break;
        }
      }

      if(k == -1)
      {
        // terminatore non trovato: salva nel buffer e continua ascolto
        bb.write(buffer, 0, nb);
        count += nb;
      }
      else
      {
        // terminatore trovato: salva nel buffer ed esce dal ciclo
        bb.write(buffer, 0, k);

        // pushback dei caratteri in eccedenza
        if(k < nb)
          inputStream.unread(buffer, k, nb - k);

        count += k;
        break;
      }
    }

    return count;
  }

  /**
   * Attende l'arrivo di uno specifico byte sulla porta seriale.
   * Tutti i caratteri letti sono accumulati nel buffer.
   * Eventuali caratteri successivi a charWait presenti nel buffer
   * sono reinviati allo stream di input per essere letti nelle
   * read successive.
   * La chiamata può essere bloccante.
   * @param charWait byte da attendere
   * @param buffer accumulo dei caratteri
   * @param offset indice all'interno di buffer dove depositare i dati
   * @param len lunghezza massima di buffer a disposizione per la ricezione
   * @return posizione all'interno di buffer di charWait + 1 oppure numero di caratteri letti
   * se non è stato trovato charWait e lo stream di input è stato chiuso.
   * @throws java.lang.Exception
   */
  public int waitForChar(int charWait, byte[] buffer, int offset, int len)
     throws Exception
  {
    int c, nb;

    while(true)
    {
      // se lo stream è vuoto gli da l'opportunità di riempirsi un po
      if(inputStream.available() == 0)
        Thread.sleep(100);

      // questo serve a fare il primo giro solo con i caratteri
      // di pushback dello stream, senza provocare il blocco dello stream reale
      int lenWait = len;
      if(inputStream.available() != 0)
        lenWait = inputStream.available();

      // preleva dallo stream
      if((nb = inputStream.read(buffer, offset, lenWait)) <= 0)
        break;

      for(c = 0; c < nb; c++)
      {
        if(buffer[offset + c] == charWait)
        {
          // pushback dei caratteri eccedenti
          if(++c < nb)
            inputStream.unread(buffer, offset + c, nb - c);

          return offset + c;
        }
      }

      offset += nb;
      len -= nb;
    }

    return offset;
  }

  /**
   * Scarica il buffer di ricezione della seriale.
   * Tutti i caratteri contenuti nel buffer sono distrutti.
   * @return numero di caratteri distrutti
   * @throws java.lang.Exception
   */
  public int flushRX()
     throws Exception
  {
    int chFlushed = 0, avail = 0;

    do
    {
      while((avail = inputStream.available()) > 0)
      {
        chFlushed += inputStream.skip(avail);
      }

      Thread.sleep(10);
    }
    while(inputStream.available() > 0);

    return chFlushed;
  }

  /**
   * Scarica il buffer di ricezione della seriale.
   * Attende per nChar caratteri nel buffer e quindi li elimina dal buffer.
   * @param nChar numero di attesa
   * @return numero di caratteri distrutti
   * @throws java.lang.Exception
   */
  public int flushRX(int nChar)
     throws Exception
  {
    waitRxCount(nChar);
    return flushRX();
  }

  /**
   * Attende che tutti i caratteri nel buffer di trasmissione
   * vengano realmente spediti attraverso la porta seriale.
   * @throws Exception
   */
  public void flushTX()
     throws Exception
  {
    outputStream.flush();
  }

  /**
   * Ritorna numero di caratteri disponibili nel buffer di ricezione.
   * @return numero di caratteri
   * @throws java.lang.Exception
   */
  public int getRxAvail()
     throws Exception
  {
    return inputStream.available();
  }

  /**
   * Ritorna il primo byte disponibile sulla porta seriale.
   * Questa chiamata può essere bloccante.
   * @return byte letto
   * @throws java.io.IOException
   */
  public int getRxCharBlocking()
     throws IOException
  {
    return inputStream.read();
  }

  /**
   * Ritorna i caratteri sotto forma di stringa.
   * Il contenuto attuale del buffer viene ritornato sotto forma di stringa.
   * Encoding di default del sistema ospite.
   * @return stringa
   * @throws java.lang.Exception
   */
  public String getRxString()
     throws Exception
  {
    byte[] buffer = new byte[4096];
    int nb = inputStream.read(buffer, 0, buffer.length);
    return new String(buffer, 0, nb);
  }

  /**
   * Ritorna i caratteri sotto forma di stringa.
   * Il contenuto attuale del buffer viene ritornato sotto forma di stringa.
   * @param encoding il tipo di encoding da usare
   * @return stringa
   * @throws java.lang.Exception
   */
  public String getRxString(String encoding)
     throws Exception
  {
    byte[] buffer = new byte[4096];
    int nb = inputStream.read(buffer, 0, buffer.length);
    return new String(buffer, 0, nb, encoding);
  }

  /**
   * Ritorna il primo byte disponibile sulla porta seriale con attesa.
   * Internamente viene chiamata waitRxCount(1) per attendere il byte che viene poi tornato.
   * @return il byte letto
   * @exception WaitCharException per il timeout
   */
  public int getRxChar()
     throws Exception
  {
    waitRxCount(1);
    return getRxCharBlocking();
  }

  /**
   * Ritorna i caratteri sotto forma di stringa.
   * Attende per nChar byte nel buffer e comunque
   * qualsiasi sia il numero di caratteri nel buffer torna
   * solo quelli indicati da nChar.
   * Viene usato l'encoding di default del sistema.
   * @param nChar numero di byte da attendere
   * @return un array di byte con i caratteri letti
   * @exception WaitCharException per il timeout
   */
  public String getRxString(int nChar)
     throws Exception
  {
    waitRxCount(nChar);
    byte b[] = new byte[nChar];
    getRxArray(b);
    return new String(b);
  }

  /**
   * Ritorna i caratteri attualmente nel buffer di ricezione sotto forma di array.
   * Se non ci sono caratteri disponibili ritorna null.
   * La chiamata non è bloccante.
   * @return array di byte ricevuti
   * @throws java.io.IOException
   */
  public byte[] getRxArray()
     throws IOException
  {
    int numBytes = inputStream.available();
    if(numBytes > 0)
    {
      byte[] rv = new byte[numBytes];
      inputStream.read(rv);
      return rv;
    }
    return null;
  }

  /**
   * Ritorna i caratteri sotto forma di array.
   * Legge il contenuto del buffer nell'array indicato.
   * La chiamata non è bloccante.
   * @param b array di bytes da leggere
   * @return numero di bytes effettivamente letti
   * @throws IOException
   */
  public int getRxArray(byte[] b)
     throws IOException
  {
    return getRxArray(b, 0, b.length);
  }

  /**
   * Ritorna i caratteri sotto forma di array.
   * Legge il contenuto del buffer nell'array indicato.
   * La chiamata non è bloccante.
   * @param b array di bytes da leggere
   * @param ofs primo byte disponibile nel buffer
   * @param len numero massimo di byte da leggere
   * @return numero di bytes effettivamente letti
   * @throws IOException
   */
  public int getRxArray(byte[] b, int ofs, int len)
     throws IOException
  {
    if(inputStream.available() > 0)
      return inputStream.read(b, ofs, len);
    return 0;
  }

  /**
   * Ritorna i caratteri sotto forma di array.
   * Legge il contenuto del buffer nell'array indicato.
   * La chiamata è bloccante.
   * @param b array di bytes da leggere
   * @param ofs primo byte disponibile nel buffer
   * @param len numero massimo di byte da leggere
   * @return numero di bytes effettivamente letti
   * @throws IOException
   */
  public int getRxArrayBlocking(byte[] b, int ofs, int len)
     throws IOException
  {
    return inputStream.read(b, ofs, len);
  }

  /**
   * Invia un singolo byte alla porta seriale.
   * @param c byte da spedire
   * @throws Exception
   */
  public void putTxChar(int c)
     throws Exception
  {
    outputStream.write(c);
  }

  /**
   * Invia la stringa alla porta seriale.
   * Viene usato l'encoding di default del sistema ospite.
   * @param messageString stringa da trasmettere
   * @throws Exception
   */
  public void putTxString(String messageString)
     throws Exception
  {
    outputStream.write(messageString.getBytes());
  }

  /**
   * Invia la stringa alla porta seriale.
   * @param messageString stringa da trasmettere
   * @param encoding il tipo di encoding da usare
   * @throws Exception
   */
  public void putTxString(String messageString, String encoding)
     throws Exception
  {
    outputStream.write(messageString.getBytes(encoding));
  }

  /**
   * Invia l'array di bytes alla porta seriale.
   * @param arByte caratteri da spedire
   * @throws Exception
   */
  public void putTxBuffer(byte[] arByte)
     throws Exception
  {
    outputStream.write(arByte);
  }

  /**
   * Invia l'array di bytes alla porta seriale.
   * @param b caratteri da spedire
   * @param off offset del primo byte da spedire all'interno dell'array
   * @param len numero di byte da spedire
   * @throws Exception
   */
  public void putTxBuffer(byte[] b, int off, int len)
     throws Exception
  {
    outputStream.write(b, off, len);
  }

  /**
   * Pushback di un singolo byte allo stream di input.
   * @param c byte da spedire
   * @throws Exception
   */
  public void ungetRxChar(int c)
     throws Exception
  {
    inputStream.unread(c);
  }

  /**
   * Pushback della stringa allo stream di input.
   * Viene usato l'encoding di default del sistema ospite.
   * @param messageString stringa da trasmettere
   * @throws Exception
   */
  public void ungetRxString(String messageString)
     throws Exception
  {
    inputStream.unread(messageString.getBytes());
  }

  /**
   * Pushback della stringa allo stream di input.
   * Viene usato l'encoding specificato
   * @param messageString stringa da trasmettere
   * @param encoding encoding per la conversione
   * @throws Exception
   */
  public void ungetRxString(String messageString, String encoding)
     throws Exception
  {
    inputStream.unread(messageString.getBytes(encoding));
  }

  /**
   * Pushback dell'array di bytes allo stream di input.
   * @param arByte caratteri da spedire
   * @throws Exception
   */
  public void ungetRxBuffer(byte[] arByte)
     throws Exception
  {
    inputStream.unread(arByte);
  }

  /**
   * Pushback dell'array di bytes allo stream di input.
   * @param b caratteri da spedire
   * @param off offset del primo byte da spedire all'interno dell'array
   * @param len numero di byte da spedire
   * @throws Exception
   */
  public void ungetRxBuffer(byte[] b, int off, int len)
     throws Exception
  {
    inputStream.unread(b, off, len);
  }
}
