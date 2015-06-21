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
import org.grapentin.apps.exceer.activity.TrainingActivity;
import org.grapentin.apps.exceer.helpers.Context;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.orm.Backref;
import org.grapentin.apps.exceer.orm.BaseModel;
import org.grapentin.apps.exceer.orm.Column;
import org.grapentin.apps.exceer.orm.Relation;
import org.grapentin.apps.exceer.training.BaseExercisable;
import org.grapentin.apps.exceer.training.Properties;

public class Level extends BaseExercisable
{

  @SuppressWarnings("unused") // accessed by reflection from BaseModel
  public final static String TABLE_NAME = "levels";

  // database layout
  private final Column name = new Column("name");
  private final Column progress = new Column("progress");
  private final Relation properties = makeRelation(Property.class);
  private final Backref exercise = makeBackref(Exercise.class);

  public static Level fromXml (@NonNull XmlNode root)
    {
      Level m = new Level();

      m.name.set(root.getAttribute("name"));

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(Property.fromXml(property));

      return m;
    }

  @Nullable
  @SuppressWarnings("unused")
  public static Level get (long id)
    {
      return (Level)BaseModel.get(Level.class, id);
    }

  @NonNull
  public BaseExercisable getLeafExercisable ()
    {
      return this;
    }

  public void prepare (@NonNull Properties p)
    {
      props = new Properties(p, properties);

      super.prepare();
    }

  @Override
  public void show ()
    {
      TextView currentExerciseLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      TextView currentExerciseLevelLabel1 = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel1);
      TextView currentExerciseLevelLabel2 = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel2);

      currentExerciseLabel.setText(((Exercise)exercise.get()).getName());
      currentExerciseLevelLabel1.setText(Context.get().getString(R.string.TrainingActivityCurrentExerciseLevelInt) + ((Exercise)exercise.get()).getCurrentLevelId());
      currentExerciseLevelLabel2.setText(name.get());

      super.show();
    }

  @Override
  public void levelUp ()
    {
      ((Exercise)exercise.get()).levelUp();
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
      commit();
    }

}
