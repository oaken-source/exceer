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

import org.grapentin.apps.exceer.models.Session;
import org.grapentin.apps.exceer.models.Training;
import org.grapentin.apps.exceer.orm.Database;

import java.io.Serializable;

public class TrainingManager implements Serializable
{

  private final static TrainingManager instance = new TrainingManager();

  @Nullable
  private Training currentTraining = null;
  private int currentTrainingId = 0;

  private TrainingManager ()
    {

    }

  @Nullable
  public static BaseExercisable getLeafExercisable ()
    {
      assert instance.currentTraining != null;
      return instance.currentTraining.getLeafExercisable();
    }

  public static boolean isRunning ()
    {
      assert instance.currentTraining != null;
      return instance.currentTraining.isRunning();
    }

  public static boolean isFinished ()
    {
      assert instance.currentTraining != null;
      return instance.currentTraining.isFinished();
    }

  public static void prepare ()
    {
      if (instance.currentTraining != null)
        return;

      // TODO: get currentTrainingId from settings
      instance.currentTrainingId = 1;
      instance.currentTraining = Training.get(instance.currentTrainingId);
      // TODO: handle edge case where currentTrainingId is not valid
      instance.currentTraining.prepare();
    }

  public static void next ()
    {
      assert instance.currentTraining != null;
      instance.currentTraining.next();
    }

  public static void reset ()
    {
      assert instance.currentTraining != null;
      instance.currentTraining.reset();
      instance.currentTraining = null;
    }

  public static void start ()
    {
      assert instance.currentTraining != null;
      instance.currentTraining.start();
    }

  public static void pause ()
    {
      assert instance.currentTraining != null;
      instance.currentTraining.pause();
    }

  public static void wrapUp ()
    {
      assert instance.currentTraining != null;
      instance.currentTraining.wrapUp();
      Database.add(new Session(instance.currentTraining.getId()));
      instance.currentTraining = null;
    }

}
