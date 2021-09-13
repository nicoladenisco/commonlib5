/*
 * RemoteErrorException.java
 *
 * Created on 2-dic-2011, 11.18.09
 *
 * Copyright (C) 2011 WinSOFT di Nicola De Nisco
 *
 * Questo software è proprietà di Nicola De Nisco.
 * I termini di ridistribuzione possono variare in base
 * al tipo di contratto in essere fra Nicola De Nisco e
 * il fruitore dello stesso.
 *
 * Fare riferimento alla documentazione associata al contratto
 * di committenza per ulteriori dettagli.
 */
package org.commonlib5.xmlrpc;

/**
 * Eccezione sollevata quando l'errore avviene nel server
 * e viene segnalato attraverso la restitituzione di un
 * oggetto XmlRpcException al client.
 *
 * @author Nicola De Nisco
 */
public class RemoteErrorException extends Exception
{
  public RemoteErrorException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public RemoteErrorException(String message)
  {
    super(message);
  }
}
