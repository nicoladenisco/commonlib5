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

import java.awt.Image;
import java.lang.reflect.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OSXAdapter implements InvocationHandler
{
  private static final Log log = LogFactory.getLog(OSXAdapter.class);
  //
  protected Object targetObject;
  protected Method targetMethod;
  protected String proxySignature;
  static Object macOSXApplication;

  /**
   * Set the Quit Handler.
   * Pass this method an Object and Method equipped to perform application shutdown logic.
   * The method passed should return a boolean stating whether or not the quit should occur.
   * @param target
   * @param quitHandler
   */
  public static void setQuitHandler(Object target, Method quitHandler)
  {
    setHandler(new OSXAdapter("handleQuit", target, quitHandler));
  }

  /**
   * Set About Handler.
   * Pass this method an Object and Method equipped to display application info
   * They will be called when the About menu item is selected from the application menu
   * @param target
   * @param aboutHandler
   */
  public static void setAboutHandler(Object target, Method aboutHandler)
  {
    boolean enableAboutMenu = (target != null && aboutHandler != null);
    if(enableAboutMenu)
    {
      setHandler(new OSXAdapter("handleAbout", target, aboutHandler));
    }

    // If we're setting a handler, enable the About menu item by calling
    // com.apple.eawt.Application reflectively
    try
    {
      Method enableAboutMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledAboutMenu", boolean.class);
      enableAboutMethod.invoke(macOSXApplication, enableAboutMenu);
    }
    catch(Exception ex)
    {
      log.error("OSXAdapter could not access the About Menu", ex);
    }
  }

  /**
   * Set Preference Handler.
   * Pass this method an Object and a Method equipped to display application options
   * They will be called when the Preferences menu item is selected from the application menu
   * @param target
   * @param prefsHandler
   */
  public static void setPreferencesHandler(Object target, Method prefsHandler)
  {
    boolean enablePrefsMenu = (target != null && prefsHandler != null);
    if(enablePrefsMenu)
    {
      setHandler(new OSXAdapter("handlePreferences", target, prefsHandler));
    }

    // If we're setting a handler, enable the Preferences menu item by calling
    // com.apple.eawt.Application reflectively
    try
    {
      Method enablePrefsMethod
         = macOSXApplication.getClass().getDeclaredMethod("setEnabledPreferencesMenu", boolean.class);
      enablePrefsMethod.invoke(macOSXApplication, enablePrefsMenu);
    }
    catch(Exception ex)
    {
      log.error("OSXAdapter could not access the About Menu", ex);
    }
  }

  /**
   * Set File handler.
   * Pass this method an Object and a Method equipped to handle document events from the Finder
   * Documents are registered with the Finder via the CFBundleDocumentTypes dictionary in the
   * application bundle's Info.plist
   * @param target
   * @param fileHandler
   */
  public static void setFileHandler(Object target, Method fileHandler)
  {
    setHandler(new OSXAdapter("handleOpenFile", target, fileHandler)
    {
      // Override OSXAdapter.callTarget to send information on the
      // file to be opened
      @Override
      public boolean callTarget(Object appleEvent)
      {
        if(appleEvent != null)
        {
          try
          {
            Method getFilenameMethod = appleEvent.getClass().getDeclaredMethod("getFilename");
            String filename = (String) getFilenameMethod.invoke(appleEvent);
            this.targetMethod.invoke(this.targetObject, filename);
          }
          catch(Exception ex)
          {
            log.error("Invocation not possible:", ex);
          }
        }
        return true;
      }
    });
  }

  /**
   * Set a generic handler.
   * setHandler creates a Proxy object from the passed OSXAdapter and adds it as an ApplicationListener.
   * @param adapter
   */
  public static void setHandler(OSXAdapter adapter)
  {
    try
    {
      Class applicationClass = Class.forName("com.apple.eawt.Application");
      if(macOSXApplication == null)
        macOSXApplication = applicationClass.getConstructor().newInstance();

      Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
      Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", applicationListenerClass);

      // Create a proxy object around this handler that can be reflectively added as an Apple ApplicationListener
      Object osxAdapterProxy = Proxy.newProxyInstance(OSXAdapter.class.getClassLoader(), new Class[]
      {
        applicationListenerClass
      }, adapter);

      addListenerMethod.invoke(macOSXApplication, osxAdapterProxy);
    }
    catch(ClassNotFoundException cnfe)
    {
      log.error("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled (" + cnfe + ")");
    }
    catch(Exception ex)
    {
      // Likely a NoSuchMethodException or an IllegalAccessException loading/invoking eawt.Application methods
      log.error("Mac OS X Adapter could not talk to EAWT:", ex);
    }
  }

  public static void setDockIconImage(Image image)
  {
    try
    {
      Class applicationClass = Class.forName("com.apple.eawt.Application");
      if(macOSXApplication == null)
        macOSXApplication = applicationClass.getConstructor().newInstance();

      Method m = applicationClass.getMethod("setDockIconImage", Image.class);
      m.invoke(macOSXApplication, image);
    }
    catch(Exception ex)
    {
      log.error("Cannot set dock icon:", ex);
    }
  }

  /**
   * Protected constructor.
   * Each OSXAdapter has the name of the EAWT method it intends to listen for (handleAbout, for example),
   * the Object that will ultimately perform the task, and the Method to be called on that Object
   * @param proxySignature
   * @param target
   * @param handler
   */
  protected OSXAdapter(String proxySignature, Object target, Method handler)
  {
    this.proxySignature = proxySignature;
    this.targetObject = target;
    this.targetMethod = handler;
  }

  /**
   * Override this method to perform any operations on the event
   * that comes with the various callbacks.
   * See setFileHandler above for an example.
   * @param appleEvent
   * @return
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  public boolean callTarget(Object appleEvent)
     throws InvocationTargetException, IllegalAccessException
  {
    Object result = targetMethod.invoke(targetObject, (Object[]) null);
    if(result == null)
      return true;

    return Boolean.parseBoolean(result.toString());
  }

  /**
   * InvocationHandler implementatio.
   * This is the entry point for our proxy object; it is called every time an ApplicationListener method is invoked.
   * @param proxy
   * @param method
   * @param args
   * @return
   * @throws Throwable
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
     throws Throwable
  {
    if(isCorrectMethod(method, args))
    {
      boolean handled = callTarget(args[0]);
      setApplicationEventHandled(args[0], handled);
    }

    // All of the ApplicationListener methods are void; return null regardless of what happens
    return null;
  }

  /**
   * Verify method.
   * Compare the method that was called to the intended method when the OSXAdapter instance was created.
   * (e.g. handleAbout, handleQuit, handleOpenFile, etc.)
   * @param method
   * @param args
   * @return
   */
  protected boolean isCorrectMethod(Method method, Object[] args)
  {
    return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
  }

  /**
   * Attach an handler.
   * It is important to mark the ApplicationEvent as handled and cancel the default behavior.
   * This method checks for a boolean result from the proxy method and sets the event accordingly.
   * @param event
   * @param handled
   */
  protected void setApplicationEventHandled(Object event, boolean handled)
  {
    if(event != null)
    {
      try
      {
        Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", boolean.class);
        // If the target method returns a boolean, use that as a hint
        setHandledMethod.invoke(event, handled);
      }
      catch(Exception ex)
      {
        log.error("OSXAdapter was unable to handle an ApplicationEvent: " + event, ex);
      }
    }
  }
}
