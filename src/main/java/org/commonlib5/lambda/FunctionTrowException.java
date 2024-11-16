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

import java.util.Objects;

/**
 * Represents a function that accepts one argument and produces a result.
 * Versione modificata di Function che può sollevare eccezioni.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 * @since 1.8
 */
@FunctionalInterface
public interface FunctionTrowException<T, R>
{

  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   * @throws java.lang.Exception
   */
  R apply(T t)
     throws Exception;

  /**
   * Returns a composed function that first applies the {@code before}
   * function to its input, and then applies this function to the result.
   * If evaluation of either function throws an exception, it is relayed to
   * the caller of the composed function.
   *
   * @param <V> the type of input to the {@code before} function, and to the
   * composed function
   * @param before the function to apply before this function is applied
   * @return a composed function that first applies the {@code before}
   * function and then applies this function
   * @throws java.lang.Exception
   * @throws NullPointerException if before is null
   */
  default <V> FunctionTrowException<V, R> compose(FunctionTrowException<? super V, ? extends T> before)
     throws Exception
  {
    Objects.requireNonNull(before);
    return (V v) -> apply(before.apply(v));
  }

  /**
   * Returns a composed function that first applies this function to
   * its input, and then applies the {@code after} function to the result.
   * If evaluation of either function throws an exception, it is relayed to
   * the caller of the composed function.
   *
   * @param <V> the type of output of the {@code after} function, and of the
   * composed function
   * @param after the function to apply after this function is applied
   * @return a composed function that first applies this function and then
   * applies the {@code after} function
   * @throws java.lang.Exception
   * @throws NullPointerException if after is null
   */
  default <V> FunctionTrowException<T, V> andThen(FunctionTrowException<? super R, ? extends V> after)
     throws Exception
  {
    Objects.requireNonNull(after);
    return (T t) -> after.apply(apply(t));
  }

  /**
   * Returns a function that always returns its input argument.
   *
   * @param <T> the type of the input and output objects to the function
   * @return a function that always returns its input argument
   */
  static <T> FunctionTrowException<T, T> identity()
  {
    return t -> t;
  }
}
