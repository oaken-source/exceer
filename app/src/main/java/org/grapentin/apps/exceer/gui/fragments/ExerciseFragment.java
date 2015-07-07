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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.models.Exercise;

public class ExerciseFragment extends Fragment
{

  private Exercise exercise;

  public static Fragment newInstance (Exercise exercise)
    {
      Bundle args = new Bundle();
      args.putSerializable("exercise", exercise);

      Fragment f = new ExerciseFragment();
      f.setArguments(args);
      return f;
    }

  @Override
  public void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);

      Bundle args = getArguments();
      exercise = (Exercise)args.getSerializable("exercise");
    }

  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
      View v = inflater.inflate(R.layout.fragment_exercise, container, false);

      String exerciseName = exercise.getName();
      String level = "";
      String levelName = "";
      if (exercise.getParentExercise() != null && exercise.getParentExercise().getExerciseChildrenType() == Exercise.ExerciseChildrenType.progressing)
        {
          exerciseName = exercise.getParentExercise().getName();
          level = "level 1";
          levelName = exercise.getName();
        }

      TextView exerciseNameLabel = (TextView)v.findViewById(R.id.ExerciseFragmentExerciseNameLabel);
      exerciseNameLabel.setText(exerciseName);
      TextView levelLabel = (TextView)v.findViewById(R.id.ExerciseFragmentLevelLabel);
      levelLabel.setText(level);
      TextView levelNameLabel = (TextView)v.findViewById(R.id.ExerciseFragmentLevelNameLabel);
      levelNameLabel.setText(levelName);

      return v;
    }

}
