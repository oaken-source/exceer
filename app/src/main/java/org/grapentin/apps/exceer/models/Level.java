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

@DatabaseTable
public class Level extends BaseExercisable
{

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String name;
  @DatabaseField
  private String progress;

  @ForeignCollectionField
  private ForeignCollection<Property> properties;

  @DatabaseField(foreign = true)
  private Training parentTraining;
  @DatabaseField(foreign = true)
  private Exercise parentExercise;

  public static Level fromXml (@NonNull XmlNode root)
    {
      Level m = new Level();

      m.name = root.getAttribute("name");

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(Property.fromXml(property));

      return m;
    }

  @Nullable
  public static Level get (int id)
    {
      return (Level)DatabaseService.query(Level.class).get(id);
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
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLabel, parentExercise.getName());
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLevelLabel1, BaseActivity.getContext().getString(R.string.TrainingActivityCurrentExerciseLevelInt) + parentExercise.getCurrentLevelId());
      BaseActivity.setText(R.id.TrainingActivityCurrentExerciseLevelLabel2, name);

      super.show();
    }

  @Override
  public void levelUp ()
    {
      parentExercise.levelUp();
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

    }

}
