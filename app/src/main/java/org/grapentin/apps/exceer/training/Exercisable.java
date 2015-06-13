/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.managers.ContextManager;
import org.grapentin.apps.exceer.managers.SoundManager;
import org.grapentin.apps.exceer.managers.TaskManager;

import java.io.Serializable;

abstract public class Exercisable implements Serializable
{

  protected Activity gui;
  protected Properties properties;

  private TaskManager.TimerTask task;

  private boolean running = false;

  protected Exercisable (Properties properties, Activity gui)
    {
      this.gui = gui;
      this.properties = new Properties(properties);
    }

  abstract public Exercisable getCurrentExercisable ();

  protected void setProperty (String key, String value)
    {
      properties.set(key, value);
    }

  public boolean isFinished ()
    {
      return false;
    }

  public boolean isRunning ()
    {
      return running;
    }

  public void prepare ()
    {
      if (properties.reps_begin != null)
        prepareReps();
      else if (properties.duration_begin != null)
        prepareDuration();
      else
        prepareDurationStatic();
    }

  private void prepareReps ()
    {

    }

  private void prepareDuration ()
    {

    }

  private void prepareDurationStatic ()
    {
      long min = properties.duration / 60000;
      long sec = (properties.duration % 60000) / 1000;

      TextView progressLabel = (TextView)gui.findViewById(R.id.TrainingActivityProgressLabel);
      progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

      task = new DurationTask(properties.duration);
    }

  public void start ()
    {
      task.start();
      Button contextButton = (Button)gui.findViewById(R.id.TrainingActivityContextButton);
      contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextPause));
      running = true;
    }

  public void pause ()
    {
      task.pause();
      Button contextButton = (Button)gui.findViewById(R.id.TrainingActivityContextButton);
      contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextStart));
      running = false;
    }

  public void finish ()
    {
      if (properties.reps_begin != null)
        finishReps();
      else if (properties.duration_begin != null)
        finishDuration();
      else
        finishDurationStatic();


    }

  private void finishReps ()
    {

    }

  private void finishDuration ()
    {

    }

  private void finishDurationStatic ()
    {
      TextView progressLabel = (TextView)gui.findViewById(R.id.TrainingActivityProgressLabel);
      progressLabel.setText("00:00");
    }

  private class DurationTask extends TaskManager.TimerTask
  {
    private long duration;
    private long start = 0;
    private long paused = 0;

    private long countdown = 3;

    public DurationTask (long duration)
      {
        this.duration = duration;
      }

    public void pause ()
      {
        paused = System.currentTimeMillis();
        super.stop();
      }

    public long update ()
      {
        if (start == 0)
          start = System.currentTimeMillis();

        if (paused > 0)
          {
            start += System.currentTimeMillis() - paused;
            paused = 0;
          }

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
            finish();
            return 0;
          }

        long min = remaining / 60;
        long sec = remaining % 60;

        TextView progressLabel = (TextView)gui.findViewById(R.id.TrainingActivityProgressLabel);
        progressLabel.setText((min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        return start + (Math.round(elapsed / 1000.0) + 1) * 1000;
      }
  }

}
