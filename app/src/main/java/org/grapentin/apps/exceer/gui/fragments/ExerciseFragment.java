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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.WorkoutActivity;
import org.grapentin.apps.exceer.models.Exercise;

public class ExerciseFragment extends Fragment
{

  protected Exercise exercise;

  protected ProgressBar progressBar;
  protected TextView progressLabel;

  protected ExerciseState state = ExerciseState.PREPARED;

  public static Fragment newInstance (Exercise exercise)
    {
      Bundle args = new Bundle();
      args.putSerializable("exercise", exercise);

      Fragment f;
      if (exercise.getDuration() != null)
        f = new ExerciseFragmentSimpleDuration();
      else if (exercise.getDurationBegin() != null)
        f = new ExerciseFragmentProgressDuration();
      else if (exercise.getRepsBegin() != null)
        f = new ExerciseFragmentProgressReps();
      else
        f = new ExerciseFragment();

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

      TextView exerciseNameLabel = (TextView)v.findViewById(R.id.ExerciseFragmentExerciseNameLabel);
      TextView levelLabel = (TextView)v.findViewById(R.id.ExerciseFragmentLevelLabel);
      TextView levelNameLabel = (TextView)v.findViewById(R.id.ExerciseFragmentLevelNameLabel);

      if (exercise.isLevel())
        {
          exerciseNameLabel.setText(exercise.getParentExercise().getName());
          levelLabel.setText(String.format("%1$s %2$s", getString(R.string.ExerciseFragmentExerciseLevel), exercise.getParentExercise().getLevelNumber(exercise)));
          levelNameLabel.setText(exercise.getName());
        }
      else
        {
          exerciseNameLabel.setText(exercise.getName());
          levelLabel.setText("");
          levelNameLabel.setText("");
        }

      progressBar = (ProgressBar)v.findViewById(R.id.ExerciseFragmentProgressBar);
      progressLabel = (TextView)v.findViewById(R.id.ExerciseFragmentProgressLabel);

      return v;
    }

  public ExerciseState getState ()
    {
      return state;
    }

  public void onContextButtonClicked ()
    {
      switch (state)
        {
        case PREPARED:
          start();
          break;
        case RUNNING:
          pause();
          break;
        case PAUSED:
          resume();
          break;
        case FINISHED:
          finish();
          break;
        }

      getTrainingActivity().onFragmentStateChanged();
    }

  public WorkoutActivity getTrainingActivity ()
    {
      return (WorkoutActivity)getActivity();
    }

  protected void start ()
    {

    }

  protected void pause ()
    {

    }

  protected void resume ()
    {

    }

  protected void finish ()
    {

    }

  public enum ExerciseState
  {
    PREPARED,
    RUNNING,
    PAUSED,
    FINISHED
  }

}
