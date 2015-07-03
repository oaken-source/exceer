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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.TrainingActivity;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.helpers.Sounds;
import org.grapentin.apps.exceer.helpers.Tasks;

abstract public class BaseExercisable
{

  protected Properties props = new Properties();

  @Nullable
  private Tasks.TimerTask task = null;

  private boolean running = false;

  abstract protected void levelUp ();

  abstract protected String getCurrentProgress ();

  abstract protected void setCurrentProgress (String progress);

  public boolean isRunning ()
    {
      return running;
    }

  @Nullable
  public String getImage ()
    {
      return props.image;
    }

  protected void prepare ()
    {
      if (props.reps_begin != null)
        {
          String progress = getCurrentProgress();
          Reps reps = ((progress == null) ? new Reps(props.reps_begin) : Reps.fromString(progress));
          setCurrentProgress(reps.toString());

          task = new RepsTask(reps);
        }
      else if (props.duration_begin != null)
        {
          String progress = getCurrentProgress();
          Duration duration = ((progress == null) ? props.duration_begin : Duration.fromString(progress));
          setCurrentProgress(duration.toString());

          task = new DurationTask(duration);
        }
      else
        {
          task = new DurationTask(props.duration);
        }
    }

  @SuppressWarnings("WeakerAccess")
  public void reset ()
    {
      if (task != null)
        task.stop();
    }

  public void show ()
    {
      if (props.reps_begin != null)
        {
          Reps reps = Reps.fromString(getCurrentProgress());

          TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText("1:0/" + reps.sets.get(0));
        }
      else if (props.duration_begin != null)
        {
          Duration duration = Duration.fromString(getCurrentProgress());

          long min = duration.get() / 60000;
          long sec = (duration.get() % 60000) / 1000;

          TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);
        }
      else if (props.duration != null)
        {
          long min = props.duration.get() / 60000;
          long sec = (props.duration.get() % 60000) / 1000;

          TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);
        }
    }

  public void start ()
    {
      assert task != null;
      task.start();
      Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
      contextButton.setText(BaseActivity.getContext().getString(R.string.TrainingActivityContextButtonTextPause));
      running = true;
    }

  public void pause ()
    {
      assert task != null;
      task.pause();
      Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
      contextButton.setText(BaseActivity.getContext().getString(R.string.TrainingActivityContextButtonTextStart));
      running = false;
    }

  private void finishExercise ()
    {
      running = false;

      if (props.reps_begin != null)
        {
          DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
          {
            @Override
            public void onClick (DialogInterface dialog, int which)
              {
                switch (which)
                  {
                  case DialogInterface.BUTTON_POSITIVE:
                    increment();
                    break;
                  case DialogInterface.BUTTON_NEGATIVE:
                    break;
                  }
              }
          };

          AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.getInstance());
          builder.setMessage("Did you make it?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }
      else if (props.duration_begin != null)
        {
          TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText("00:00");

          DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
          {
            @Override
            public void onClick (DialogInterface dialog, int which)
              {
                switch (which)
                  {
                  case DialogInterface.BUTTON_POSITIVE:
                    increment();
                    break;
                  case DialogInterface.BUTTON_NEGATIVE:
                    break;
                  }
              }
          };

          AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.getInstance());
          builder.setMessage("Did you make it?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }
      else
        {
          TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText("00:00");
        }

      if (props.pause_after_exercise.get() > 0)
        {
          task = new PauseTask(props.pause_after_exercise.get());
          task.start();
        }
      else
        afterPause();
    }

  private void afterPause ()
    {
      Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
      contextButton.setEnabled(true);
      contextButton.setText(BaseActivity.getContext().getString(R.string.TrainingActivityContextButtonTextStart));

      TrainingManager.next();
    }

  private void increment ()
    {
      if (props.reps_begin != null && props.reps_finish != null)
        {
          Reps reps = Reps.fromString(getCurrentProgress());

          if (props.reps_finish != null && reps.compareTo(props.reps_finish) >= 0)
            {
              levelUp();
              return;
            }

          reps.increment(props.reps_increment, props.reps_finish, props.reps_increment_direction, props.reps_increment_style);
          setCurrentProgress(reps.toString());
        }
      else if (props.duration_begin != null && props.duration_finish != null)
        {
          Duration duration = Duration.fromString(getCurrentProgress());

          if (props.duration_finish != null && duration.compareTo(props.duration_finish) >= 0)
            {
              levelUp();
              return;
            }

          duration.increment(props.duration_increment);
          setCurrentProgress(duration.toString());
        }
    }

  public boolean knowsProgress ()
    {
      return props.reps_begin != null || props.duration_begin != null;
    }

  private class DurationTask extends Tasks.TimerTask
  {
    private final Duration duration;

    private long start = 0;
    private long paused = 0;
    private long countdown = 3;
    private boolean halftime = false;

    public DurationTask (Duration duration)
      {
        this.duration = duration;
      }

    public void pause ()
      {
        if (start > 0)
          paused = System.currentTimeMillis();
        else
          countdown = 3;

        super.stop();
      }

    public long update ()
      {
        if (start == 0 && countdown > 0)
          {
            Sounds.play(R.raw.beep_low);
            countdown--;
            return System.currentTimeMillis() + 1000;
          }

        if (start == 0)
          {
            Sounds.play(R.raw.beep_four);
            start = System.currentTimeMillis();
            paused = 0;
            countdown = 3;
          }

        if (paused > 0)
          {
            start += System.currentTimeMillis() - paused;
            paused = 0;
          }

        long elapsed = System.currentTimeMillis() - start;
        long remaining = Math.round((duration.get() - elapsed) / 1000.0);

        if (props.two_sided && !halftime && duration.get() / elapsed >= 0.5)
          {
            Sounds.play(R.raw.beep_two);
            halftime = true;
          }

        if (countdown > 0 && countdown >= remaining)
          {
            Sounds.play(R.raw.beep_low);
            countdown--;
          }

        if (remaining <= 0)
          {
            Sounds.play(R.raw.beep_four);
            finishExercise();
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
        progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);
        ProgressBar progressBar = (ProgressBar)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressBar);
        progressBar.setMax((int)duration.get());
        progressBar.setProgress((int)(duration.get() - remaining));

        return start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }
  }

  private class RepsTask extends Tasks.TimerTask
  {
    private final Reps reps;
    private final Reps done;

    private int currentSet = 0;
    private int phase = 0;

    private long pause_start = 0;
    private long pause_duration = 0;
    private long pause_countdown = 0;

    private long countdown = 3;
    private int next_sound = 0;

    public RepsTask (Reps reps)
      {
        this.reps = reps;
        this.done = reps.empty();
      }

    public void pause ()
      {
        countdown = 3;
        phase = 0;

        super.pause();
      }

    public long update ()
      {
        if (countdown > 0)
          {
            Sounds.play(R.raw.beep_low);
            countdown--;
            next_sound = R.raw.beep_four;
            return System.currentTimeMillis() + 1000;
          }

        if (pause_duration > 0)
          return updateSetPause();

        phase = (phase + 1) % 5;

        // phase 0: pause
        if (phase == 0)
          {
            done.sets.set(currentSet, done.sets.get(currentSet) + 1);

            TextView progressLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityProgressLabel);
            progressLabel.setText("" + (currentSet + 1) + ":" + done.sets.get(currentSet) + "/" + reps.sets.get(currentSet));

            if (done.sets.get(currentSet) >= reps.sets.get(currentSet)) // a set finished
              {
                Sounds.play(R.raw.beep_four);
                next_sound = 0;
                currentSet++;

                if (currentSet >= reps.sets.size()) // exercise finished
                  {
                    finishExercise();
                    return 0;
                  }

                progressLabel.setText("" + (currentSet + 1) + ":" + done.sets.get(currentSet) + "/" + reps.sets.get(currentSet));

                if (props.pause_after_set.get() > 0)
                  {
                    pause_duration = props.pause_after_set.get();
                    return System.currentTimeMillis();
                  }
              }
            phase++;
          }

        if (next_sound != 0)
          {
            Sounds.play(next_sound);
            next_sound = 0;
          }

        // phase 1: primary motion
        if (phase == 1)
          {
            next_sound = (props.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_high : R.raw.beep_low);
            if (props.primary_motion == Properties.PrimaryMotion.concentric && props.reps_duration_concentric.get() > 0)
              return System.currentTimeMillis() + props.reps_duration_concentric.get();
            if (props.primary_motion == Properties.PrimaryMotion.eccentric && props.reps_duration_eccentric.get() > 0)
              return System.currentTimeMillis() + props.reps_duration_eccentric.get();
            phase++; // skip phase if no time set
          }

        // phase 2: pause after primary motion
        if (phase == 2)
          {
            next_sound = (props.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_high : R.raw.beep_low);
            if (props.primary_motion == Properties.PrimaryMotion.concentric && props.reps_pause_after_concentric.get() > 0)
              return System.currentTimeMillis() + props.reps_pause_after_concentric.get();
            if (props.primary_motion == Properties.PrimaryMotion.eccentric && props.reps_pause_after_eccentric.get() > 0)
              return System.currentTimeMillis() + props.reps_pause_after_eccentric.get();
            phase++; // skip phase if no time set
          }

        // phase 3: secondary motion
        if (phase == 3)
          {
            next_sound = (props.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_low : R.raw.beep_high);
            if (props.primary_motion == Properties.PrimaryMotion.concentric && props.reps_duration_eccentric.get() > 0)
              return System.currentTimeMillis() + props.reps_duration_eccentric.get();
            if (props.primary_motion == Properties.PrimaryMotion.eccentric && props.reps_duration_concentric.get() > 0)
              return System.currentTimeMillis() + props.reps_duration_concentric.get();
            phase++; // skip phase if no time set
          }

        // phase 3: pause after secondary motion
        if (phase == 4)
          {
            next_sound = (props.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_low : R.raw.beep_high);
            if (props.primary_motion == Properties.PrimaryMotion.concentric && props.reps_pause_after_eccentric.get() > 0)
              return System.currentTimeMillis() + props.reps_pause_after_eccentric.get();
            if (props.primary_motion == Properties.PrimaryMotion.eccentric && props.reps_pause_after_concentric.get() > 0)
              return System.currentTimeMillis() + props.reps_pause_after_concentric.get();
          }

        return System.currentTimeMillis();
      }

    private long updateSetPause ()
      {
        if (pause_start == 0)
          {
            pause_start = System.currentTimeMillis();
            pause_countdown = 3;
          }

        long elapsed = System.currentTimeMillis() - pause_start;
        long remaining = Math.round((pause_duration - elapsed) / 1000.0);

        if (pause_countdown > 0 && pause_countdown >= remaining)
          {
            Sounds.play(R.raw.beep_low);
            pause_countdown--;
          }

        if (remaining <= 0)
          {
            Sounds.play(R.raw.beep_four);
            pause_duration = 0;
            pause_start = 0;
            countdown = 3;
            running = false;
            Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
            contextButton.setEnabled(true);
            contextButton.setText(BaseActivity.getContext().getString(R.string.TrainingActivityContextButtonTextStart));
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
        contextButton.setEnabled(false);
        contextButton.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        return pause_start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }

  }

  private class PauseTask extends Tasks.TimerTask
  {
    private final long duration;

    private long start = 0;
    private long countdown = 3;

    public PauseTask (long duration)
      {
        this.duration = duration;
      }

    public long update ()
      {
        if (start == 0)
          start = System.currentTimeMillis();

        long elapsed = System.currentTimeMillis() - start;
        long remaining = Math.round((duration - elapsed) / 1000.0);

        if (countdown > 0 && countdown >= remaining)
          {
            Sounds.play(R.raw.beep_low);
            countdown--;
          }

        if (remaining <= 0)
          {
            Sounds.play(R.raw.beep_four);
            afterPause();
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
        contextButton.setEnabled(false);
        contextButton.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        return start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }
  }

}
