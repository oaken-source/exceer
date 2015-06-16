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

package org.grapentin.apps.exceer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.grapentin.apps.exceer.managers.ContextManager;
import org.grapentin.apps.exceer.managers.SoundManager;
import org.grapentin.apps.exceer.managers.TaskManager;
import org.grapentin.apps.exceer.orm.DatabaseManager;
import org.grapentin.apps.exceer.orm.ModelExercise;
import org.grapentin.apps.exceer.orm.ModelTraining;
import org.grapentin.apps.exceer.training.TrainingManager;
import org.grapentin.apps.exceer.training.TrainingStorage;

public class MainActivity extends Activity
{

  private TaskManager.TimerTask task = new UpdateTimerTask();

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // initialize managers
      ContextManager.init(getApplicationContext());
      SoundManager.init();
      TaskManager.init();

      DatabaseManager.init();

      TrainingStorage.init();
      TrainingManager.init();

      task.start();
    }

  @Override
  protected void onStop ()
    {
      super.onStop();
      task.stop();
    }

  @Override
  protected void onResume ()
    {
      super.onResume();
      task.start();
    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
    }

  @Override
  public boolean onOptionsItemSelected (MenuItem item)
    {
      int id = item.getItemId();

      switch (id)
        {
        case R.id.action_settings:
          Intent settingsIntent = new Intent(this, SettingsActivity.class);
          startActivity(settingsIntent);
          break;
        case R.id.action_about:
          Intent aboutIntent = new Intent(this, AboutActivity.class);
          startActivity(aboutIntent);
          break;
        }

      return super.onOptionsItemSelected(item);
    }

  public void onTrainButtonClicked (View view)
    {
      Intent intent = new Intent(this, TrainingActivity.class);
      startActivity(intent);
    }

  private class UpdateTimerTask extends TaskManager.TimerTask
  {
    public long update ()
      {
        TextView lastSessionTextView = (TextView)findViewById(R.id.MainActivityLastSessionDate);

        long last = TrainingStorage.getLastTrainingDate();
        long elapsed = System.currentTimeMillis() - last;

        elapsed = Math.round(elapsed / 1000.0);
        if (elapsed < 60 /* seconds */)
          {
            lastSessionTextView.setText(elapsed + " sec");
            return last + (elapsed + 1) * 1000;
          }
        elapsed = Math.round(elapsed / 60.0);
        if (elapsed < 60 /* minutes */)
          {
            lastSessionTextView.setText(elapsed + " min");
            return last + (elapsed + 1) * 1000 * 60;
          }
        elapsed = Math.round(elapsed / 60.0);
        if (elapsed < 24 /* hours */)
          {
            lastSessionTextView.setText(elapsed + " hour" + (elapsed > 1 ? "s" : ""));
            return last + (elapsed + 1) * 1000 * 60 * 60;
          }
        elapsed = Math.round(elapsed / 24.0);

        lastSessionTextView.setText(elapsed + " day" + (elapsed > 1 ? "s" : ""));
        return last + (elapsed + 1) * 1000 * 60 * 60 * 24;
      }
  }

}
