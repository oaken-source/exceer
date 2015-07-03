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
public class Exercise extends BaseExercisable
{

  @DatabaseColumn(id = true)
  private int id;

  @DatabaseColumn
  private String name;
  @DatabaseColumn
  private int currentExerciseId;
  @DatabaseColumn
  private int currentLevelId;
  @DatabaseColumn
  private String progress;
  @DatabaseRelation
  private List<Level> levels;
  @DatabaseRelation
  private List<Exercise> exercises;
  @DatabaseRelation
  private List<Property> properties;

  public static Exercise fromXml (@NonNull XmlNode root)
    {
      Exercise m = new Exercise();

      m.name = (root.getAttribute("name"));
      m.currentExerciseId = 0;
      m.currentLevelId = 0;

      m.properties = new ArrayList<>();
      for (XmlNode property : root.getChildren("property"))
        m.properties.add(Property.fromXml(property));
      m.exercises = new ArrayList<>();
      for (XmlNode exercise : root.getChildren("exercise"))
        m.exercises.add(Exercise.fromXml(exercise));
      m.levels = new ArrayList<>();
      for (XmlNode level : root.getChildren("level"))
        m.levels.add(Level.fromXml(level));

      return m;
    }

  @Nullable
  @SuppressWarnings("unused")
  public static Exercise get (int id)
    {
      return (Exercise)Database.query(Exercise.class).get(id);
    }

  @NonNull
  public BaseExercisable getLeafExercisable ()
    {
      if (getCurrentExercise() != null)
        return getCurrentExercise().getLeafExercisable();
      if (getCurrentLevel() != null)
        return getCurrentLevel().getLeafExercisable();
      return this;
    }

  @Nullable
  private Exercise getCurrentExercise ()
    {
      if (exercises.size() <= currentExerciseId)
        return null;
      return exercises.get(currentExerciseId);
    }

  @Nullable
  private Level getCurrentLevel ()
    {
      if (levels.size() <= currentLevelId)
        return null;
      return levels.get(currentLevelId);
    }

  public int getCurrentLevelId ()
    {
      return currentLevelId + 1;
    }

  public void prepare (@NonNull Properties p)
    {
      props = new Properties(p, properties);

      if (getCurrentExercise() != null)
        getCurrentExercise().prepare(props);
      else if (getCurrentLevel() != null)
        getCurrentLevel().prepare(props);
      else
        super.prepare();
    }

  @Override
  public void show ()
    {
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLabel, name);
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLevelLabel1, "");
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLevelLabel2, "");

      super.show();
    }

  public void reset ()
    {
      if (getCurrentExercise() != null)
        getCurrentExercise().reset();
      else if (getCurrentLevel() != null)
        getCurrentLevel().reset();
      super.reset();
    }

  @Override
  public void levelUp ()
    {
      if (levels.size() <= currentLevelId + 1)
        return;

      currentLevelId++;
      progress = null;
    }

  @Nullable
  public String getCurrentProgress ()
    {
      return progress;
    }

  public void setCurrentProgress (@NonNull String s)
    {
      progress = s;
    }

  public void wrapUp ()
    {
      if (!exercises.isEmpty())
        currentExerciseId = (currentExerciseId + 1) % exercises.size();

      for (Exercise e : exercises)
        e.wrapUp();
      for (Level l : levels)
        l.wrapUp();
    }

  public String getName ()
    {
      return name;
    }

}

