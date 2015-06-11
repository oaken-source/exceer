/*
 * comment
 */

package org.grapentin.apps.exceer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.grapentin.apps.exceer.training.Training;

public class TrainingActivity extends Activity
{

  private Training training;

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_training);

      if (savedInstanceState == null)
        {
          training = new Training("default startbodyweight.com routine");
        }
      else
        {
          training = (Training)savedInstanceState.getSerializable(getString(R.string.TrainingActivityBundleTraining));
        }
    }

  @Override
  protected void onStop ()
    {
      super.onStop();
    }

  @Override
  public void onBackPressed ()
    {

    }

  @Override
  public void onSaveInstanceState (@NonNull Bundle savedInstanceState)
    {
      super.onSaveInstanceState(savedInstanceState);

      savedInstanceState.putSerializable(getString(R.string.TrainingActivityBundleTraining), training);
    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_training, menu);
      return true;
    }

  @Override
  public boolean onOptionsItemSelected (MenuItem item)
    {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings)
        {
          return true;
        }
      if (id == R.id.action_training_abort)
        {
          super.onBackPressed();
          return true;
        }

      return super.onOptionsItemSelected(item);
    }

  public void onContextButtonClicked (View view)
    {

    }

}
