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

import android.os.Handler;
import android.os.SystemClock;

public abstract class CountDownTimer
{

  private long duration;
  private long interval;

  private long ticks;

  private long start;

  private Handler handler = new Handler();
  private Runnable task = new Runnable()
  {
    @Override
    public void run ()
      {
        tick();
      }
  };

  public CountDownTimer (long duration, long interval)
    {
      this.duration = duration;
      this.interval = interval;
    }

  public abstract void onTick (long millisUntilFinished);

  public abstract void onFinish ();

  private void tick ()
    {
      if (ticks * interval >= duration)
        {
          onFinish();
          return;
        }

      onTick(duration - (start - SystemClock.uptimeMillis()));

      ++ticks;
      if (ticks * interval >= duration)
        handler.postAtTime(task, start + duration);
      else
        handler.postAtTime(task, start + ticks * interval);
    }

  public void start ()
    {
      start = SystemClock.uptimeMillis();
      handler.post(task);
    }

  public void cancel ()
    {
      handler.removeCallbacks(task);
    }

  public void pause ()
    {

    }

  public void resume ()
    {

    }

}
