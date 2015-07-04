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
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.grapentin.apps.exceer.gui.SplashActivity;

import java.util.concurrent.CountDownLatch;

public class DatabaseService extends Service
{

  private final IBinder binder = new LocalBinder();

  public CountDownLatch initLock = new CountDownLatch(1);

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
      Log.d("DatabaseService", "onDestroy");
    }

  void initialize ()
    {
      Log.d("DatabaseService", "starting Initialization");

      initLock.countDown();

      Log.d("DatabaseService", "finished Initialization");
    }

  @Override
  public IBinder onBind (Intent intent)
    {
      return binder;
    }

  public class LocalBinder extends Binder
  {
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
