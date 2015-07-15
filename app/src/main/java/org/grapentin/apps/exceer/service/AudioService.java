/******************************************************************************
 *    This file is part of Exceer                                             *
 *                                                                            *
 *    Copyright (C) 2015  Andreas Grapentin                                   *
 *                                                                            *
 *    This program is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by    *
 *    the Free Software Foundation, either version 3 of the License, or       *
 *    (at your option) any later version.                                     *
 *                                                                            *
 *    This program is distributed in the hope that it will be useful,         *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *    GNU General Public License for more details.                            *
 *                                                                            *
 *    You should have received a copy of the GNU General Public License       *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.   *
 ******************************************************************************/

package org.grapentin.apps.exceer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.RawRes;
import android.util.Log;

import org.grapentin.apps.exceer.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class AudioService extends Service
{

  private static LocalBinder local = null;

  private final IBinder binder = new LocalBinder();

  private final SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
  private final HashMap<Integer, Integer> sounds = new HashMap<>();

  public CountDownLatch initLock = new CountDownLatch(1);

  public static void play (@RawRes int resource)
    {
      local.play(resource);
    }

  @Override
  public void onCreate ()
    {
      super.onCreate();

      new Thread(new Runnable()
      {
        @Override
        public void run ()
          {
            initialize();
          }
      }).start();
    }

  @Override
  public void onDestroy ()
    {
      Log.d("AudioService", "onDestroy");
    }

  void initialize ()
    {
      Log.d("AudioService", "starting Initialization");

      for (Field field : R.raw.class.getFields())
        try
          {
            sounds.put(field.getInt(null), soundPool.load(getApplicationContext(), field.getInt(null), 0));
          }
        catch (IllegalAccessException e)
          {
            throw new Error(e);
          }

      initLock.countDown();

      Log.d("AudioService", "finished Initialization");
    }

  @Override
  public IBinder onBind (Intent intent)
    {
      local = (LocalBinder)binder;
      return binder;
    }

  public class LocalBinder extends Binder
  {
    public void play (@RawRes int resource)
      {
        Log.d("AudioService", "playing " + resource);
        soundPool.play(sounds.get(resource), 1, 1, 1, 0, 1);
      }

    public void await ()
      {
        while (initLock.getCount() > 0)
          try
            {
              initLock.await();
            }
          catch (InterruptedException e)
            {
              // just retry
            }
      }
  }

}
