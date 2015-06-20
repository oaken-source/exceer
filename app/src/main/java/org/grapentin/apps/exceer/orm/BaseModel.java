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

import org.grapentin.apps.exceer.helpers.Reflection;

import java.util.ArrayList;

public abstract class BaseModel
{

  protected Column _ID = new Column("id", Column.TYPE_INT, "PRIMARY KEY");

  public BaseModel ()
    {

    }

  public static String getTableName (Class model)
    {
      try
        {
          return (String)model.getDeclaredField("TABLE_NAME").get(null);
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  public static void onCreate (Class model)
    {
      if (getTableName(model) == null)
        return;

      String columns = "";
      for (Object o : Reflection.getDeclaredFieldsOfType(model, Column.class))
        columns += (columns.equals("") ? "" : ", ") + ((Column)o).name + " " + ((Column)o).type + (((Column)o).params.equals("") ? "" : " " + ((Column)o).params);

      String query = "CREATE TABLE " + getTableName(model) + " (" + columns + ")";
      DatabaseManager.getSession().execSQL(query);

      for (Object o : Reflection.getDeclaredFieldsOfType(model, Relation.class))
        ((Relation)o).onCreate();
    }

  public static void onDrop (Class model)
    {
      if (getTableName(model) == null)
        return;

      String query = "DROP TABLE IF EXISTS " + getTableName(model);
      DatabaseManager.getSession().execSQL(query);

      for (Object o : Reflection.getDeclaredFieldsOfType(model, Relation.class))
        ((Relation)o).onDrop();
    }

  public static BaseModel get (Class model, long id)
    {
      BaseModel m = DatabaseManager.getFromCache(model, id);
      if (m != null)
        return m;

      try
        {
          m = (BaseModel)model.newInstance();

          Cursor c = DatabaseManager.getSession().query(getTableName(model), null, m._ID.name + "=" + id, null, null, null, null);
          if (c.getCount() == 1)
            {
              m._ID.set(id);
              c.moveToFirst();
              for (Object o : Reflection.getDeclaredFieldsOfType(model, Column.class, m))
                ((Column)o).set(c.getString(c.getColumnIndex(((Column)o).name)));
            }
          c.close();

          DatabaseManager.addToCache(m);

          return m;
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  public static ArrayList<Long> getAllIds (Class model)
    {
      ArrayList<Long> out = new ArrayList<>();

      try
        {
          BaseModel m = (BaseModel)model.newInstance();

          Cursor c = DatabaseManager.getSession().query(getTableName(model), new String[]{ m._ID.name }, null, null, null, null, null);
          c.moveToFirst();
          while (!c.isAfterLast())
            {
              out.add(c.getLong(c.getColumnIndex(m._ID.name)));
              c.moveToNext();
            }
          c.close();

          return out;
        }
      catch (Exception e)
        {
          throw new Error(e);
        }
    }

  public void onInsert ()
    {
      if (_ID.get() != null)
        return;

      ContentValues values = new ContentValues();
      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Column.class, this))
        if (o != _ID)
          values.put(((Column)o).name, ((Column)o).get());

      long id = DatabaseManager.getSession().insert(getTableName(this.getClass()), null, values);
      _ID.set(id);

      DatabaseManager.addToCache(this);

      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Relation.class, this))
        ((Relation)o).onInsert();
    }

  public void commit ()
    {
      if (_ID.get() == null)
        {
          onInsert();
          return;
        }

      ContentValues values = new ContentValues();
      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Column.class, this))
        if (o != _ID)
          values.put(((Column)o).name, ((Column)o).get());

      DatabaseManager.getSession().update(getTableName(this.getClass()), values, _ID.name + "=" + _ID.get(), null);
    }

  public Relation makeRelation (String name, Class other)
    {
      return new Relation(this, name, other);
    }

  public Backref makeBackref (String name, Class other)
    {
      return new Backref(this, name, other);
    }

  public long getId ()
    {
      return _ID.getLong();
    }

}
