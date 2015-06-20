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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.grapentin.apps.exceer.orm.Database;
import org.grapentin.apps.exceer.models.Session;
import org.grapentin.apps.exceer.models.Training;

import java.io.Serializable;

public class TrainingManager implements Serializable
{

  private static TrainingManager instance = new TrainingManager();

  @Nullable
  private Training currentTraining = null;
  private int currentTrainingId = 0;

  private TrainingManager ()
    {

    }

  @NonNull
  private static TrainingManager getInstance ()
    {
      return instance;
    }

  @Nullable
  public static BaseExercisable getLeafExercisable ()
    {
      assert getInstance().currentTraining != null;
      return getInstance().currentTraining.getLeafExercisable();
    }

  public static boolean isRunning ()
    {
      assert getInstance().currentTraining != null;
      return getInstance().currentTraining.isRunning();
    }

  public static boolean isFinished ()
    {
      assert getInstance().currentTraining != null;
      return getInstance().currentTraining.isFinished();
    }

  public static void prepare ()
    {
      if (getInstance().currentTraining != null)
        return;

      // TODO: get currentTrainingId from settings
      getInstance().currentTrainingId = 1;
      getInstance().currentTraining = Training.get(getInstance().currentTrainingId);
      // TODO: handle edge case where currentTrainingId is not valid
      getInstance().currentTraining.prepare();
    }

  public static void next ()
    {
      assert getInstance().currentTraining != null;
      getInstance().currentTraining.next();
    }

  public static void reset ()
    {
      assert getInstance().currentTraining != null;
      getInstance().currentTraining.reset();
      getInstance().currentTraining = null;
    }

  public static void start ()
    {
      assert getInstance().currentTraining != null;
      getInstance().currentTraining.start();
    }

  public static void pause ()
    {
      assert getInstance().currentTraining != null;
      getInstance().currentTraining.pause();
    }

  public static void wrapUp ()
    {
      assert getInstance().currentTraining != null;
      getInstance().currentTraining.wrapUp();
      Database.add(new Session(getInstance().currentTraining.getId()));
      getInstance().currentTraining = null;
    }

}
