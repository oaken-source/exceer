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

package org.grapentin.apps.exceer.training;

import android.support.annotation.Nullable;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.models.Session;
import org.grapentin.apps.exceer.models.Training;
import org.grapentin.apps.exceer.service.DatabaseService;

import java.io.Serializable;

public class TrainingManager implements Serializable
{

  @Nullable
  private static Training currentTraining = null;
  private static int currentTrainingId = 0;

  private static TrainingState state = TrainingState.NOT_SET;

  public static TrainingState getState ()
    {
      return state;
    }

  private static void setState (TrainingState s)
    {
      state = s;
      switch (state)
        {
        case PREPARED:
          BaseActivity.setText(R.id.TrainingActivityContextButton, R.string.TrainingActivityContextButtonTextStart);
          break;
        case RUNNING:
          BaseActivity.setText(R.id.TrainingActivityContextButton, R.string.TrainingActivityContextButtonTextPause);
          break;
        case PAUSED:
          BaseActivity.setText(R.id.TrainingActivityContextButton, R.string.TrainingActivityContextButtonTextResume);
          break;
        case FINISHED:
          BaseActivity.setText(R.id.TrainingActivityContextButton, R.string.TrainingActivityContextButtonTextFinish);
          break;
        }
    }

  @Nullable
  public static BaseExercisable getLeafExercisable ()
    {
      assert currentTraining != null;
      return currentTraining.getLeafExercisable();
    }

  public static void onCreate ()
    {
      if (state != TrainingState.NOT_SET)
        return;

      // TODO: get currentTrainingId from settings
      currentTrainingId = 1;
      currentTraining = Training.get(currentTrainingId);
      // TODO: handle edge case where currentTrainingId is not valid
    }

  public static void onResume ()
    {
      if (currentTraining == null)
        return;

      currentTraining.prepare();
      setState(TrainingState.PREPARED);
    }

  public static void onPause ()
    {
      // just keep running
    }

  public static void onAbort ()
    {
      if (currentTraining == null)
        return;

      currentTraining.reset();
      currentTraining = null;
      setState(TrainingState.NOT_SET);
    }

  public static void start ()
    {
      if (currentTraining == null)
        return;

      currentTraining.start();
      setState(TrainingState.RUNNING);
    }

  public static void pause ()
    {
      if (currentTraining == null)
        return;

      currentTraining.pause();
      setState(TrainingState.PAUSED);
    }

  public static void resume ()
    {
      if (currentTraining == null)
        return;

      currentTraining.resume();
      setState(TrainingState.RUNNING);
    }

  public static void wrapUp ()
    {
      if (currentTraining == null)
        return;

      currentTraining.wrapUp();
      DatabaseService.add(new Session(currentTraining));
      currentTraining = null;
      setState(TrainingState.NOT_SET);
    }

  public static void next ()
    {
      if (currentTraining == null)
        return;

      if (currentTraining.next())
        setState(TrainingState.PREPARED);
      else
        setState(TrainingState.FINISHED);
    }

  public enum TrainingState
  {
    NOT_SET,
    PREPARED,
    RUNNING,
    PAUSED,
    FINISHED
  }

}
