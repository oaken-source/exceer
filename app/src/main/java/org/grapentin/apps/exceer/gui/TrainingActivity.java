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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.ServiceBoundActivity;
import org.grapentin.apps.exceer.gui.fragments.ExerciseFragment;
import org.grapentin.apps.exceer.models.Training;

public class TrainingActivity extends ServiceBoundActivity
{

  private ViewPager viewPager;
  private ViewPagerAdapter adapter;

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_training);

      adapter = new ViewPagerAdapter();

      viewPager = (ViewPager)findViewById(R.id.TrainingActivityViewPager);
      viewPager.setAdapter(adapter);
    }

  @Override
  protected void onResume ()
    {
      super.onResume();
      // TODO: make training id configurable
      adapter.setTraining(Training.get(1));
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
                TrainingActivity.super.onBackPressed();
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
      // ...
    }

  private class ViewPagerAdapter extends FragmentPagerAdapter
  {
    private Training training = null;

    public ViewPagerAdapter ()
      {
        super(getSupportFragmentManager());
      }

    @Override
    public Fragment getItem (int position)
      {
        return ExerciseFragment.newInstance(training.getExercise(position));
      }

    @Override
    public int getCount ()
      {
        return (training == null) ? 0 : training.getNumberOfExercises();
      }

    public void setTraining (Training training)
      {
        this.training = training;
        notifyDataSetChanged();
      }

  }

}
