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

package org.grapentin.apps.exceer.orm;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class Relation
{

  protected Field field;
  protected Model model;
  protected String name;

  protected Model other;

  protected Relation (Field field, Model model)
    {
      Log.d("Column", "creating relation '" + field.getName() + "' for model '" + model.name + "'");
      this.field = field;
      this.model = model;
      this.name = field.getName();

      if (!field.getType().isAssignableFrom(List.class))
        throw new Database.DatabaseAccessException(model.name + "." + name + ": relation incompatible with a List");
      if (!((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]).isAnnotationPresent(DatabaseTable.class))
        throw new Database.DatabaseAccessException(model.name + "." + name + ": foreign type is not a model");
    }

  public void link ()
    {
      Class c = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
      other = Database.models.get(c);

      if (model.id == null)
        throw new Database.DatabaseAccessException(model.name + "." + name + ": model has no primary key");
      if (other.id == null)
        throw new Database.DatabaseAccessException(model.name + "." + name + ": foreign model has no primary key");
    }

  public void onCreate (SQLiteDatabase db)
    {
      String query = "CREATE TABLE '" + model.table + "_" + other.table + "' ('left_id' " + model.id.datatype.toSql() + ", 'right_id' " + other.id.datatype.toSql() + ")";
      Log.d("Relation", model.name + "." + name + ": " + query);
      db.execSQL(query);
    }

  public void materialize (Object o)
    {

    }

}
