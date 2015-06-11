/*
 * comment
 */

package org.grapentin.apps.exceer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.grapentin.apps.exceer.managers.SoundManager;
import org.grapentin.apps.exceer.managers.StorageManager;
import org.grapentin.apps.exceer.managers.TaskManager;

import java.io.Serializable;

public class TrainingActivity extends Activity
{

  private TextView currentExerciseLabel;
  private TextView currentExerciseLevelLabel;
  private TextView timerLabel;
  private Button contextButton;

  private TrainingStage stage;
  private TrainingState state;

  private TaskManager.TimerTask updateTimerTask;
  private TaskManager.TimerTask updateRepsTask;

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_training);

      currentExerciseLabel = (TextView)findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      currentExerciseLevelLabel = (TextView)findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel);
      timerLabel = (TextView)findViewById(R.id.TrainingActivityTimerLabel);
      contextButton = (Button)findViewById(R.id.TrainingActivityContextButton);

      if (savedInstanceState == null)
        {
          exerciseSet(TrainingStage.STAGE_WARM_UP);
        }
      else
        {
          stage = (TrainingStage)savedInstanceState.getSerializable(getString(R.string.TrainingActivityBundleStage));
          state = (TrainingState)savedInstanceState.getSerializable(getString(R.string.TrainingActivityBundleState));
        }
    }

  @Override
  protected void onStop ()
    {
      super.onStop();

      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_training, menu);
      return true;
    }

  @Override
  public boolean onOptionsItemSelected (MenuItem item)
    {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings)
        {
          return true;
        }
      if (id == R.id.action_training_abort)
        {
          super.onBackPressed();
          return true;
        }

      return super.onOptionsItemSelected(item);
    }

  @Override
  public void onSaveInstanceState (@NonNull Bundle savedInstanceState)
    {
      super.onSaveInstanceState(savedInstanceState);

      savedInstanceState.putSerializable(getString(R.string.TrainingActivityBundleStage), stage);
      savedInstanceState.putSerializable(getString(R.string.TrainingActivityBundleState), state);
    }

  @Override
  public void onBackPressed ()
    {
    }

  public void onContextButtonClicked (View view)
    {
      switch (state)
        {
        case STATE_PREPARED:
          exerciseStart();
          break;
        case STATE_RUNNING:
        case STATE_FINISHED:
          exerciseNext();
          break;
        }
    }

  private void exerciseSet (TrainingStage stage)
    {
      this.stage = stage;
      state = TrainingState.STATE_PREPARED;
      contextButton.setText(getString(R.string.TrainingActivityContextButtonTextStart));

      switch (stage)
        {
        case STAGE_WARM_UP:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseWarmUp));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelNone));
          timerSet(10);
          break;
        case STAGE_SQUATS:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseSquats));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelSquats, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsSquats, new RepSet(1, 1, 1)));
          break;
        case STAGE_PULL_UPS:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExercisePullUps));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelPullUps, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsPullUps, new RepSet(4, 4, 4)));
          break;
        case STAGE_HANDSTAND_PUSH_UPS:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseHandstandPushUps));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelHandstandPushUps, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsHandstandPushUps, new RepSet(4, 4, 4)));
          break;
        case STAGE_LEG_RAISES:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLegRaises));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelLegRaises, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsLegRaises, new RepSet(4, 4, 4)));
          break;
        case STAGE_PUSH_UPS:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExercisePushUps));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelPushUps, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsPushUps, new RepSet(4, 4, 4)));
          break;
        case STAGE_DIPS:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseDips));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelDips, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsDips, new RepSet(4, 4, 4)));
          break;
        case STAGE_HORIZONTAL_PULLS:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseHorizontalPulls));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelHorizontalPulls, 1));
          repsSet((RepSet)StorageManager.getSerializable(R.string.SharedPreferencesRepsHorizontalPulls, new RepSet(4, 4, 4)));
          break;
        case STAGE_PLANK:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExercisePlank));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelInt) + StorageManager.getLong(R.string.SharedPreferencesLevelPlank, 1));
          timerSet(StorageManager.getLong(R.string.SharedPreferencesDurationPlank, 30));
          break;
        case STAGE_STRETCHING:
          currentExerciseLabel.setText(getString(R.string.TrainingActivityCurrentExerciseStretching));
          currentExerciseLevelLabel.setText(getString(R.string.TrainingActivityCurrentExerciseLevelNone));
          timerSet(10 * 60);
          break;
        }
    }

  private void exerciseStart ()
    {
      state = TrainingState.STATE_RUNNING;
      contextButton.setText(getString(R.string.TrainingActivityContextButtonTextSkip));

      switch (stage)
        {
        case STAGE_WARM_UP:
        case STAGE_PLANK:
        case STAGE_STRETCHING:
          timerStart();
          break;
        case STAGE_SQUATS:
        case STAGE_PULL_UPS:
        case STAGE_HANDSTAND_PUSH_UPS:
        case STAGE_LEG_RAISES:
        case STAGE_PUSH_UPS:
        case STAGE_DIPS:
        case STAGE_HORIZONTAL_PULLS:
          repsStart();
          break;
        }
    }

  private void exerciseFinish ()
    {
      state = TrainingState.STATE_FINISHED;
      timerFinish();
      contextButton.setText(getString(R.string.TrainingActivityContextButtonTextFinish));

      switch (stage)
        {
        case STAGE_WARM_UP:
          break;
        case STAGE_SQUATS:
          break;
        case STAGE_PULL_UPS:
          break;
        case STAGE_HANDSTAND_PUSH_UPS:
          break;
        case STAGE_LEG_RAISES:
          break;
        case STAGE_PUSH_UPS:
          break;
        case STAGE_DIPS:
          break;
        case STAGE_HORIZONTAL_PULLS:
          break;
        case STAGE_PLANK:
          break;
        case STAGE_STRETCHING:
          break;
        }
    }

  private void exerciseNext ()
    {
      switch (stage)
        {
        case STAGE_WARM_UP:
          exerciseSet(TrainingStage.STAGE_SQUATS);
          break;
        case STAGE_SQUATS:
          exerciseSet(TrainingStage.STAGE_PULL_UPS);
          break;
        case STAGE_PULL_UPS:
          exerciseSet(TrainingStage.STAGE_HANDSTAND_PUSH_UPS);
          break;
        case STAGE_HANDSTAND_PUSH_UPS:
          exerciseSet(TrainingStage.STAGE_LEG_RAISES);
          break;
        case STAGE_LEG_RAISES:
          if (0 < StorageManager.getLong(R.string.SharedPreferencesLastSessionWasPushUps, 0))
            exerciseSet(TrainingStage.STAGE_PUSH_UPS);
          else
            exerciseSet(TrainingStage.STAGE_DIPS);
          break;
        case STAGE_PUSH_UPS:
          exerciseSet(TrainingStage.STAGE_HORIZONTAL_PULLS);
          break;
        case STAGE_DIPS:
          exerciseSet(TrainingStage.STAGE_HORIZONTAL_PULLS);
          break;
        case STAGE_HORIZONTAL_PULLS:
          exerciseSet(TrainingStage.STAGE_PLANK);
          break;
        case STAGE_PLANK:
          exerciseSet(TrainingStage.STAGE_STRETCHING);
          break;
        case STAGE_STRETCHING:
          // TODO: this should go somewhere else
          SharedPreferences pref = getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE);
          SharedPreferences.Editor editor = pref.edit();
          editor.putLong(getString(R.string.SharedPreferencesLastSession), System.currentTimeMillis());
          editor.commit();
          finish();
          break;
        }
    }

  private void timerSet (long timer)
    {
      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
      timerUpdate(timer);
      updateTimerTask = new UpdateTimerTask(timer);
    }

  private void repsSet (RepSet reps)
    {
      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
      repsUpdate(new RepSet(0, 0, 0));
      updateRepsTask = new UpdateRepsTask(reps);
    }

  private void timerStart ()
    {
      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
      TaskManager.post(updateTimerTask);
    }

  private void repsStart ()
    {
      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
      TaskManager.post(updateRepsTask);
    }

  private void timerFinish ()
    {
      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
      timerUpdate(0);
    }

  private void repsFinish ()
    {
      TaskManager.removeCallbacks(updateTimerTask);
      TaskManager.removeCallbacks(updateRepsTask);
    }

  private void timerUpdate (long remaining)
    {
      long minutes = remaining / 60;
      long seconds = remaining % 60;

      String minutes_str = (minutes < 10 ? "0" : "") + minutes;
      String seconds_str = (seconds < 10 ? "0" : "") + seconds;

      timerLabel.setText(minutes_str + ":" + seconds_str);
    }

  private void repsUpdate (RepSet reps)
    {
      timerLabel.setText(reps.toString());
    }

  private enum TrainingStage
  {
    STAGE_WARM_UP,
    STAGE_SQUATS,
    STAGE_PULL_UPS,
    STAGE_HANDSTAND_PUSH_UPS,
    STAGE_LEG_RAISES,
    STAGE_PUSH_UPS,
    STAGE_DIPS,
    STAGE_HORIZONTAL_PULLS,
    STAGE_PLANK,
    STAGE_STRETCHING
  }

  private enum TrainingState
  {
    STATE_PREPARED,
    STATE_RUNNING,
    STATE_FINISHED
  }

  private class UpdateTimerTask extends TaskManager.TimerTask
  {
    private long timer = 0;
    private long start = 0;
    private long countdown = 3;

    public UpdateTimerTask (long timer)
      {
        this.timer = timer;
      }

    public long update ()
      {
        if (start == 0 && countdown > 0)
          {
            SoundManager.play(R.raw.beep_low);
            --countdown;
            return System.currentTimeMillis() + 1000;
          }

        if (start == 0 && countdown == 0)
          {
            SoundManager.play(R.raw.beep_start);
            start = System.currentTimeMillis();
            countdown = 3;
            return start + 1000;
          }

        long elapsed = System.currentTimeMillis() - start;
        long remaining = Math.round(timer - (elapsed / 1000.0));

        if (countdown > 0 && remaining <= countdown)
          {
            SoundManager.play(R.raw.beep_low);
            countdown--;
          }

        if (remaining <= 0)
          {
            SoundManager.play(R.raw.beep_start);
            timerFinish();
            exerciseFinish();
            return 0;
          }

        timerUpdate(remaining);
        return start + (timer - remaining + 1) * 1000;
      }
  }

  private class UpdateRepsTask extends TaskManager.TimerTask
  {
    private RepSet reps;
    private RepSet done;

    private long countdown = 3;
    private boolean inverse;
    private long count = 0;
    private long phase;

    public UpdateRepsTask (RepSet reps)
      {
        this.reps = reps;
        this.done = new RepSet(0, 0, 0);
        this.inverse = false;
        this.phase = (inverse ? 2 : 0);
      }

    private long next ()
      {
        if (phase == 0)       // concentric phase
          {
            phase = 1;
            return System.currentTimeMillis() + 2000;
          }
        if (phase == 1)  // pause on top
          {
            phase = 2;
            return System.currentTimeMillis() + 1000;
          }

        phase = 0;
        return System.currentTimeMillis() + 3000;
      }

    public long update ()
      {
        if (countdown > 0)
          {
            SoundManager.play(R.raw.beep_low);
            --countdown;
            return System.currentTimeMillis() + 1000;
          }
        if (countdown == 0)
          {
            SoundManager.play(R.raw.beep_start);
            --countdown;
            return next();
          }

        ++count;
        if (count >= 3)
          {
            count = 0;
            if (done.set1 < reps.set1)
              ++done.set1;
            else if (done.set2 < reps.set2)
              ++done.set2;
            else
              ++done.set3;
            repsUpdate(done);
          }

        if (done.set3 == reps.set3)
          {
            SoundManager.play(R.raw.beep_start);
            repsFinish();
            return 0;
          }

        if (phase == 0)
          SoundManager.play(R.raw.beep_low);
        else
          SoundManager.play(R.raw.beep_high);

        return next();
      }
  }

  private class RepSet implements Serializable
  {
    public int set1;
    public int set2;
    public int set3;

    public RepSet (int set1, int set2, int set3)
      {
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
      }

    @Override
    public String toString ()
      {
        return set1 + "," + set2 + "," + set3;
      }
  }

}
