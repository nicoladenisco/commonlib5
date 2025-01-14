package org.commonlib5.utils;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.UIManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interfaccia specifica verso le funzionalita di Mac OS X.
 * Adatta barra dei menu e consente di installare gli handler
 * per accettare funzionalità specifiche di mac.
 * <br>
 * Nel caso Java >= 11 alcune di queste funzionalità
 * sono già presenti in java.awt.Desktop e occorre far riferimento
 * a queste (nel proxy sono disattivate).
 * @author Nicola De Nisco
 */
public class OSXProxy implements InvocationHandler
{
  private static final Log log = LogFactory.getLog(OSXProxy.class);
  //
  private Object source;
  private Object macOSXApplication;
  private Object osxAdapterProxy;
  private final Map<String, Consumer<ActionEvent>> funMap1 = new HashMap<>();
  private final Map<String, Function<ActionEvent, Boolean>> funMap2 = new HashMap<>();

  public Object getSource()
  {
    return source;
  }

  public void setSource(Object source)
  {
    this.source = source;
  }

  /**
   * Inizializza proxy.
   * Attiva modifiche alle property ottimali per mac os x.
   * Il nome appicazione viene impostato (appare nella barra menu a sinistra).
   * @param appName
   */
  public void init(String appName)
  {
    if(!OsIdent.isMac())
      return;

    try
    {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("apple.graphics.UseQuartz", "true");
      System.setProperty("apple.awt.application.name", appName);
      System.setProperty("apple.awt.application.appearance", "system");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      if(OsIdent.getJavaVersionNumber() >= 11)
      {
        initJava11();
      }
      else
      {
        initJava8();
      }
    }
    catch(Exception ex)
    {
      log.error("Unavailable system look and feel.");
    }
  }

  /**
   * Inizializzazione con Java 8.
   * Vengono attivati gli handler opportuni per le funzionalita presenti in com.apple.eawt.ApplicationListener.
   */
  public void initJava8()
  {
    try
    {
      Class applicationClass = Class.forName("com.apple.eawt.Application");
      if(macOSXApplication == null)
        macOSXApplication = applicationClass.getConstructor().newInstance();

      Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
      Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", applicationListenerClass);

      // Create a proxy object around this handler that can be reflectively added as an Apple ApplicationListener
      osxAdapterProxy = Proxy.newProxyInstance(OSXProxy.class.getClassLoader(), new Class[]
      {
        applicationListenerClass
      }, this);

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

  /**
   * Inizializza con Java11.
   * In java 11 server solo la classe com.apple.eawt.Application.
   * Altre funzionalita sono disponibili in java.awt.Desktop.
   */
  public void initJava11()
  {
    try
    {
      Class applicationClass = Class.forName("com.apple.eawt.Application");
      if(macOSXApplication == null)
        macOSXApplication = applicationClass.getConstructor().newInstance();
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

  public Method getAppMethod(String name, Class<?>... parameterTypes)
  {
    try
    {
      return macOSXApplication.getClass().getDeclaredMethod(name, parameterTypes);
    }
    catch(Throwable ex)
    {
      return null;
    }
  }

  /**
   * Set the Quit Handler.
   * Pass this method an Object and Method equipped to perform application shutdown logic.
   * The method passed should return a boolean stating whether or not the quit should occur.
   * @param fun implementation
   */
  public void setQuitHandler(Consumer<ActionEvent> fun)
  {
    funMap1.put("handleQuit", fun);
  }

  /**
   * Set About Handler.
   * Pass this method an Object and Method equipped to display application info
   * They will be called when the About menu item is selected from the application menu
   * @param fun implementation
   */
  public void setAboutHandler(Consumer<ActionEvent> fun)
  {
    funMap1.put("handleAbout", fun);

    // If we're setting a handler, enable the About menu item by calling
    // com.apple.eawt.Application reflectively
    try
    {
      Method enableAboutMethod = getAppMethod("setEnabledAboutMenu", boolean.class);
      if(enableAboutMethod != null)
        enableAboutMethod.invoke(macOSXApplication, true);
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
   * @param fun implementation
   */
  public void setPreferencesHandler(Consumer<ActionEvent> fun)
  {
    funMap1.put("handlePreferences", fun);

    // If we're setting a handler, enable the Preferences menu item by calling
    // com.apple.eawt.Application reflectively
    try
    {
      Method enablePrefsMethod = getAppMethod("setEnabledPreferencesMenu", boolean.class);
      if(enablePrefsMethod != null)
        enablePrefsMethod.invoke(macOSXApplication, true);
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
   * @param fun implementation
   */
  public void setFileHandler(Function<ActionEvent, Boolean> fun)
  {
    funMap2.put("handleOpenFile", (appleEvent) ->
    {
      if(appleEvent != null)
      {
        try
        {
          Method getFilenameMethod = appleEvent.getClass().getDeclaredMethod("getFilename");
          String filename = (String) getFilenameMethod.invoke(appleEvent);
          return fun.apply(new ActionEvent(filename, 0, "handleOpenFile"));
        }
        catch(Exception ex)
        {
          log.error("Invocation not possible:", ex);
        }
      }
      return true;
    });
  }

  public void setDockIconImage(Image image)
  {
    try
    {
      Method m = getAppMethod("setDockIconImage", Image.class);
      m.invoke(macOSXApplication, image);
    }
    catch(Exception ex)
    {
      log.error("Cannot set dock icon:", ex);
    }
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
    String name = method.getName();

    Consumer<ActionEvent> fun1 = funMap1.get(name);
    if(fun1 != null)
    {
      Object params = args.length > 0 ? args[0] : source;
      fun1.accept(new ActionEvent(params, 0, name));
      setApplicationEventHandled(args[0], true);
      return null;
    }

    Function<ActionEvent, Boolean> fun2 = funMap2.get(name);
    if(fun2 != null)
    {
      Object params = args.length > 0 ? args[0] : source;
      boolean handled = fun2.apply(new ActionEvent(params, 0, name));
      setApplicationEventHandled(args[0], handled);
      return null;
    }

    return null;
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
