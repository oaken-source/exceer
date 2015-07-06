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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.ForeignCollection;

import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.models.Property;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class Properties implements Serializable
{

  public Duration pause_after_set = Duration.fromString("90s");
  public Duration pause_after_exercise = Duration.fromString("90s");

  public Duration reps_duration_concentric = Duration.fromString("2s");
  public Duration reps_duration_eccentric = Duration.fromString("3s");
  public Duration reps_pause_after_concentric = Duration.fromString("1s");
  public Duration reps_pause_after_eccentric = Duration.fromString("0");

  @Nullable
  public Duration duration = null;
  @Nullable
  public Duration duration_begin = null;
  @Nullable
  public Duration duration_finish = null;
  public Duration duration_increment = Duration.fromString("5s");

  @NonNull
  public PrimaryMotion primary_motion = PrimaryMotion.concentric;
  public boolean two_sided = false;

  @Nullable
  public Reps reps_begin = null;
  @Nullable
  public Reps reps_finish = null;
  public long reps_increment = 0;
  @NonNull
  public Reps.IncrementDirection reps_increment_direction = Reps.IncrementDirection.front_to_back;
  @NonNull
  public Reps.IncrementStyle reps_increment_style = Reps.IncrementStyle.balanced;

  @Nullable
  public String image = null;

  public Properties ()
    {

    }

  @SuppressWarnings("WeakerAccess")
  public Properties (@NonNull Properties properties)
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

  public Properties (@NonNull ForeignCollection<Property> properties)
    {
      for (Property p : properties)
        {
          String key = p.getKey();
          String val = p.getValue();
          set(key, val);
        }
    }

  public Properties (@NonNull Properties other, @NonNull ForeignCollection<Property> properties)
    {
      this(other);

      for (Property p : properties)
        {
          String key = p.getKey();
          String val = p.getValue();
          set(key, val);
        }
    }

  private void set (@NonNull String key, @NonNull String value)
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
        case "duration_increment":
        case "duration":
        case "duration_begin":
        case "duration_finish":
          setObject(key, Duration.fromString(value));
          break;
        case "reps_increment":
          setLong(key, Long.parseLong(value));
          break;
        case "reps_begin":
        case "reps_finish":
          setObject(key, Reps.fromString(value));
          break;
        case "image":
          setObject(key, value);
          break;
        case "two_sided":
          setBoolean(key, Boolean.parseBoolean(value));
          break;
        case "reps_increment_direction":
          try
            {
              setObject(key, Reps.IncrementDirection.valueOf(value));
            }
          catch (Exception e)
            {
              Toast.makeText(BaseActivity.getContext(), "invalid value for reps_increment_direction: '" + value + "'", Toast.LENGTH_LONG).show();
            }
          break;
        case "reps_increment_style":
          try
            {
              setObject(key, Reps.IncrementStyle.valueOf(value));
            }
          catch (Exception e)
            {
              Toast.makeText(BaseActivity.getContext(), "invalid value for reps_increment_style: '" + value + "'", Toast.LENGTH_LONG).show();
            }
          break;
        case "primary_motion":
          try
            {
              setObject(key, PrimaryMotion.valueOf(value));
            }
          catch (Exception e)
            {
              Toast.makeText(BaseActivity.getContext(), "invalid value for primary_motion: '" + value + "'", Toast.LENGTH_LONG).show();
            }
          break;
        default:
          throw new IndexOutOfBoundsException();
        }
    }

  private void setLong (@NonNull String key, long value)
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

  private void setObject (@NonNull String key, @NonNull Object value)
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

  private void setBoolean (@NonNull String key, boolean value)
    {
      try
        {
          this.getClass().getDeclaredField(key).setBoolean(this, value);
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  public enum PrimaryMotion
  {
    concentric,
    eccentric
  }
}
