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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Crea una implementazione runtime di una interfaccia.
 * Viene creato un oggetto proxy i cui metodi specificati
 * da addMethod sono implementati attraverso una apposita lambda.
 *
 *
 * @author Nicola De Nisco
 * @param <T> interfaccia da implementare
 */
public class InterfaceImplementAdapter<T>
{
  protected final Class<T> interfaccia;
  protected final Map<String, FunCall> methodMap = new HashMap<>();

  public InterfaceImplementAdapter(Class<T> interfaccia)
  {
    this.interfaccia = interfaccia;
  }

  public void addMethod(String methodName, FunCall f)
  {
    methodMap.put(methodName, f);
  }

  public T createAdapter()
  {
    return (T) Proxy.newProxyInstance(LocalHandler.class.getClassLoader(),
       new Class<?>[]
       {
         interfaccia
       },
       new LocalHandler());
  }

  public class LocalHandler implements InvocationHandler
  {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
       throws Throwable
    {
      FunCall f = methodMap.get(method.getName());

      if(f != null)
        return f.apply(args);

      throw new UnsupportedOperationException("Not implemented: "
         + method + ", args=" + Arrays.toString(args)); // NOI18N
    }
  }

  @FunctionalInterface
  public interface FunCall
  {
    public Object apply(Object[] args);
  }
}
