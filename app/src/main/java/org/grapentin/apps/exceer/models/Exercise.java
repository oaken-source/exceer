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
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.TrainingActivity;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.orm.BaseModel;
import org.grapentin.apps.exceer.orm.Column;
import org.grapentin.apps.exceer.orm.Relation;
import org.grapentin.apps.exceer.training.BaseExercisable;
import org.grapentin.apps.exceer.training.Properties;

public class Exercise extends BaseExercisable
{

  @SuppressWarnings("unused") // accessed by reflection from BaseModel
  public final static String TABLE_NAME = "exercises";

  // database layout
  private final Column name = new Column("name");
  private final Column currentExerciseId = new Column("currentExerciseId", Column.TYPE_INT);
  private final Column currentLevelId = new Column("currentLevelId", Column.TYPE_INT);
  private final Column progress = new Column("progress");
  private final Relation levels = makeRelation(Level.class);
  private final Relation exercises = makeRelation(Exercise.class);
  private final Relation properties = makeRelation(Property.class);

  public static Exercise fromXml (@NonNull XmlNode root)
    {
      Exercise m = new Exercise();

      m.name.set(root.getAttribute("name"));
      m.currentExerciseId.set(0);
      m.currentLevelId.set(0);

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(Property.fromXml(property));
      for (XmlNode exercise : root.getChildren("exercise"))
        m.exercises.add(Exercise.fromXml(exercise));
      for (XmlNode level : root.getChildren("level"))
        m.levels.add(Level.fromXml(level));

      return m;
    }

  @Nullable
  @SuppressWarnings("unused")
  public static Exercise get (long id)
    {
      return (Exercise)BaseModel.get(Exercise.class, id);
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
      return (Exercise)exercises.at(currentExerciseId.getInt());
    }

  @Nullable
  private Level getCurrentLevel ()
    {
      return (Level)levels.at(currentLevelId.getInt());
    }

  public int getCurrentLevelId ()
    {
      return currentLevelId.getInt() + 1;
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
      TextView currentExerciseLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      TextView currentExerciseLevelLabel1 = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel1);
      TextView currentExerciseLevelLabel2 = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel2);

      currentExerciseLabel.setText(name.get());
      currentExerciseLevelLabel1.setText("");
      currentExerciseLevelLabel2.setText("");

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
      if (levels.at(currentLevelId.getInt() + 1) != null)
        {
          currentLevelId.set(currentLevelId.getInt() + 1);
          progress.set(null);
        }
    }

  @Nullable
  public String getCurrentProgress ()
    {
      return progress.get();
    }

  public void setCurrentProgress (@NonNull String s)
    {
      progress.set(s);
    }

  public void wrapUp ()
    {
      if (!exercises.isEmpty())
        currentExerciseId.set((currentExerciseId.getInt() + 1) % exercises.size());

      for (BaseModel e : exercises.all())
        ((Exercise)e).wrapUp();
      for (BaseModel l : levels.all())
        ((Level)l).wrapUp();

      commit();
    }

  public String getName ()
    {
      return name.get();
    }

}

