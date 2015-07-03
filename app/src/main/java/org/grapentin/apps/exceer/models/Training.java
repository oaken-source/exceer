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
import android.support.annotation.Nullable;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.orm.Database;
import org.grapentin.apps.exceer.orm.annotations.DatabaseColumn;
import org.grapentin.apps.exceer.orm.annotations.DatabaseRelation;
import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;
import org.grapentin.apps.exceer.training.BaseExercisable;
import org.grapentin.apps.exceer.training.Properties;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable
public class Training
{

  @DatabaseColumn(id = true)
  private int id;

  @DatabaseColumn
  private String name;
  @DatabaseRelation
  private List<Exercise> exercises;
  @DatabaseRelation
  private List<Property> properties;

  private int currentExerciseId = 0;

  public static Training fromXml (@NonNull XmlNode root)
    {
      Training m = new Training();

      m.name = root.getAttribute("name");

      m.properties = new ArrayList<>();
      for (XmlNode property : root.getChildren("property"))
        m.properties.add(Property.fromXml(property));
      m.exercises = new ArrayList<>();
      for (XmlNode exercise : root.getChildren("exercise"))
        m.exercises.add(Exercise.fromXml(exercise));

      return m;
    }

  @Nullable
  public static Training get (int id)
    {
      return (Training)Database.query(Training.class).get(id);
    }

  @Nullable
  private Exercise getCurrentExercise ()
    {
      if (exercises.size() <= currentExerciseId)
        return null;
      return exercises.get(currentExerciseId);
    }

  @Nullable
  public BaseExercisable getLeafExercisable ()
    {
      if (getCurrentExercise() == null)
        return null;
      return getCurrentExercise().getLeafExercisable();
    }

  public void prepare ()
    {
      Properties props = new Properties(properties);

      for (Exercise e : exercises)
        e.prepare(props);

      if (getLeafExercisable() != null)
        getLeafExercisable().show();
      else
        show();
    }

  private void show ()
    {
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLabel, R.string.TrainingActivityNoExercises);
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLevelLabel1, "");
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLevelLabel2, "");
    }

  public boolean next ()
    {
      currentExerciseId++;
      if (getCurrentExercise() == null)
        return false;

      getCurrentExercise().getLeafExercisable().show();
      return true;
    }

  public void reset ()
    {
      currentExerciseId = 0;

      for (Exercise e : exercises)
        e.reset();
    }

  public void start ()
    {
      if (getLeafExercisable() != null)
        getLeafExercisable().start();
    }

  public void pause ()
    {
      if (getLeafExercisable() != null)
        getLeafExercisable().pause();
    }

  public void resume ()
    {
      if (getLeafExercisable() != null)
        getLeafExercisable().start();
    }

  public void wrapUp ()
    {
      currentExerciseId = 0;

      for (Exercise e : exercises)
        e.wrapUp();
    }

}
