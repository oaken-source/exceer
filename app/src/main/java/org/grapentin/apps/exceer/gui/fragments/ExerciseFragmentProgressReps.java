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

package org.grapentin.apps.exceer.gui.fragments;

import org.grapentin.apps.exceer.training.Reps;

public class ExerciseFragmentProgressReps extends ExerciseFragment
{

  private Reps reps;

  @Override
  public void onStart ()
    {
      super.onStart();

      String current = exercise.getProgress();
      reps = (current == null) ? exercise.getRepsBegin() : Reps.fromString(current);

      progressBar.setMax(reps.getProgressMax());
      progressLabel.setText(reps.toProgressString());
    }

}
