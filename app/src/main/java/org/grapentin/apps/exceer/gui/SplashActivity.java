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

package org.grapentin.apps.exceer.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.service.AudioService;
import org.grapentin.apps.exceer.service.DatabaseService;

import java.util.concurrent.CountDownLatch;

public class SplashActivity extends Activity
{

  private ServiceConnection audioService;
  private ServiceConnection databaseService;

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_loading);

      new Thread(new Runnable()
      {
        @Override
        public void run ()
          {
            initialize();
          }
      }).start();
    }

  private void initialize ()
    {
      // init lock for services
      final CountDownLatch initLock = new CountDownLatch(2);

      // set default audio stream (controlled with hardware buttons)
      setVolumeControlStream(AudioManager.STREAM_MUSIC);

      // bind to audio service and start threaded wait for finished init
      Intent audioServiceIntent = new Intent(this, AudioService.class);
      audioService = new ServiceConnection()
      {
        @Override
        public void onServiceConnected (ComponentName name, final IBinder service)
          {
            new Thread(new Runnable()
            {
              @Override
              public void run ()
                {
                  ((AudioService.LocalBinder)service).await();
                  initLock.countDown();
                }
            }).start();
          }

        @Override
        public void onServiceDisconnected (ComponentName name)
          {
            throw new Error("AudioService initialization failed");
          }
      };
      bindService(audioServiceIntent, audioService, BIND_AUTO_CREATE);

      // bind to database service and start threaded wait for finished init
      Intent databaseServiceIntent = new Intent(this, DatabaseService.class);
      databaseService = new ServiceConnection()
      {
        @Override
        public void onServiceConnected (ComponentName name, final IBinder service)
          {
            new Thread(new Runnable()
            {
              @Override
              public void run ()
                {
                  ((DatabaseService.LocalBinder)service).await();
                  initLock.countDown();
                }
            }).start();
          }

        @Override
        public void onServiceDisconnected (ComponentName name)
          {
            throw new Error("DatabaseService initialization failed");
          }
      };
      bindService(databaseServiceIntent, databaseService, BIND_AUTO_CREATE);

      // wait for services to start
      while (initLock.getCount() > 0)
        try
          {
            initLock.await();
          }
        catch (InterruptedException e)
          {
            // just retry...
          }

      // initialization done - proceed to main activity
      Intent mainIntent = new Intent(this, MainActivity.class);
      startActivity(mainIntent);

      finish();
    }

  protected void onDestroy ()
    {
      super.onDestroy();

      unbindService(audioService);
      unbindService(databaseService);
    }

}
