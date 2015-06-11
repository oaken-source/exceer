/*
 * comment
 */

package org.grapentin.apps.exceer.managers;

import android.media.AudioManager;
import android.media.SoundPool;

import org.grapentin.apps.exceer.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SoundManager extends Thread
{

  private static SoundManager instance = null;

  private SoundPool soundPool;

  private HashMap<Integer, Integer> sounds = new HashMap<>();
  private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  @SuppressWarnings("deprecation")
  private SoundManager ()
    {
      start();

      this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

      Class raw = R.raw.class;
      Field[] fields = raw.getFields();
      for (Field field : fields)
        {
          try
            {
              int resource = field.getInt(null);
              int sound = this.soundPool.load(ContextManager.get(), resource, 0);
              this.sounds.put(resource, sound);
            }
          catch (IllegalAccessException e)
            {
              // ignore
            }
        }
    }

  private static SoundManager getInstance ()
    {
      if (instance == null)
        instance = new SoundManager();
      return instance;
    }

  public static void init ()
    {
      getInstance();
    }

  public void run ()
    {
      while (true)
        {
          try
            {
              int resource = queue.take();
              soundPool.play(sounds.get(resource), 1, 1, 1, 0, 1);
            }
          catch (InterruptedException e)
            {
              break;
            }
        }
    }

  public static void play (int resource)
    {
      getInstance().queue.add(resource);
    }

}
