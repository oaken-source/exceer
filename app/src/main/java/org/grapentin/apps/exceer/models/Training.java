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

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.service.DatabaseService;
import org.grapentin.apps.exceer.training.BaseExercisable;
import org.grapentin.apps.exceer.training.Properties;

import java.sql.SQLException;

@DatabaseTable
public class Training
{

  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField
  private String name;

  @ForeignCollectionField
  private ForeignCollection<Exercise> exercises;
  @ForeignCollectionField
  private ForeignCollection<Property> properties;

  private int currentExerciseId = 0;

  public static void fromXml (@NonNull XmlNode root)
    {
      Training m = new Training();

      m.name = root.getAttribute("name");

      DatabaseService.add(m);

      for (XmlNode property : root.getChildren("property"))
        Property.fromXml(property, m);
      for (XmlNode exercise : root.getChildren("exercise"))
        Exercise.fromXml(exercise, m);
    }

  @Nullable
  public static Training get (int id)
    {
      return (Training)DatabaseService.query(Training.class).get(id);
    }

  @Nullable
  private Exercise getCurrentExercise ()
    {
      if (exercises.size() <= currentExerciseId)
        return null;
      try
        {
          return exercises.iterator(currentExerciseId).current();
        }
      catch (SQLException e)
        {
          throw new Error(e);
        }
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
