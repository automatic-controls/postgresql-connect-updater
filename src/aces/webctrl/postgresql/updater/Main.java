package aces.webctrl.postgresql.updater;
import javax.servlet.*;
import java.nio.file.*;
import com.controlj.green.addonsupport.*;
import com.controlj.green.webserver.*;
public class Main implements ServletContextListener {
  private final static String addonName = "PostgreSQL_Connect";
  private volatile boolean stop = false;
  private volatile boolean stopped = false;
  private volatile Object obj = new Object();
  private final AddOnInfo info = AddOnInfo.getAddOnInfo();
  private final FileLogger logger = info.getDateStampLogger();
  private final static long timeout = 600L;
  private final Thread main = new Thread(){
    public void run(){
      boolean first = true;
      while (!stop){
        if (first){
          first = false;
        }else{
          try{
            Thread.sleep(600000L);
          }catch(Throwable t){}
          if (stop){ break; }
        }
        try{
          if (!exec()){
            continue;
          }
        }catch(Throwable t){
          logger.println(t);
          continue;
        }
        break;
      }
      synchronized (obj){
        stopped = true;
        obj.notifyAll();
      }
    }
  };
  @Override public void contextInitialized(ServletContextEvent sce){
    main.start();
  }
  @Override public void contextDestroyed(ServletContextEvent sce){
    if (!stopped){
      stop = true;
      main.interrupt();
      synchronized (obj){
        while (!stopped){
          try{
            obj.wait(3000L);
          }catch(Throwable t){}
        }
      }
    }
  }
  private boolean exec() throws Throwable {
    final TomcatServer server = TomcatServerSingleton.get();
    final Path addons = server.getAddOnsDir().toPath();
    final Path update = addons.resolve(addonName+".update");
    if (!Files.exists(update)){
      return false;
    }
    for (AddOn x:server.scanForAddOns()){
      if (x!=null && addonName.equalsIgnoreCase(x.getName())){
        server.removeAddOn(x,false);
      }
    }
    final Path addon = addons.resolve(addonName+".addon");
    Thread.sleep(timeout);
    if (Files.deleteIfExists(addon)){
      Thread.sleep(timeout);
    }
    Files.move(update,addon);
    Thread.sleep(timeout);
    AddOn y = null;
    for (AddOn x:server.scanForAddOns()){
      if (x!=null && addonName.equalsIgnoreCase(x.getName())){
        y = x;
        break;
      }
    }
    if (y==null){
      server.deployAddOn(addon.toFile());
    }else{
      try{
        server.enableAddOn(y);
      }catch(Throwable t){
        Thread.sleep(timeout);
        server.deployAddOn(addon.toFile());
      }
    }
    for (AddOn x:server.scanForAddOns()){
      if (x!=null && addonName.equalsIgnoreCase(x.getName())){
        final WebApp.State s = x.getState();
        if (s==WebApp.State.RUNNING || s==WebApp.State.STARTING || s==WebApp.State.STARTUP_ERROR){
          return true;
        }
        break;
      }
    }
    return false;
  }
}