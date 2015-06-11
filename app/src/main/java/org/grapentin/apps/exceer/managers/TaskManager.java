/*
 * comment
 */

package org.grapentin.apps.exceer.managers;

import android.os.Handler;
import android.util.Log;

public class TaskManager
{

  private static TaskManager instance = null;

  private Handler handler;

  private TaskManager ()
    {
      handler = new Handler();
    }

  private static TaskManager getInstance ()
    {
      if (instance == null)
        instance = new TaskManager();
      return instance;
    }

  public static void init ()
    {
      getInstance();
    }

  public static void removeCallbacks (TimerTask t)
    {
      getInstance().handler.removeCallbacks(t);
    }

  public static void post(TimerTask t)
    {
      getInstance().handler.post(t);
    }

  abstract static public class TimerTask implements Runnable
  {
    public void run ()
      {
        long next = update();

        if (next > 0)
          {
            next -= System.currentTimeMillis();
            Log.d("TimerTask", "[" + this + "] next delay: " + next);
            getInstance().handler.postDelayed(this, next);
          }
      }

    abstract public long update ();
  }

}
