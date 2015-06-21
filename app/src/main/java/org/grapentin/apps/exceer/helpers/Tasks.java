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
import android.support.annotation.CallSuper;

public class Tasks
{

  private final static Tasks instance = new Tasks();
  private final Handler handler = new Handler();

  private Tasks ()
    {

    }

  abstract static public class TimerTask implements Runnable
  {
    final public void run ()
      {
        long next = update();

        if (next > 0)
          {
            next -= System.currentTimeMillis();
            instance.handler.postDelayed(this, next);
          }
      }

    @CallSuper
    public void start ()
      {
        instance.handler.removeCallbacks(this);
        instance.handler.post(this);
      }

    @CallSuper
    public void stop ()
      {
        instance.handler.removeCallbacks(this);
      }

    @CallSuper
    public void pause ()
      {
        stop();
      }

    abstract public long update ();
  }

}
