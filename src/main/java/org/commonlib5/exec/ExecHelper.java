/*
 * ExecHelper.java
 *
 * Created on 23 marzo 2006, 17.18
 */
package org.commonlib5.exec;

import java.io.*;
import java.util.Collection;

/**
 * Convenience methods for executing non-Java processes.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/ExecHelper.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.00
 */
public final class ExecHelper
{
  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * Output from the process is expected to be text in the system's default character set.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmd string containing the command to call and its arguments.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String cmd)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmd), null);
  }

  public static ExecHelper exec(String cmd, String[] envp)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmd, envp), null);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * Output from the process is expected to be text in the system's default character set.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(Collection<String> cmdarray)
     throws IOException
  {
    String[] cmdArr = cmdarray.toArray(new String[cmdarray.size()]);
    return new ExecHelper(Runtime.getRuntime().exec(cmdArr), null);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * Output from the process is expected to be text in the system's default character set.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String[] cmdarray)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray), null);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * Output from the process is expected to be text in the system's default character set.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @param envp array of strings, each element of which has environment variable settings in format name=value.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String[] cmdarray, String[] envp)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray, envp), null);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * Output from the process is expected to be text in the system's default character set.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @param envp array of strings, each element of which has environment variable settings in format name=value.
   * @param dir the working directory of the subprocess, or null if the subprocess should inherit the working directory
   * of the current process.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String[] cmdarray, String[] envp, File dir)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray, envp, dir), null);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @param charset Output from the executed command is expected to be in this character set.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String[] cmdarray, String charset)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray), charset);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @param envp array of strings, each element of which has environment variable settings in format name=value.
   * @param charset Output from the executed command is expected to be in this character set.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String[] cmdarray, String[] envp, String charset)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray, envp), charset);
  }

  /**
   * Executes the specified command and arguments in a separate process, and waits for the
   * process to finish.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param cmdarray array containing the command to call and its arguments.
   * @param envp array of strings, each element of which has environment variable settings in format name=value.
   * @param dir the working directory of the subprocess, or null if the subprocess should inherit the working directory
   * of the current process.
   * @param charset Output from the executed command is expected to be in this character set.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper exec(String[] cmdarray, String[] envp, File dir, String charset)
     throws IOException
  {
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray, envp), charset);
  }

  /**
   * Executes the specified command using a shell. On windows uses cmd.exe or command.exe.
   * On other platforms it uses /bin/sh.
   * <p>
   * A shell should be used to execute commands when features such as file redirection, pipes,
   * argument parsing are desired.
   * <p>
   * Output from the process is expected to be text in the system's default character set.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param command String containing a command to be parsed by the shell and executed.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if command is null
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper execUsingShell(String command)
     throws IOException
  {
    return execUsingShell(command, null);
  }

  /**
   * Executes the specified command using a shell. On windows uses cmd.exe or command.exe.
   * On other platforms it uses /bin/sh.
   * <p>
   * A shell should be used to execute commands when features such as file redirection, pipes,
   * argument parsing are desired.
   * <p>
   * No input is passed to the process on STDIN.
   *
   * @param command String containing a command to be parsed by the shell and executed.
   * @param charset Output from the executed command is expected to be in this character set.
   * @return The results of the execution in an ExecHelper object.
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if command is null
   *
   * @since ostermillerutils 1.06.00
   */
  public static ExecHelper execUsingShell(String command, String charset)
     throws IOException
  {
    if(command == null)
      throw new NullPointerException();
    String[] cmdarray;
    String os = System.getProperty("os.name");
    if(os.equals("Windows 95") || os.equals("Windows 98") || os.equals("Windows ME"))
      cmdarray = new String[]
      {
        "command.exe", "/C", command
      };
    else if(os.startsWith("Windows"))
      cmdarray = new String[]
      {
        "cmd.exe", "/C", command
      };
    else
      cmdarray = new String[]
      {
        "/bin/sh", "-c", command
      };
    return new ExecHelper(Runtime.getRuntime().exec(cmdarray), charset);
  }

  /**
   * Take a process, record its standard error and standard out streams, wait for it to finish
   *
   * @param process process to watch
   * @param charset
   * @throws SecurityException if a security manager exists and its checkExec method doesn't allow creation of a
   * subprocess.
   * @throws IOException - if an I/O error occurs
   * @throws NullPointerException - if cmdarray is null
   * @throws IndexOutOfBoundsException - if cmdarray is an empty array (has length 0).
   *
   * @since ostermillerutils 1.06.00
   */
  public ExecHelper(Process process, String charset)
     throws IOException
  {
    runExecHelper(process, charset);
  }

  private StringBuilder boutput = new StringBuilder();
  private StringBuilder berror = new StringBuilder();
  private String charset;

  private ProcessWatchListner defaultListner = new ProcessWatchListner()
  {
    private String convert(byte[] output, int offset, int length)
    {
      try
      {
        return charset == null ? new String(output, offset, length)
                  : new String(output, offset, length, charset);
      }
      catch(UnsupportedEncodingException ex)
      {
        return "";
      }
    }

    @Override
    public void notifyStdout(byte[] output, int offset, int length)
    {
      boutput.append(convert(output, offset, length));
    }

    @Override
    public void notifyStderr(byte[] output, int offset, int length)
    {
      berror.append(convert(output, offset, length));
    }
  };

  protected void runExecHelper(Process process, String _charset)
     throws IOException
  {
    this.charset = _charset;
    ProcessWatch.watch(process, true, defaultListner);
  }

  /**
   * Get the output of the job that ran.
   *
   * @return Everything the executed process wrote to its standard output as a String.
   *
   * @since ostermillerutils 1.06.00
   */
  public String getOutput()
  {
    return boutput.toString();
  }

  /**
   * Get the error output of the job that ran.
   *
   * @return Everything the executed process wrote to its standard error as a String.
   *
   * @since ostermillerutils 1.06.00
   */
  public String getError()
  {
    return berror.toString();
  }
  /**
   * The status of the job that ran.
   *
   * @since ostermillerutils 1.06.00
   */
  private int status;

  /**
   * Get the status of the job that ran.
   *
   * @return exit status of the executed process, by convention, the value 0 indicates normal termination.
   *
   * @since ostermillerutils 1.06.00
   */
  public int getStatus()
  {
    return status;
  }
}
