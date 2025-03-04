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
package org.commonlib5.utils;

import java.util.List;
import java.util.function.Consumer;
import org.commonlib5.lambda.ConsumerThrowException;

/**
 * Split automatico operazioni lunghe.
 * Frammentazione di operazioni lunghe e/o su grossi array in parti più piccole
 * per diminuire il carico sulla memoria e la cpu.
 * Ogni funzione ha un insieme di riferimento e un massimo ammesso oltre il quale
 * l'insieme viene frammentato e inviato in più parti al consumer.
 * @author Nicola De Nisco
 */
public class AutoSplitter
{
  public static <T> void split(List<T> cobj, int maxSplit, Consumer<List<T>> consumer)
  {
    if(cobj.size() < maxSplit)
    {
      consumer.accept(cobj);
      return;
    }

    List<List<T>> splitList = ArrayOper.splitList(cobj, maxSplit);

    for(List<T> lt : splitList)
      consumer.accept(lt);
  }

  public static <T> void split2(List<T> cobj, int maxSplit, ConsumerThrowException<List<T>> consumer)
     throws Exception
  {
    if(cobj.size() < maxSplit)
    {
      consumer.accept(cobj);
      return;
    }

    List<List<T>> splitList = ArrayOper.splitList(cobj, maxSplit);

    for(List<T> lt : splitList)
      consumer.accept(lt);
  }
}
