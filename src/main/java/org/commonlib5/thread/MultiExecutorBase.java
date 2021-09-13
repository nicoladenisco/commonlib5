/*
 * Copyright (C) 2019 Nicola De Nisco
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
package org.commonlib5.thread;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Classe base per esecuzioni estremamente parallele.
 * Crea un pool di thread a cui sottoporre operazioni.
 * L'implementazione di base crea uno work stealing pool.
 * Ridefinire il metodo createExecutor() per
 * creare un executor di tipo diverso.
 *
 * @author Nicola De Nisco
 */
public class MultiExecutorBase
   implements ExecutorService
{
  /**
   * executor per la parallelizzazione delle operazioni.
   */
  private ExecutorService __executor = null;

  public boolean mustExit = false;

  /**
   * Verifica se executor esiste e attivo.
   * @return vero se l'executor è attivo
   */
  public boolean isRunning()
  {
    if(__executor != null && !__executor.isTerminated())
      return true;

    // azzera eventuale run precedente; verrà ricreato se necessario
    __executor = null;

    return false;
  }

  @Override
  public boolean isShutdown()
  {
    if(__executor == null)
      return true;

    return getExecutor().isShutdown();
  }

  @Override
  public boolean isTerminated()
  {
    if(__executor == null)
      return true;

    return getExecutor().isTerminated();
  }

  /**
   * Ritorna executor.
   * @return executor
   */
  protected ExecutorService getExecutor()
  {
    if(__executor == null || __executor.isTerminated())
      __executor = createExecutor();

    return __executor;
  }

  /**
   * Creazione di un nuovo executor.
   * Questa implementazione crea uno work stealing pool.
   * Ridefinire eventualmente in classi derivate.
   * @return executor per l'esecuzione dei lavori
   */
  protected ExecutorService createExecutor()
  {
    return Executors.newWorkStealingPool();
  }

  /**
   * Predispone executor per shutdown.
   * questo provoca la terminazione di tutti i thread a lavoro finito
   */
  @Override
  public void shutdown()
  {
    if(__executor != null)
      __executor.shutdown();
  }

  /**
   * Submits a Runnable task for execution and returns a Future
   * representing that task. The Future's {@code get} method will
   * return {@code null} upon <em>successful</em> completion.
   *
   * @param task the task to submit
   * @return a Future representing pending completion of the task
   */
  @Override
  public Future<?> submit(Runnable task)
  {
    return getExecutor().submit(task);
  }

  @Override
  public List<Runnable> shutdownNow()
  {
    if(__executor == null)
      return Collections.EMPTY_LIST;

    return getExecutor().shutdownNow();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit)
     throws InterruptedException
  {
    if(__executor == null)
      return true;

    return getExecutor().awaitTermination(timeout, unit);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task)
  {
    return getExecutor().submit(task);
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result)
  {
    return getExecutor().submit(task, result);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
     throws InterruptedException
  {
    return getExecutor().invokeAll(tasks);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
     throws InterruptedException
  {
    return getExecutor().invokeAll(tasks, timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
     throws InterruptedException, ExecutionException
  {
    return getExecutor().invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
     throws InterruptedException, ExecutionException, TimeoutException
  {
    return getExecutor().invokeAny(tasks, timeout, unit);
  }

  @Override
  public void execute(Runnable command)
  {
    getExecutor().execute(command);
  }
}
