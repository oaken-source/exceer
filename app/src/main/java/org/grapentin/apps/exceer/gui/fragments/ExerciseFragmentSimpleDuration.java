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

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.CountDownTimer;
import org.grapentin.apps.exceer.service.AudioService;
import org.grapentin.apps.exceer.training.Duration;

public class ExerciseFragmentSimpleDuration extends ExerciseFragment
{

  private Duration duration;

  private CountDownTimer timer;

  @Override
  public void onStart ()
    {
      super.onStart();

      duration = exercise.getDuration();

      progressBar.setMax(duration.getProgressMax());
      progressLabel.setText(duration.toProgressString());
    }

  @Override
  protected void start ()
    {
      state = ExerciseState.RUNNING;

      timer = new CountDown();
      timer.start();
    }

  @Override
  protected void pause ()
    {
      state = ExerciseState.PAUSED;
    }

  @Override
  protected void resume ()
    {
      state = ExerciseState.RUNNING;
    }

  @Override
  protected void finish ()
    {
      // ...
    }

  private class CountDown extends CountDownTimer
  {
    public CountDown ()
      {
        super(3000, 1000);
      }

    @Override
    public void onTick (long millisUntilFinished)
      {
        AudioService.play(R.raw.beep_low);
      }

    @Override
    public void onFinish ()
      {
        AudioService.play(R.raw.beep_four);
      }
  }

}
