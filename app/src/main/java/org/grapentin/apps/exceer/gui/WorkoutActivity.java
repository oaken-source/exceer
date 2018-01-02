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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.ServiceBoundActivity;
import org.grapentin.apps.exceer.gui.fragments.ExerciseFragment;
import org.grapentin.apps.exceer.models.Workout;
import org.w3c.dom.Text;

public class WorkoutActivity extends ServiceBoundActivity implements SensorEventListener
{

  private ViewPager viewPager;
  private ViewPagerAdapter adapter;

  private ProgressBar progressBar;
  private Button contextButton;

  private SensorManager mSensorManager;

	private float[] m_lastMagFields = new float[3];
	private float[] m_lastAccels = new float[3];
	private float[] m_rotationMatrix = new float[16];
  private float[] m_lastLinAccels = new float[4];

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);

      adapter = new ViewPagerAdapter();

      progressBar = (ProgressBar) findViewById(R.id.TrainingActivityProgressBar);
      contextButton = (Button) findViewById(R.id.TrainingActivityContextButton);

      viewPager = (ViewPager) findViewById(R.id.TrainingActivityViewPager);
      viewPager.setAdapter(adapter);
      viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
      {
        @Override
        public void onPageSelected (int position)
          {
            progressBar.setProgress(position + 1);
            onFragmentStateChanged();
          }
      });

      mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      Log.d("moo", "" + mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
      Log.d("moo", "" + mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
      Log.d("moo", "" + mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
      Log.d("moo", "" + mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
      mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
    }

  public void onSensorChanged (SensorEvent event)
    {
      switch (event.sensor.getType())
        {
        case Sensor.TYPE_ACCELEROMETER:
          System.arraycopy(event.values, 0, m_lastAccels, 0, 3);
          break;
        case Sensor.TYPE_GRAVITY:
          System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);
          break;
        case Sensor.TYPE_LINEAR_ACCELERATION:
          System.arraycopy(event.values, 0, m_lastLinAccels, 0, 3);
        default:
          return;
        }

      float[] inv_rotationMatrix = new float[16];
      SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastAccels, m_lastMagFields);

      float[] res = new float[4];
      android.opengl.Matrix.invertM(inv_rotationMatrix, 0, m_rotationMatrix, 0);
      android.opengl.Matrix.multiplyMV(res, 0, inv_rotationMatrix, 0, m_lastLinAccels, 0);

      TextView labelX = (TextView) findViewById(R.id.label_x);
      labelX.setText(Float.toString(res[0]));
      TextView labelY = (TextView) findViewById(R.id.label_y);
      labelY.setText(Float.toString(res[1]));
      TextView labelZ = (TextView) findViewById(R.id.label_z);
      labelZ.setText(Float.toString(res[2]));
    }

  @Override
  public void onAccuracyChanged (Sensor sensor, int accuracy)
    {

    }

  @Override
  protected int getContentView ()
    {
      return R.layout.activity_workout;
    }

  @Override
  protected void onResume ()
    {
      super.onResume();
      // TODO: make workout id configurable
      adapter.setWorkout(Workout.get(1));
    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      getMenuInflater().inflate(R.menu.menu_training, menu);
      return true;
    }

  @Override
  public boolean onOptionsItemSelected (@NonNull MenuItem item)
    {
      int id = item.getItemId();

      switch (id)
        {
        case android.R.id.home:
          onBackPressed();
          return true;
        }

      return super.onOptionsItemSelected(item);
    }

  @Override
  public void onBackPressed ()
    {
      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick (DialogInterface dialog, int which)
          {
            switch (which)
              {
              case DialogInterface.BUTTON_POSITIVE:
                WorkoutActivity.super.onBackPressed();
                break;
              case DialogInterface.BUTTON_NEGATIVE:
                break;
              }
          }
      };

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getString(R.string.TrainingActivityAbortConfirmation));
      builder.setPositiveButton(getString(R.string.Yes), dialogClickListener);
      builder.setNegativeButton(getString(R.string.No), dialogClickListener);
      builder.show();
    }

  public void onContextButtonClicked (View view)
    {
      adapter.getCurrentFragment().onContextButtonClicked();
    }

  public void onFragmentStateChanged ()
    {
      switch (adapter.getCurrentFragment().getState())
        {
        case PREPARED:
          contextButton.setText(R.string.TrainingActivityContextButtonTextStart);
          break;
        case RUNNING:
          contextButton.setText(R.string.TrainingActivityContextButtonTextPause);
          break;
        case PAUSED:
          contextButton.setText(R.string.TrainingActivityContextButtonTextResume);
          break;
        case FINISHED:
          contextButton.setText(R.string.TrainingActivityContextButtonTextFinish);
          break;
        }

    }

  private class ViewPagerAdapter extends FragmentPagerAdapter
  {
    private Workout workout = null;

    public ViewPagerAdapter ()
      {
        super(getSupportFragmentManager());
      }

    @Override
    public Fragment getItem (int position)
      {
        return ExerciseFragment.newInstance(workout.getExercise(position));
      }

    @Override
    public int getCount ()
      {
        return (workout == null) ? 0 : workout.getNumberOfExercises();
      }

    public void setWorkout (Workout workout)
      {
        this.workout = workout;
        progressBar.setMax(getCount());
        progressBar.setProgress(viewPager.getCurrentItem() + 1);
        notifyDataSetChanged();
      }

    public ExerciseFragment getCurrentFragment ()
      {
        return getFragmentAt(viewPager.getCurrentItem());
      }

    public ExerciseFragment getFragmentAt (int position)
      {
        return (ExerciseFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.TrainingActivityViewPager + ":" + position);
      }
  }

}
