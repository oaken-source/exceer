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

package org.grapentin.apps.exceer.models;


import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.service.DatabaseService;
import org.grapentin.apps.exceer.training.Duration;
import org.grapentin.apps.exceer.training.Reps;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

@DatabaseTable
public class Exercise implements Serializable
{

  @DatabaseField(generatedId = true)
  public int id;

  @DatabaseField
  private String name;
  @DatabaseField
  private int currentExerciseId;
  @DatabaseField
  private String duration;
  @DatabaseField
  private String duration_begin;
  @DatabaseField
  private String duration_finish;
  @DatabaseField
  private String duration_increment;
  @DatabaseField
  private String reps_begin;
  @DatabaseField
  private String reps_finish;
  @DatabaseField
  private String reps_increment;
  @DatabaseField
  private String reps_duration_concentric;
  @DatabaseField
  private String reps_duration_eccentric;
  @DatabaseField
  private String reps_pause_after_concentric;
  @DatabaseField
  private String reps_pause_after_eccentric;
  @DatabaseField
  private String progress;
  @DatabaseField
  private String pause_after_set;
  @DatabaseField
  private String pause_after_exercise;
  @DatabaseField
  private String image;
  @DatabaseField
  private boolean two_sided;
  @DatabaseField(dataType = DataType.ENUM_INTEGER)
  private ExerciseChildrenType exerciseChildrenType;
  @DatabaseField(dataType = DataType.ENUM_INTEGER)
  private ExercisePrimaryMotion exercisePrimaryMotion;

  @ForeignCollectionField
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private ForeignCollection<Exercise> exercisesField;
  private ArrayList<Exercise> exercises = null;

  @DatabaseField(foreign = true)
  private Training parentTraining;
  @DatabaseField(foreign = true)
  private Exercise parentExercise;

  public static void fromXml (@NonNull XmlNode root, Training p)
    {
      Exercise e = new Exercise();

      e.currentExerciseId = 0;
      e.progress = null;

      e.parentTraining = p;

      for (Map.Entry<String, String> entry : root.getAttributes().entrySet())
        e.setAttribute(entry.getKey(), entry.getValue());

      DatabaseService.add(e);

      for (XmlNode exercise : root.getChildren("exercise"))
        Exercise.fromXml(exercise, e);
    }

  public static void fromXml (@NonNull XmlNode root, Exercise p)
    {
      Exercise e = new Exercise();

      e.currentExerciseId = 0;
      e.progress = null;

      e.parentExercise = p;

      for (Map.Entry<String, String> entry : root.getAttributes().entrySet())
        e.setAttribute(entry.getKey(), entry.getValue());

      DatabaseService.add(e);

      for (XmlNode exercise : root.getChildren("exercise"))
        Exercise.fromXml(exercise, e);
    }

  private void setAttribute (String key, String value)
    {
      switch (key)
        {
        case "type":
          exerciseChildrenType = ExerciseChildrenType.valueOf(value);
          break;
        case "primary_motion":
          exercisePrimaryMotion = ExercisePrimaryMotion.valueOf(value);
          break;
        case "two_sided":
          two_sided = Boolean.parseBoolean(value);
          break;
        default:
          try
            {
              Field f = getClass().getDeclaredField(key);
              f.set(this, value);
            }
          catch (Exception e)
            {
              Log.w("Exercise", "unhandled setAttribute: " + key, e);
            }
        }
    }

  public String getName ()
    {
      return name;
    }

  public void collectExercises (ArrayList<Exercise> exercises)
    {
      if (getNumberOfExercises() == 0)
        exercises.add(this);
      else if (exerciseChildrenType == ExerciseChildrenType.progressing)
        exercises.add(getCurrentExercise());
      else if (exerciseChildrenType == ExerciseChildrenType.alternating)
        getCurrentExercise().collectExercises(exercises);
      else if (exerciseChildrenType == ExerciseChildrenType.sequential)
        for (Exercise e : getExercises())
          e.collectExercises(exercises);
    }

  private ArrayList<Exercise> getExercises ()
    {
      if (exercises == null)
        exercises = new ArrayList<>(exercisesField);
      return exercises;
    }

  private Exercise getCurrentExercise ()
    {
      if (currentExerciseId >= exercises.size())
        currentExerciseId = exercises.size() - 1;
      return getExercises().get(currentExerciseId);
    }

  private int getNumberOfExercises ()
    {
      return getExercises().size();
    }

  public Exercise getParentExercise ()
    {
      return parentExercise;
    }

  public ExerciseChildrenType getExerciseChildrenType ()
    {
      return exerciseChildrenType;
    }

  public int getLevelNumber (Exercise exercise)
    {
      return exercises.indexOf(exercise) + 1;
    }

  public Duration getDuration ()
    {
      return (duration != null) ? Duration.fromString(duration) : ((parentExercise != null) ? parentExercise.getDuration() : null);
    }

  public Duration getDurationBegin ()
    {
      return (duration_begin != null) ? Duration.fromString(duration_begin) : ((parentExercise != null) ? parentExercise.getDurationBegin() : null);
    }

  public Reps getRepsBegin ()
    {
      return (reps_begin != null) ? Reps.fromString(reps_begin) : ((parentExercise != null) ? parentExercise.getRepsBegin() : null);
    }

  public boolean isLevel ()
    {
      return getParentExercise() != null && getParentExercise().getExerciseChildrenType() == ExerciseChildrenType.progressing;
    }

  public String getProgress ()
    {
      return progress;
    }

  public enum ExerciseChildrenType
  {
    alternating,
    sequential,
    progressing
  }

  public enum ExercisePrimaryMotion
  {
    concentric,
    eccentric
  }

}

