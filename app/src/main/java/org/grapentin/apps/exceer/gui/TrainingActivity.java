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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.gui.settings.TrainingSettingsActivity;
import org.grapentin.apps.exceer.models.Level;
import org.grapentin.apps.exceer.training.BaseExercisable;
import org.grapentin.apps.exceer.training.TrainingManager;

import java.io.InputStream;

public class TrainingActivity extends BaseActivity
{

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_training);
    }

  @Override
  protected void onResume ()
    {
      super.onResume();

    }

  @Override
  protected void onPause ()
    {
      super.onPause();

    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      getMenuInflater().inflate(R.menu.menu_training, menu);
      return true;
    }

  @Override
  public boolean onPrepareOptionsMenu (Menu menu)
    {
      BaseExercisable e = TrainingManager.getLeafExercisable();

      // allow setting levels when leaf exercise is level
      boolean showSettingsLevel = (e != null && e.getClass() == Level.class);
      // allow setting progress when leaf exercise knows progress
      boolean showSettingsProgress = (e != null && e.knowsProgress());

      // if we allow neither, don't show the menu
      return showSettingsLevel || showSettingsProgress;
    }

  @Override
  public boolean onOptionsItemSelected (@NonNull MenuItem item)
    {
      int id = item.getItemId();

      switch (id)
        {
        case R.id.action_training_settings:
          Intent progressSettingsIntent = new Intent(this, TrainingSettingsActivity.class);
          startActivity(progressSettingsIntent);
          break;
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
                TrainingManager.reset();
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
      if (TrainingManager.isRunning())
        TrainingManager.pause();
      else if (TrainingManager.isFinished())
        {
          TrainingManager.wrapUp();
          super.onBackPressed();
        }
      else
        TrainingManager.start();
    }

  public void onCurrentExerciseLevelLabelClicked (View view)
    {
      BaseExercisable ex = TrainingManager.getLeafExercisable();
      if (ex == null || ex.getImage() == null)
        return;

      Bitmap bitmap;
      try
        {
          InputStream in = new java.net.URL(ex.getImage()).openStream();
          bitmap = BitmapFactory.decodeStream(in);
        }
      catch (Exception e)
        {
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
