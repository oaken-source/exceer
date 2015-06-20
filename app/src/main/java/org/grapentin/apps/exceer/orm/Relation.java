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

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class Relation
{
  public String name;
  public Class other;

  public BaseModel left = null;
  public ArrayList<BaseModel> right = null;

  public Relation (BaseModel left, String name, Class other)
    {
      this.left = left;
      this.name = name;
      this.other = other;
    }

  private String getRelationTableName ()
    {
      return "orm_" + BaseModel.getTableName(left.getClass()) + "_" + BaseModel.getTableName(other);
    }

  public void onCreate ()
    {
      String query = "CREATE TABLE " + getRelationTableName() + " (" + "left_id " + Column.TYPE_INT + ", right_id " + Column.TYPE_INT + ", PRIMARY KEY (left_id, right_id))";
      DatabaseManager.getSession().execSQL(query);
    }

  public void onDrop ()
    {
      String query = "DROP TABLE IF EXISTS " + getRelationTableName();
      DatabaseManager.getSession().execSQL(query);
    }

  public void onInsert ()
    {
      if (right == null)
        return;

      for (BaseModel m : right)
        {
          m.onInsert();
          ContentValues values = new ContentValues();
          values.put("left_id", left._ID.get());
          values.put("right_id", m._ID.get());
          DatabaseManager.getSession().insert(getRelationTableName(), null, values);
        }
    }

  public void add (BaseModel m)
    {
      if (left._ID.get() != null)
        {
          getRight().add(m);

          m.onInsert();
          ContentValues values = new ContentValues();
          values.put("left_id", left._ID.get());
          values.put("right_id", m._ID.get());
          DatabaseManager.getSession().insert(getRelationTableName(), null, values);
        }
      else
        {
          if (right == null)
            right = new ArrayList<>();
          right.add(m);
        }
    }

  public ArrayList<BaseModel> getRight ()
    {
      if (right != null)
        return right;

      right = new ArrayList<>();

      Cursor c = DatabaseManager.getSession().query(getRelationTableName(), new String[]{
          "right_id"
      }, "left_id=" + left._ID.get(), null, null, null, null);

      c.moveToFirst();
      while (!c.isAfterLast())
        {
          right.add(BaseModel.get(other, c.getLong(c.getColumnIndex("right_id"))));
          c.moveToNext();
        }

      c.close();
      return right;
    }

  public BaseModel at (int id)
    {
      return (id >= getRight().size() ? null : getRight().get(id));
    }

  public ArrayList<BaseModel> all ()
    {
      return getRight();
    }

  public boolean isEmpty ()
    {
      return getRight().isEmpty();
    }

  public int size ()
    {
      return getRight().size();
    }
}