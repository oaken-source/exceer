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

package org.grapentin.apps.exceer.helpers;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RawRes;
import android.util.Log;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.activity.base.BaseActivity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sounds
{

  private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  static
    {
      new Thread(new Runnable()
      {
        public void run ()
          {
            Log.d("Sounds", "starting Initialization");

            // initialize
            SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            HashMap<Integer, Integer> sounds = new HashMap<>();

            for (Field field : R.raw.class.getFields())
              try
                {
                  sounds.put(field.getInt(null), soundPool.load(BaseActivity.getContext(), field.getInt(null), 0));
                }
              catch (IllegalAccessException e)
                {
                  throw new Error(e);
                }

            BaseActivity.initLock.countDown();
            Log.d("Sounds", "finished Initialization");

            // process queue
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
      }).start();
    }

  public static void init ()
    {
      // nothing here. go look elsewhere.
    }

  public static void play (@RawRes int resource)
    {
      queue.add(resource);
    }

}
