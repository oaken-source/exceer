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

import android.database.Cursor;
import android.support.annotation.Nullable;

import org.grapentin.apps.exceer.orm.BaseModel;
import org.grapentin.apps.exceer.orm.Column;
import org.grapentin.apps.exceer.orm.Database;

public class Session extends BaseModel
{

  @SuppressWarnings("unused") // accessed by reflection from BaseModel
  public final static String TABLE_NAME = "sessions";

  // database layout
  private final Column date = new Column("date", Column.TYPE_LONG);
  private final Column training_id = new Column("training_id", Column.TYPE_LONG);

  public Session (long training_id)
    {
      this.date.set(System.currentTimeMillis());
      this.training_id.set(training_id);
    }

  private Session ()
    {

    }

  @Nullable
  private static Session get (long id)
    {
      return (Session)BaseModel.get(Session.class, id);
    }

  @Nullable
  public static Session getLast ()
    {
      Session out = null;

      Session tmp = new Session();
      Cursor c = Database.getSession().query(TABLE_NAME, new String[]{ tmp._ID.name }, null, null, null, null, tmp.date.name + " DESC", "1");
      if (c.getCount() == 1)
        {
          c.moveToFirst();
          out = get(c.getLong(c.getColumnIndex(tmp._ID.name)));
        }
      c.close();

      return out;
    }

  public long getDate ()
    {
      return date.getLong();
    }

}
