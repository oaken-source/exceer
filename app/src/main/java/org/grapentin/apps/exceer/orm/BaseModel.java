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

  protected static final String TYPE_TEXT = "TEXT";
  protected static final String TYPE_INT = "INTEGER";

  public Column _ID = new Column("id", TYPE_INT, "PRIMARY KEY");

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
              m._ID.value = Long.toString(id);
              c.moveToFirst();
              for (Object o : Reflection.getDeclaredFieldsOfType(model, Column.class))
                ((Column)o).value = c.getString(c.getColumnIndex(((Column)o).name));
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
      if (_ID.value != null)
        return;

      ContentValues values = new ContentValues();
      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Column.class, this))
        values.put(((Column)o).name, ((Column)o).value);

      long id = DatabaseManager.getSession().insert(getTableName(this.getClass()), null, values);
      _ID.set(id);

      DatabaseManager.addToCache(this);

      for (Object o : Reflection.getDeclaredFieldsOfType(getClass(), Relation.class, this))
        ((Relation)o).onInsert();
    }

  public Relation makeRelation (String name, Class other)
    {
      return new Relation(this, name, other);
    }

  public static class Column
  {
    public String name;
    public String type;
    public String params;
    private String value = null;

    public Column (String name)
      {
        this(name, TYPE_TEXT);
      }

    public Column (String name, String type)
      {
        this(name, type, "");
      }

    public Column (String name, String type, String params)
      {
        this.name = name;
        this.type = type;
        this.params = params;
      }

    public String get ()
      {
        return this.value;
      }

    public long getLong ()
      {
        return Long.parseLong(this.value);
      }

    public void set (String value)
      {
        this.value = value;
      }

    public void set (Long value)
      {
        set(Long.toString(value));
      }
  }

  public static class Relation
  {
    public String name;
    public Class other;

    public BaseModel left = null;
    public ArrayList<BaseModel> right = new ArrayList<>();

    public Relation (BaseModel left, String name, Class other)
      {
        this.left = left;
        this.name = name;
        this.other = other;
      }

    private String getRelationTableName ()
      {
        return "orm_" + getTableName(left.getClass()) + "_" + getTableName(other);
      }

    public void onCreate ()
      {
        String query = "CREATE TABLE " + getRelationTableName() + " (" + "left_id " + TYPE_INT + ", right_id " + TYPE_INT + ", PRIMARY KEY (left_id, right_id))";
        DatabaseManager.getSession().execSQL(query);
      }

    public void onDrop ()
      {
        String query = "DROP TABLE IF EXISTS " + getRelationTableName();
        DatabaseManager.getSession().execSQL(query);
      }

    public void onInsert ()
      {
        for (BaseModel m : right)
          {
            m.onInsert();
            ContentValues values = new ContentValues();
            values.put("left_id", left._ID.value);
            values.put("right_id", m._ID.value);
            DatabaseManager.getSession().insert(getRelationTableName(), null, values);
          }
      }

    public void add (BaseModel m)
      {
        right.add(m);

        if (left._ID.value == null)
          return;

        m.onInsert();
        ContentValues values = new ContentValues();
        values.put("left_id", left._ID.value);
        values.put("right_id", m._ID.value);
        DatabaseManager.getSession().insert(getRelationTableName(), null, values);
      }
  }

}
