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

import android.util.Log;

import org.grapentin.apps.exceer.orm.annotations.DatabaseColumn;
import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

import java.lang.reflect.Field;

public class Column
{

  protected Field field;
  protected Model model;
  protected String name;

  protected DataType datatype;

  protected Column (Field field, Model model)
    {
      Log.d("Column", "creating column '" + field.getName() + "' for model '" + model.name + "'");
      this.field = field;
      this.model = model;
      this.name = field.getName();

      if (field.getAnnotation(DatabaseColumn.class).id())
        model.id = this;
    }

  protected void link ()
    {
      Class type = field.getType();
      if (type.isAnnotationPresent(DatabaseTable.class))
        {
          final Model m = Database.models.get(type);
          if (m.id == null)
            throw new Database.DatabaseAccessException(model.name + "." + name + ": foreign model has no primary key");

          datatype = new DataType<Object>()
          {
            @Override
            public String toSql ()
              {
                return m.id.datatype.toSql();
              }

            @Override
            public void set (Object o, String string) throws IllegalAccessException
              {
                field.set(o, Database.query(m.model).get(string));
              }
          };
        }
      else if (type == int.class || type == long.class)
        datatype = new DataType<Integer>()
        {
          @Override
          public String toSql ()
            {
              return "INTEGER";
            }

          @Override
          public void set (Object o, String string) throws IllegalAccessException
            {
              field.setInt(o, Integer.parseInt(string));
            }
        };
      else if (type == String.class)
        datatype = new DataType<String>()
        {
          @Override
          public String toSql ()
            {
              return "TEXT";
            }

          @Override
          public void set (Object o, String string) throws IllegalAccessException
            {
              field.set(o, string);
            }
        };
      else
        throw new Database.DatabaseAccessException("incompatible type on column '" + name + "': " + type.getSimpleName());
    }

  protected String toSql ()
    {
      String out = "'" + name + "' " + datatype.toSql();

      if (field.getAnnotation(DatabaseColumn.class).id())
        out += " PRIMARY KEY";

      return out;
    }

  protected interface DataType<T>
  {
    String toSql ();

    void set (Object o, String string) throws IllegalAccessException;
  }

}
