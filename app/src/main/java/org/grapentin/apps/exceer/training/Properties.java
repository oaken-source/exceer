/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.util.Log;

import org.grapentin.apps.exceer.helpers.DurationString;

import java.io.Serializable;
import java.lang.reflect.Field;

public class Properties implements Serializable
{

  public long pause_after_rep = 0;
  public long pause_after_set = 90000;
  public long pause_after_exercise = 90000;

  public long reps_duration_concentric = 2000;
  public long reps_duration_eccentric = 3000;
  public long reps_pause_after_concentric = 1000;
  public long reps_pause_after_eccentric = 0;

  public long duration = 0;
  public Duration duration_begin = null;
  public Duration duration_finish = null;
  public long duration_increment = 5000;

  public PrimaryMotion primary_motion = PrimaryMotion.Concentric;

  public Reps reps_begin = null;
  public Reps reps_finish = null;
  public long reps_increment = 0;
  public RepsIncrementDirection reps_increment_direction = RepsIncrementDirection.FrontToBack;
  public RepsIncrementStyle reps_increment_style = RepsIncrementStyle.Balanced;

  public String image = null;

  public Properties ()
    {

    }

  public Properties (Properties properties)
    {
      try
        {
          for (Field f : this.getClass().getFields())
            f.set(this, f.get(properties));
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  public void set (String key, String value)
    {
      switch (key)
        {
        case "pause_after_rep":
        case "pause_after_set":
        case "pause_after_exercise":
        case "reps_duration_concentric":
        case "reps_duration_eccentric":
        case "reps_pause_after_concentric":
        case "reps_pause_after_eccentric":
        case "duration":
        case "duration_increment":
          setLong(key, DurationString.parseLong(value));
          break;
        case "reps_increment":
          setLong(key, Long.parseLong(value));
          break;
        case "duration_begin":
        case "duration_finish":
          setObject(key, new Duration(value));
          break;
        case "reps_begin":
        case "reps_finish":
          setObject(key, new Reps(value));
          break;
        case "image":
          setObject(key, value);
          break;
        case "reps_increment_direction":
          switch (value)
            {
            case "front_to_back":
              this.reps_increment_direction = RepsIncrementDirection.FrontToBack;
              break;
            case "back_to_front":
              this.reps_increment_direction = RepsIncrementDirection.BackToFront;
              break;
            default:
              Log.w("Properties", "unrecognized reps_increment_direction:" + value);
              break;
            }
          break;
        case "reps_increment_style":
          switch (value)
            {
            case "balanced":
              this.reps_increment_style = RepsIncrementStyle.Balanced;
              break;
            case "fill_sets":
              this.reps_increment_style = RepsIncrementStyle.FillSets;
              break;
            default:
              Log.w("Properties", "unrecognized reps_increment_style:" + value);
              break;
            }
          break;
        case "primary_motion":
          switch (value)
            {
            case "concentric":
              this.primary_motion = PrimaryMotion.Concentric;
              break;
            case "eccentric":
              this.primary_motion = PrimaryMotion.Eccentric;
              break;
            default:
              Log.w("Properties", "unrecognized primary_motion:" + value);
              break;
            }
          break;
        default:
          Log.w("Properties", "unrecognized property " + key + ":" + value);
          break;
        }
    }

  private void setLong (String key, long value)
    {
      try
        {
          this.getClass().getDeclaredField(key).setLong(this, value);
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  private void setObject (String key, Object value)
    {
      try
        {
          this.getClass().getDeclaredField(key).set(this, value);
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  public enum RepsIncrementDirection
  {
    FrontToBack,
    BackToFront
  }

  public enum RepsIncrementStyle
  {
    Balanced,
    FillSets
  }

  public enum PrimaryMotion
  {
    Concentric,
    Eccentric
  }
}
