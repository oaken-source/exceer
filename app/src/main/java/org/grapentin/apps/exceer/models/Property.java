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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.service.DatabaseService;

@DatabaseTable
public class Property
{

  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField
  private String key;
  @DatabaseField
  private String value;

  @DatabaseField(foreign = true)
  Training parentTraining;
  @DatabaseField(foreign = true)
  Exercise parentExercise;
  @DatabaseField(foreign = true)
  Level parentLevel;

  public static void fromXml (@NonNull XmlNode root, Training parentTraining)
    {
      Property m = new Property();

      m.key = root.getAttribute("name");
      m.value = root.getValue();

      m.parentTraining = parentTraining;

      DatabaseService.add(m);
    }

  public static void fromXml (@NonNull XmlNode root, Exercise parentExercise)
    {
      Property m = new Property();

      m.key = root.getAttribute("name");
      m.value = root.getValue();

      m.parentExercise = parentExercise;

      DatabaseService.add(m);
    }

  public static void fromXml (@NonNull XmlNode root, Level parentLevel)
    {
      Property m = new Property();

      m.key = root.getAttribute("name");
      m.value = root.getValue();

      m.parentLevel = parentLevel;

      DatabaseService.add(m);
    }

  @Nullable
  public static Property get (int id)
    {
      return (Property)DatabaseService.query(Property.class).get(id);
    }

  public String getKey ()
    {
      return key;
    }

  public String getValue ()
    {
      return value;
    }

}
