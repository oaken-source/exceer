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

public class Backref
{
  public String name;
  public Class other;

  public BaseModel left = null;
  public BaseModel right = null;

  public Backref (BaseModel right, String name, Class other)
    {
      this.right = right;
      this.name = name;
      this.other = other;
    }

  private String getRelationTableName ()
    {
      return "orm_" + BaseModel.getTableName(other) + "_" + BaseModel.getTableName(right.getClass());
    }

  public BaseModel get ()
    {
      if (left != null)
        return left;

      Cursor c = DatabaseManager.getSession().query(getRelationTableName(), new String[]{
          "left_id"
      }, "right_id=" + right._ID.get(), null, null, null, null);

      c.moveToFirst();
      if (!c.isAfterLast())
        left = BaseModel.get(other, c.getLong(c.getColumnIndex("left_id")));

      c.close();
      return left;
    }

}

