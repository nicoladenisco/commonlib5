/*
 * Copyright (C) 2017 Nicola De Nisco
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
package org.commonlib5.lambda;

import java.util.Iterator;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility per catturare le eccezioni nelle lambda expression (tipo forEach).
 *
 * @author Nicola De Nisco
 */
public class LEU
{
  @FunctionalInterface
  public interface Consumer_WithExceptions<T, E extends Exception>
  {
    void accept(T t)
       throws E;
  }

  @FunctionalInterface
  public interface Function_WithExceptions<T, R, E extends Exception>
  {
    R apply(T t)
       throws E;
  }

  @FunctionalInterface
  public interface ToIntFunction_WithExceptions<T, E extends Exception>
  {
    int applyAsInt(T value)
       throws E;
  }

  @FunctionalInterface
  public interface ToLongFunction_WithExceptions<T, E extends Exception>
  {
    long applyAsLong(T value)
       throws E;
  }

  @FunctionalInterface
  public interface ToDoubleFunction_WithExceptions<T, E extends Exception>
  {
    double applyAsDouble(T value)
       throws E;
  }

  @FunctionalInterface
  public interface Predicate_WithExceptions<T, E extends Exception>
  {
    boolean test(T t)
       throws E;
  }

  @FunctionalInterface
  public interface Runnable_WithExceptions<E extends Exception>
  {
    public void run()
       throws E;
  }

  /**
   * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name))));
   */
  public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer)
     throws E
  {
    return t ->
    {
      try
      {
        consumer.accept(t);
      }
      catch(Exception exception)
      {
        throwActualException(exception);
      }
    };
  }

  /**
   * .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName))
   */
  public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function)
     throws E
  {
    return t ->
    {
      try
      {
        return function.apply(t);
      }
      catch(Exception exception)
      {
        throwActualException(exception);
        return null;
      }
    };
  }

  /**
   * .mapToInt(LEU.rethrowFunctionInt((r) -> r.getValue(indexInRecord).asInt()))
   */
  public static <T, E extends Exception> ToIntFunction<T> rethrowFunctionInt(ToIntFunction_WithExceptions<T, E> function)
     throws E
  {
    return t ->
    {
      try
      {
        return function.applyAsInt(t);
      }
      catch(Exception exception)
      {
        throwActualException(exception);
        return 0;
      }
    };
  }

  public static <T, E extends Exception> ToLongFunction<T> rethrowFunctionLong(ToLongFunction_WithExceptions<T, E> function)
     throws E
  {
    return t ->
    {
      try
      {
        return function.applyAsLong(t);
      }
      catch(Exception exception)
      {
        throwActualException(exception);
        return 0;
      }
    };
  }

  public static <T, E extends Exception> ToDoubleFunction<T> rethrowFunctionDouble(ToDoubleFunction_WithExceptions<T, E> function)
     throws E
  {
    return t ->
    {
      try
      {
        return function.applyAsDouble(t);
      }
      catch(Exception exception)
      {
        throwActualException(exception);
        return 0;
      }
    };
  }

  /**
   * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name))));
   */
  public static <T, E extends Exception> Predicate<T> rethrowPredicate(Predicate_WithExceptions<T, E> predicate)
     throws E
  {
    return t ->
    {
      try
      {
        return predicate.test(t);
      }
      catch(Exception exception)
      {
        throwActualException(exception);
        return false;
      }
    };
  }

  /**
   * executor.execute(rethrowRunnable(() -> System.out.println(Class.forName(name))));
   */
  public static <E extends Exception> Runnable rethrowRunnable(Runnable_WithExceptions<E> runnable)
     throws E
  {
    return () ->
    {
      try
      {
        runnable.run();
      }
      catch(Exception exception)
      {
        throwActualException(exception);
      }
    };
  }

  @SuppressWarnings("unchecked")
  private static <E extends Exception> void throwActualException(Exception exception)
     throws E
  {
    throw (E) exception;
  }

  /**
   * Funzione per generare uno stream a peratire da un iteratore.
   * @param <T>
   * @param sourceIterator iteratore del tipo indicato
   * @return stream
   */
  public static <T> Stream<T> asStream(Iterator<T> sourceIterator)
  {
    return asStream(sourceIterator, false);
  }

  /**
   * Funzione per generare uno stream a peratire da un iteratore.
   * @param <T>
   * @param sourceIterator iteratore del tipo indicato
   * @param parallel vero se richiesto uno stream parallelo
   * @return stream
   */
  public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel)
  {
    Iterable<T> iterable = () -> sourceIterator;
    return StreamSupport.stream(iterable.spliterator(), parallel);
  }
}
