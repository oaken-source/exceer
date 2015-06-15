/*
 * comment
 */

package org.grapentin.apps.exceer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.grapentin.apps.exceer.managers.ContextManager;
import org.grapentin.apps.exceer.managers.SoundManager;
import org.grapentin.apps.exceer.managers.TaskManager;
import org.grapentin.apps.exceer.training.TrainingManager;
import org.grapentin.apps.exceer.training.TrainingStorage;

public class MainActivity extends Activity
{

  private TextView lastSessionTextView;

  private TaskManager.TimerTask updateTimerTask = new UpdateTimerTask();

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // initialize managers
      ContextManager.init(getApplicationContext());
      SoundManager.init();
      TaskManager.init();

      TrainingStorage.init();
      TrainingManager.init();

      lastSessionTextView = (TextView)findViewById(R.id.MainActivityLastSessionDate);

      updateTimerTask.start();
    }

  @Override
  protected void onStop ()
    {
      super.onStop();

      updateTimerTask.stop();
    }

  @Override
  protected void onResume ()
    {
      super.onResume();

      updateTimerTask.start();
    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
    }

  @Override
  public boolean onOptionsItemSelected (MenuItem item)
    {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      switch (id)
        {
        case R.id.action_settings:
          return true;
        case R.id.action_about:
          Intent intent = new Intent(this, AboutActivity.class);
          startActivity(intent);
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
