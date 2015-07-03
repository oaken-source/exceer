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

import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

import java.lang.reflect.Field;

public class Backref
{

  protected Field field;
  protected Model model;
  protected String name;

  protected Model other;

  protected Backref (Field field, Model model)
    {
      Log.d("Column", "creating backref '" + field.getName() + "' for model '" + model.name + "'");
      this.field = field;
      this.model = model;
      this.name = field.getName();

      if (!field.getType().isAnnotationPresent(DatabaseTable.class))
        throw new Database.DatabaseAccessException(model.name + "." + name + ": backref foreign type is not a model");
    }

  public void link ()
    {
      other = Database.models.get(field.getType());

      if (model.id == null)
        throw new Database.DatabaseAccessException(model.name + "." + name + ": model has no primary key");
      if (other.id == null)
        throw new Database.DatabaseAccessException(model.name + "." + name + ": foreign model has no primary key");
    }

  public void materialize (Object o)
    {

    }
}
