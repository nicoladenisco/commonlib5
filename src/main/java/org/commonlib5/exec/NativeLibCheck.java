/*
 * Copyright (C) 2013 Nicola De Nisco
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
package org.commonlib5.exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.commonlib5.nmath.BitUtils;
import org.commonlib5.utils.OsIdent;
import org.commonlib5.utils.StringOper;

/**
 * Effettua verifiche di compatibilità su librerie native.
 * Su piattaforme Unix viene utilizzata l'utility 'file',
 * richiamata attraverso la shell, per verificare il tipo
 * di eseguibile. Per Windows viene letto il file eseguibile
 * e verificati gli header alla ricerca dei descrittori di
 * architettura.
 * ATTENZIONE: su MacOSX la libreria potrebbe contemporaneamente
 * essere utilizzata sia a 32 che 64 bit. Su questa piattaforma
 * possono esistere librerie native con più immagini eseguibili
 * all'interno (non solo per intel). In questo caso sia is32bit()
 * che is64bit() ritornano vero se la libreria è in grado di
 * supportare l'architettura testata.
 *
 * @author Nicola De Nisco
 */
public class NativeLibCheck
{
  public static final String[] token32 =
  {
    "32-bit", "i386", "i586"
  };
  public static final String[] token64 =
  {
    "64-bit", "x86-64", "x86_64", "amd64"
  };

  public static final int WINDOWS_EXE_I386 = 0x014c;
  public static final int WINDOWS_EXE_IA64 = 0x0200;
  public static final int WINDOWS_EXE_AMD64 = 0x8664;

  /**
   * Ritorna vero se la libreria nativa è compatibile
   * con architetture a 64 bit.
   * @param nativeLib libreria nativa da testare
   * @return vero se può funzionare con vm a 64bit
   * @throws IOException
   */
  public static boolean is64bit(File nativeLib)
     throws IOException
  {
    switch(OsIdent.checkOStype())
    {
      case OsIdent.OS_WINDOWS:
      {
        int idExe = getWindowsExecutableDescriptor(nativeLib);
        return idExe == WINDOWS_EXE_AMD64 || idExe == WINDOWS_EXE_IA64;
      }
      case OsIdent.OS_LINUX:
      case OsIdent.OS_MACOSX:
      case OsIdent.OS_SOLARIS:
      case OsIdent.OS_FREEBSD:
      {
        ExecHelper eh = ExecHelper.execUsingShell("file -L \"" + nativeLib.getAbsolutePath() + "\"");
        String ehOut = eh.getOutput();
        if(ehOut != null && StringOper.testTokens(token64, ehOut) > 0)
          return true;
        break;
      }
    }

    return false;
  }

  /**
   * Ritorna vero se la libreria nativa è compatibile
   * con architetture a 32 bit.
   * @param nativeLib libreria nativa da testare
   * @return vero se può funzionare con vm a 32bit
   * @throws IOException
   */
  public static boolean is32bit(File nativeLib)
     throws IOException
  {
    switch(OsIdent.checkOStype())
    {
      case OsIdent.OS_WINDOWS:
      {
        int idExe = getWindowsExecutableDescriptor(nativeLib);
        return idExe == WINDOWS_EXE_I386;
      }
      case OsIdent.OS_LINUX:
      case OsIdent.OS_MACOSX:
      case OsIdent.OS_SOLARIS:
      {
        ExecHelper eh = ExecHelper.execUsingShell("file -L " + nativeLib.getAbsolutePath());
        String ehOut = eh.getOutput();
        if(ehOut != null && StringOper.testTokens(token32, ehOut) > 0)
          return true;
        break;
      }
    }

    return false;
  }

  /**
   * Legge un file eseguibile Windows e restituisce il descrittore
   * macchina ovvero il tipo di piattaforma per cui è stato compilato
   * l'eseguibile (o la dll).
   * Il valore di ritorno è essere una delle costanti WINDOWS_EXE_...
   * @param nativeLib libreria o eseguibile da testare
   * @return tipo di architettura supportata
   * @throws IOException
   */
  public static int getWindowsExecutableDescriptor(File nativeLib)
     throws IOException
  {
    int libID = 0;
    try (FileInputStream is = new FileInputStream(nativeLib))
    {
      FileChannel ch = is.getChannel();

      // carica MS-DOS header
      ByteBuffer doshdr = ByteBuffer.allocate(68);
      if(ch.read(doshdr) != 68)
        throw new IOException("Invalid nativelib file.");

      // verifica che sia un eseguibile
      byte[] dh = doshdr.array();
      if(dh[0] != 'M' || dh[1] != 'Z')
        throw new IOException("The native library is not an executable file.");

      // legge offset header PE e seek del file
      long offset = BitUtils.getDWordValue(dh, 60);
      ch.position(offset);

      // carica IMAGE_NT_HEADERS (primi 6 byte sono sufficienti)
      ByteBuffer pehdr = ByteBuffer.allocate(6);
      if(ch.read(pehdr) != 6)
        throw new IOException("Invalid nativelib file.");

      // verifica che sia un header PE
      byte[] pe = pehdr.array();
      if(pe[0] != 'P' || pe[1] != 'E')
        throw new IOException("Native library is not in PE format.");

      // legge il descrittore del tipo macchina
      libID = BitUtils.getWordValue(pe, 4);
    }
    catch(Exception e)
    {
      if(e instanceof IOException)
        throw (IOException) e;
      else
        throw new IOException("Cannot read native library file.", e);
    }

    return libID;

//-- codice PERL originale --
//open(EXE, $exe) or die "can't open $exe: $!";
//binmode(EXE);
//if (read(EXE, $doshdr, 68)) {
//
//   ($magic,$skip,$offset)=unpack('a2a58l', $doshdr);
//   die("Not an executable") if ($magic ne 'MZ');
//
//   seek(EXE,$offset,SEEK_SET);
//   if (read(EXE, $pehdr, 6)){
//       ($sig,$skip,$machine)=unpack('a2a2v', $pehdr);
//       die("No a PE Executable") if ($sig ne 'PE');
//
//       if ($machine == 0x014c){
//            print "i386\n";
//       }
//       elsif ($machine == 0x0200){
//            print "IA64\n";
//       }
//       elsif ($machine == 0x8664){
//            print "AMD64\n";
//       }
//       else{
//            printf("Unknown machine type 0x%lx\n", $machine);
//       }
//   }
//}
//
//close(EXE);
  }
}
