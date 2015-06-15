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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.managers.ContextManager;
import org.grapentin.apps.exceer.managers.SoundManager;
import org.grapentin.apps.exceer.managers.TaskManager;

import java.io.Serializable;

abstract public class Exercisable implements Serializable
{

  protected Properties properties;

  private TaskManager.TimerTask task;

  private String old_result = null;
  private String new_result = null;

  private boolean running = false;

  protected Exercisable (Properties properties)
    {
      this.properties = new Properties(properties);
    }

  abstract public Exercisable getCurrentExercisable ();

  abstract public String fetchResult ();

  abstract public void recordResult (String result);

  abstract public boolean levelUp ();

  protected void setProperty (String key, String value)
    {
      properties.set(key, value);
    }

  public boolean isRunning ()
    {
      return running;
    }

  public String getImage ()
    {
      return properties.image;
    }

  public void prepare ()
    {
      if (properties.reps_begin != null)
        {
          Reps reps = new Reps(properties.reps_begin);
          old_result = fetchResult();
          if (old_result != null)
            {
              reps = new Reps(old_result);
              if (reps.greaterOrEqual(properties.reps_finish))
                {
                  if (levelUp())
                    return;
                }
              for (int i = 0; i < properties.reps_increment; ++i)
                reps.increment(properties);
            }
          new_result = reps.toString();

          TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText("1:0/" + reps.sets.get(0));

          task = new RepsTask(reps);
        }
      else if (properties.duration_begin > 0)
        {
          long duration = properties.duration_begin;
          old_result = fetchResult();
          if (old_result != null)
            {
              duration = Long.parseLong(old_result);
              if (duration >= properties.duration_finish)
                {
                  if (levelUp())
                    return;
                }
              duration += properties.duration_increment;
            }
          new_result = "" + duration;

          long min = duration / 60000;
          long sec = (duration % 60000) / 1000;

          TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

          task = new DurationTask(duration);
        }
      else
        {
          long min = properties.duration / 60000;
          long sec = (properties.duration % 60000) / 1000;

          TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

          task = new DurationTask(properties.duration);
        }
    }

  public void start ()
    {
      task.start();
      Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
      contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextPause));
      running = true;
    }

  public void pause ()
    {
      task.pause();
      Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
      contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextStart));
      running = false;
    }

  public void finishExercise ()
    {
      running = false;

      if (properties.reps_begin != null)
        {
          DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
          {
            @Override
            public void onClick (DialogInterface dialog, int which)
              {
                switch (which)
                  {
                  case DialogInterface.BUTTON_POSITIVE:
                    recordResult(new_result);
                    break;
                  case DialogInterface.BUTTON_NEGATIVE:
                    recordResult(old_result);
                    break;
                  }
              }
          };

          AlertDialog.Builder builder = new AlertDialog.Builder(TrainingManager.getGui());
          builder.setMessage("Did you make it?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }
      else if (properties.duration_begin > 0)
        {
          TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText("00:00");

          DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
          {
            @Override
            public void onClick (DialogInterface dialog, int which)
              {
                switch (which)
                  {
                  case DialogInterface.BUTTON_POSITIVE:
                    recordResult(new_result);
                    break;
                  case DialogInterface.BUTTON_NEGATIVE:
                    recordResult(old_result);
                    break;
                  }
              }
          };

          AlertDialog.Builder builder = new AlertDialog.Builder(TrainingManager.getGui());
          builder.setMessage("Did you make it?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }
      else
        {
          TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
          progressLabel.setText("00:00");
        }

      if (properties.pause_after_exercise > 0)
        {
          task = new PauseTask(properties.pause_after_exercise);
          task.start();
        }
      else
        afterPause();
    }

  private void afterPause ()
    {
      Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
      contextButton.setEnabled(true);
      contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextStart));

      TrainingManager.next();
    }

  private class DurationTask extends TaskManager.TimerTask
  {
    private long duration;

    private long start = 0;
    private long paused = 0;
    private long countdown = 3;
    private boolean halftime = false;

    public DurationTask (long duration)
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
            SoundManager.play(R.raw.beep_low);
            countdown--;
            return System.currentTimeMillis() + 1000;
          }

        if (start == 0)
          {
            SoundManager.play(R.raw.beep_four);
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
        long remaining = Math.round((duration - elapsed) / 1000.0);

        if (properties.two_sided && !halftime && duration / elapsed >= 0.5)
          {
            SoundManager.play(R.raw.beep_two);
            halftime = true;
          }

        if (countdown > 0 && countdown >= remaining)
          {
            SoundManager.play(R.raw.beep_low);
            countdown--;
          }

        if (remaining <= 0)
          {
            SoundManager.play(R.raw.beep_four);
            finishExercise();
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
        progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        return start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }
  }

  private class RepsTask extends TaskManager.TimerTask
  {
    private Reps reps;
    private Reps done;

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
            SoundManager.play(R.raw.beep_low);
            countdown--;
            next_sound = R.raw.beep_four;
            return System.currentTimeMillis() + 1000;
          }

        if (pause_duration > 0)
          return updateSetPause();

        phase = (phase + 1) % 5;

        Log.d("RepsTask", "arriving in phase " + phase);

        // phase 0: pause
        if (phase == 0)
          {
            done.sets.set(currentSet, done.sets.get(currentSet) + 1);

            TextView progressLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityProgressLabel);
            progressLabel.setText("" + (currentSet + 1) + ":" + done.sets.get(currentSet) + "/" + reps.sets.get(currentSet));

            if (done.sets.get(currentSet) >= reps.sets.get(currentSet)) // a set finished
              {
                SoundManager.play(R.raw.beep_four);
                next_sound = 0;
                currentSet++;

                if (currentSet >= reps.sets.size()) // exercise finished
                  {
                    finishExercise();
                    return 0;
                  }

                progressLabel.setText("" + (currentSet + 1) + ":" + done.sets.get(currentSet) + "/" + reps.sets.get(currentSet));

                if (properties.pause_after_set > 0)
                  {
                    pause_duration = properties.pause_after_set;
                    return System.currentTimeMillis();
                  }
              }
            phase++;
          }

        if (next_sound != 0)
          {
            SoundManager.play(next_sound);
            next_sound = 0;
          }

        // phase 1: primary motion
        if (phase == 1)
          {
            next_sound = (properties.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_high : R.raw.beep_low);
            if (properties.primary_motion == Properties.PrimaryMotion.concentric && properties.reps_duration_concentric > 0)
              return System.currentTimeMillis() + properties.reps_duration_concentric;
            if (properties.primary_motion == Properties.PrimaryMotion.eccentric && properties.reps_duration_eccentric > 0)
              return System.currentTimeMillis() + properties.reps_duration_eccentric;
            phase++; // skip phase if no time set
          }

        // phase 2: pause after primary motion
        if (phase == 2)
          {
            next_sound = (properties.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_high : R.raw.beep_low);
            if (properties.primary_motion == Properties.PrimaryMotion.concentric && properties.reps_pause_after_concentric > 0)
              return System.currentTimeMillis() + properties.reps_pause_after_concentric;
            if (properties.primary_motion == Properties.PrimaryMotion.eccentric && properties.reps_pause_after_eccentric > 0)
              return System.currentTimeMillis() + properties.reps_pause_after_eccentric;
            phase++; // skip phase if no time set
          }

        // phase 3: secondary motion
        if (phase == 3)
          {
            next_sound = (properties.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_low : R.raw.beep_high);
            if (properties.primary_motion == Properties.PrimaryMotion.concentric && properties.reps_duration_eccentric > 0)
              return System.currentTimeMillis() + properties.reps_duration_eccentric;
            if (properties.primary_motion == Properties.PrimaryMotion.eccentric && properties.reps_duration_concentric > 0)
              return System.currentTimeMillis() + properties.reps_duration_concentric;
            phase++; // skip phase if no time set
          }

        // phase 3: pause after secondary motion
        if (phase == 4)
          {
            next_sound = (properties.primary_motion == Properties.PrimaryMotion.concentric ? R.raw.beep_low : R.raw.beep_high);
            if (properties.primary_motion == Properties.PrimaryMotion.concentric && properties.reps_pause_after_eccentric > 0)
              return System.currentTimeMillis() + properties.reps_pause_after_eccentric;
            if (properties.primary_motion == Properties.PrimaryMotion.eccentric && properties.reps_pause_after_concentric > 0)
              return System.currentTimeMillis() + properties.reps_pause_after_concentric;
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
            SoundManager.play(R.raw.beep_low);
            pause_countdown--;
          }

        if (remaining <= 0)
          {
            SoundManager.play(R.raw.beep_four);
            pause_duration = 0;
            pause_start = 0;
            countdown = 3;
            running = false;
            Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
            contextButton.setEnabled(true);
            contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextStart));
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
        contextButton.setEnabled(false);
        contextButton.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        return pause_start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }

  }

  private class PauseTask extends TaskManager.TimerTask
  {
    private long duration;

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
            SoundManager.play(R.raw.beep_low);
            countdown--;
          }

        if (remaining <= 0)
          {
            SoundManager.play(R.raw.beep_four);
            afterPause();
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
        contextButton.setEnabled(false);
        contextButton.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        return start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }
  }

}
