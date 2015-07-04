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

package org.grapentin.apps.exceer.gui.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.grapentin.apps.exceer.gui.widgets.interfaces.TextContainer;
import org.grapentin.apps.exceer.service.AudioService;
import org.grapentin.apps.exceer.service.DatabaseService;

public class BaseActivity extends Activity
{

  private static volatile BaseActivity instance = null;

  protected ServiceConnection audioService;
  protected ServiceConnection databaseService;

  @NonNull
  public static Context getContext ()
    {
      return getInstance();
    }

  @NonNull
  public static BaseActivity getInstance ()
    {
      assert instance != null;
      return instance;
    }

  public static void setText (@IdRes int res, @StringRes int string)
    {
      TextContainer c = (TextContainer)getInstance().findViewById(res);
      if (c != null)
        c.setText(string);
    }

  public static void setText (@IdRes int res, @NonNull CharSequence string)
    {
      TextContainer c = (TextContainer)getInstance().findViewById(res);
      if (c != null)
        c.setText(string);
    }

  @CallSuper
  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);

      Intent audioServiceIntent = new Intent(this, AudioService.class);
      audioService =  new ServiceConnection()
      {
        @Override
        public void onServiceConnected (ComponentName name, final IBinder service)
          {

          }

        @Override
        public void onServiceDisconnected (ComponentName name)
          {
            throw new Error("AudioService initialization failed");
          }
      };
      bindService(audioServiceIntent, audioService, BIND_AUTO_CREATE);

      Intent databaseServiceIntent = new Intent(this, DatabaseService.class);
      databaseService = new ServiceConnection()
      {
        @Override
        public void onServiceConnected (ComponentName name, final IBinder service)
          {

          }

        @Override
        public void onServiceDisconnected (ComponentName name)
          {
            throw new Error("DatabaseService initialization failed");
          }
      };
      bindService(databaseServiceIntent, databaseService, BIND_AUTO_CREATE);
    }

  @CallSuper
  @Override
  protected void onResume ()
    {
      super.onResume();
      instance = this;
    }

  @CallSuper
  @Override
  protected void onDestroy ()
    {
      super.onDestroy();
      unbindService(audioService);
      unbindService(databaseService);
    }

}
