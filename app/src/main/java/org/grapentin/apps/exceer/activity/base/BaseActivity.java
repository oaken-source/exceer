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

package org.grapentin.apps.exceer.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.grapentin.apps.exceer.helpers.Sounds;
import org.grapentin.apps.exceer.helpers.Tasks;
import org.grapentin.apps.exceer.orm.Database;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BaseActivity extends Activity
{

  public static final CountDownLatch initLock = new CountDownLatch(2);
  private static volatile BaseActivity instance = null;

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

  @CallSuper
  @Override
  protected void onResume ()
    {
      super.onResume();
      instance = this;

      if (initLock.getCount() == 0)
        return;

      Tasks.init();
      Sounds.init();
      Database.init();

      try
        {
          initLock.await(1, TimeUnit.SECONDS);
        }
      catch (InterruptedException e)
        {
          // nothing here.
        }

      if (initLock.getCount() > 0)
        {
          final ProgressDialog progress = new ProgressDialog(instance);
          progress.setTitle("Updating Database");
          progress.setMessage("Please wait while the database is updated...");
          progress.show();

          Runnable runnable = new Runnable()
          {
            @Override
            public void run ()
              {
                while (initLock.getCount() > 0)
                  try
                    {
                      initLock.await();
                    }
                  catch (InterruptedException e)
                    {
                      // just retry...
                    }
                progress.dismiss();
              }
          };
          new Thread(runnable).start();
        }
    }

}
