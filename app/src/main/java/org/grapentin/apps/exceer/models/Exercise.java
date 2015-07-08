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

import java.io.Serializable;
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
  private String progress;
  @DatabaseField(dataType = DataType.ENUM_INTEGER)
  private ExerciseChildrenType exerciseChildrenType;

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
        e.set(entry.getKey(), entry.getValue());

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
        e.set(entry.getKey(), entry.getValue());

      DatabaseService.add(e);

      for (XmlNode exercise : root.getChildren("exercise"))
        Exercise.fromXml(exercise, e);
    }

  private void set (String key, String value)
    {
      Log.d("set called", key + "->" + value);

      switch (key)
        {
        case "name":
          name = value;
          break;
        case "type":
          exerciseChildrenType = ExerciseChildrenType.valueOf(value);
          break;
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

  public enum ExerciseChildrenType
  {
    alternating,
    sequential,
    progressing
  }
}

