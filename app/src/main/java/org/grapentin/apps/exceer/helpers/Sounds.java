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
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import org.grapentin.apps.exceer.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sounds extends Thread
{

  private static Sounds instance = new Sounds();

  @SuppressWarnings("deprecation")
  private SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

  private HashMap<Integer, Integer> sounds = new HashMap<>();
  private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  private Sounds ()
    {
      start();
    }

  @NonNull
  private static Sounds getInstance ()
    {
      return instance;
    }

  public static void load ()
    {
      try
        {
          for (Field field : R.raw.class.getFields())
            getInstance().sounds.put(field.getInt(null), getInstance().soundPool.load(Context.get(), field.getInt(null), 0));
        }
      catch (IllegalAccessException e)
        {
          throw new Error(e);
        }
    }

  public static void play (@RawRes int resource)
    {
      getInstance().queue.add(resource);
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

}
