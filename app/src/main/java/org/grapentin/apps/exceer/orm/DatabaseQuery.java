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

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseQuery
{

  private Model model;

  private String orderBy = null;

  protected DatabaseQuery (Class model)
    {
      this.model = Database.models.get(model);
    }

  public Object get (int id)
    {
      return get(Integer.toString(id));
    }

  public Object get (String id)
    {
      if (model.id == null)
        throw new Database.DatabaseAccessException(model.name + ": missing primary key");

      Cursor c = Database.getSession().query(model.table, null, model.id.name + "=?", new String[]{ id }, null, null, orderBy);
      if (c.getCount() == 0)
        return null;

      c.moveToFirst();
      Object o;

      try
        {
          o = model.materialize(c);
        }
      catch (Exception e)
        {
          throw new Database.DatabaseAccessException(model.name + ": unable to materialize object", e);
        }

      c.close();
      return o;
    }

  public Object first ()
    {
      Cursor c = Database.getSession().query(model.table, null, null, null, null, null, orderBy, "1");
      if (c.getCount() == 0)
        return null;

      c.moveToFirst();
      Object o;

      try
        {
          o = model.materialize(c);
        }
      catch (Exception e)
        {
          throw new Database.DatabaseAccessException(model.name + ": unable to materialize object", e);
        }

      c.close();
      return o;
    }

  public List<Object> all ()
    {
      List<Object> out = new ArrayList<>();

      Cursor c = Database.getSession().query(model.table, null, null, null, null, null, orderBy);

      for (int i = 0; i < c.getCount(); ++i)
        {
          c.move(i);

          try
            {
              out.add(model.materialize(c));
            }
          catch (Exception e)
            {
              throw new Database.DatabaseAccessException(model.name + ": unable to materialize object", e);
            }
        }

      c.close();
      return out;
    }

  public DatabaseQuery orderBy (String orderBy)
    {
      this.orderBy = orderBy;
      return this;
    }

}
