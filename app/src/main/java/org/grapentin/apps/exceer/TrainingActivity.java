/*
 * comment
 */

package org.grapentin.apps.exceer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.grapentin.apps.exceer.training.TrainingManager;
import org.grapentin.apps.exceer.training.TrainingStorage;

import java.io.InputStream;

public class TrainingActivity extends Activity
{

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_training);

      if (savedInstanceState == null)
        {
          TrainingManager.setGui(this);
          TrainingManager.setCurrentTraining("default startbodyweight.com routine");
        }
      else
        {
          //TrainingManager.setInstance((TrainingManager)savedInstanceState.getSerializable((getString(R.string.TrainingActivityBundleTrainings))));
        }
    }

  @Override
  protected void onStop ()
    {
      // TrainingManager.getCurrentTraining().getCurrentExercisable().pause();

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

      //savedInstanceState.putSerializable(getString(R.string.TrainingActivityBundleTrainings), TrainingManager.getInstance());
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
          TrainingManager.clear();
          super.onBackPressed();
          return true;
        }

      return super.onOptionsItemSelected(item);
    }

  public void onContextButtonClicked (View view)
    {
      if (TrainingManager.getCurrentExercisable().isRunning())
        TrainingManager.getCurrentExercisable().pause();
      else if (TrainingManager.getCurrentTraining().isFinished())
        {
          TrainingStorage.finish();
          TrainingManager.clear();
          super.onBackPressed();
        }
      else
        TrainingManager.getCurrentExercisable().start();
    }

  public void onCurrentExerciseLevelLabelClicked (View view)
    {
      String url = TrainingManager.getCurrentExercisable().getImage();
      if (url == null)
        return;

      Bitmap bitmap;
      try
        {
          InputStream in = new java.net.URL(url).openStream();
          bitmap = BitmapFactory.decodeStream(in);
        }
      catch (Exception e)
        {
          Log.e("TrainingActivity", "error fetching image", e);
          return;
        }

      ImageView imageView = new ImageView(this);
      imageView.setImageBitmap(bitmap);

      Toast toast = new Toast(getApplicationContext());
      toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
      toast.setView(imageView);
      toast.show();
    }

}
