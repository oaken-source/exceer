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

import org.grapentin.apps.exceer.training.TrainingList;

public class TrainingActivity extends Activity
{

  private TrainingList trainings;

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_training);

      if (savedInstanceState == null)
        {
          trainings = new TrainingList(this);
          trainings.setCurrentTraining("default startbodyweight.com routine");
        }
      else
        {
          trainings = (TrainingList)savedInstanceState.getSerializable((getString(R.string.TrainingActivityBundleTrainings)));
        }
    }

  @Override
  protected void onStop ()
    {
      trainings.getCurrentTraining().getCurrentExercisable().pause();

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

      savedInstanceState.putSerializable(getString(R.string.TrainingActivityBundleTrainings), trainings);
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
      if (trainings.getCurrentExercisable().isRunning())
        trainings.getCurrentExercisable().pause();
      else
        trainings.getCurrentExercisable().start();
    }

}
