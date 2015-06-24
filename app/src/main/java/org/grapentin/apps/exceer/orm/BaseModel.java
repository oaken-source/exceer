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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.grapentin.apps.exceer.helpers.Reflection;

public abstract class BaseModel
{

  protected final Column _ID = new Column("id", Column.TYPE_INT, "PRIMARY KEY");

  public BaseModel ()
    {

    }

  @Nullable
  public static String getTableName (@NonNull Class model)
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

  public static void onCreate (@NonNull Class model)
    {
      String TABLE_NAME = getTableName(model);
      Log.d("onCreate", model.getName() + ":" + TABLE_NAME);
      if (TABLE_NAME == null)
        return;

      String columns = "";
      for (Object o : Reflection.getDeclaredFieldsOfType(model, Column.class))
        columns += (columns.equals("") ? "" : ", ") + ((Column)o).name + " " + ((Column)o).type + (((Column)o).params.equals("") ? "" : " " + ((Column)o).params);

      String query = "CREATE TABLE " + TABLE_NAME + " (" + columns + ")";
      Database.getSession().execSQL(query);

      for (Object o : Reflection.getDeclaredFieldsOfType(model, Relation.class))
        ((Relation)o).onCreate();
    }

  public static void onDrop (@NonNull Class model)
    {
      String TABLE_NAME = getTableName(model);
      if (TABLE_NAME == null)
        return;

      String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
      Database.getSession().execSQL(query);

      for (Object o : Reflection.getDeclaredFieldsOfType(model, Relation.class))
        ((Relation)o).onDrop();
    }

  @Nullable
  public static BaseModel get (@NonNull Class model, long id)
    {
      BaseModel m = Database.getFromCache(model, id);
      if (m != null)
        return m;

      String TABLE_NAME = getTableName(model);
      assert TABLE_NAME != null;

      try
        {
          m = (BaseModel)model.newInstance();

          Cursor c = Database.getSession().query(TABLE_NAME, null, m._ID.name + "=" + id, null, null, null, null);
          if (c.getCount() == 1)
            {
              m._ID.set(id);
              c.moveToFirst();
              for (Object o : Reflection.getDeclaredFieldsOfType(model, Column.class, m))
                ((Column)o).set(c.getString(c.getColumnIndex(((Column)o).name)));
            }
          c.close();

          Database.addToCache(m);

          return m;
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

      String TABLE_NAME = getTableName(getClass());
      assert TABLE_NAME != null;

      ContentValues values = new ContentValues();
      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Column.class, this))
        if (o != _ID)
          values.put(((Column)o).name, ((Column)o).get());

      long id = Database.getSession().insert(TABLE_NAME, null, values);
      _ID.set(id);

      Database.addToCache(this);

      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Relation.class, this))
        ((Relation)o).onInsert();
    }

  protected void commit ()
    {
      if (_ID.get() == null)
        {
          onInsert();
          return;
        }

      String TABLE_NAME = getTableName(getClass());
      assert TABLE_NAME != null;

      ContentValues values = new ContentValues();
      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Column.class, this))
        if (o != _ID)
          values.put(((Column)o).name, ((Column)o).get());

      Database.getSession().update(TABLE_NAME, values, _ID.name + "=" + _ID.get(), null);
    }

  @NonNull
  protected Relation makeRelation (@NonNull Class other)
    {
      return new Relation(this, other);
    }

  @NonNull
  protected Backref makeBackref (@NonNull Class other)
    {
      return new Backref(this, other);
    }

  public long getId ()
    {
      return _ID.getLong();
    }

}
