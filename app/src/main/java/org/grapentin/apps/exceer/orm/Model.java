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
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.grapentin.apps.exceer.orm.annotations.DatabaseBackref;
import org.grapentin.apps.exceer.orm.annotations.DatabaseColumn;
import org.grapentin.apps.exceer.orm.annotations.DatabaseRelation;
import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Model
{

  protected Class model;
  protected String table;
  protected String name;

  protected HashMap<String, Column> columns = new HashMap<>();
  protected HashMap<String, Relation> relations = new HashMap<>();
  protected HashMap<String, Backref> backrefs = new HashMap<>();

  protected Column id = null;

  protected Model (Class model)
    {
      Log.d("Model", "creating model for class '" + model.getSimpleName() + "'");
      this.model = model;
      this.name = model.getSimpleName();

      DatabaseTable databaseTable = (DatabaseTable)model.getAnnotation(DatabaseTable.class);
      table = (databaseTable.name().equals("") ? model.getSimpleName().toLowerCase() : databaseTable.name());

      for (Field field : model.getDeclaredFields())
        if (field.isAnnotationPresent(DatabaseColumn.class))
          columns.put(field.getName(), new Column(field, this));
        else if (field.isAnnotationPresent(DatabaseRelation.class))
          relations.put(field.getName(), new Relation(field, this));
        else if (field.isAnnotationPresent(DatabaseBackref.class))
          backrefs.put(field.getName(), new Backref(field, this));
    }

  protected void link ()
    {
      for (Column c : columns.values())
        c.link();
      for (Relation r : relations.values())
        r.link();
      for (Backref b : backrefs.values())
        b.link();
    }

  protected void onCreate (SQLiteDatabase db)
    {
      String columns_str = "";
      for (Column c : columns.values())
        columns_str += (columns_str.equals("") ? "" : ", ") + c.toSql();
      String query = "CREATE TABLE '" + table + "' (" + columns_str + ")";

      Log.d("Model", name + ": " + query);

      db.execSQL(query);

      for (Relation r : relations.values())
        r.onCreate(db);
    }

  protected Object materialize (Cursor c) throws IllegalAccessException, InstantiationException
    {
      Object o = model.newInstance();

      for (int i = 0; i < c.getColumnCount(); ++i)
        columns.get(c.getColumnName(i)).datatype.set(o, c.getString(i));

      for (Relation r : relations.values())
        r.materialize(o);
      for (Backref b : backrefs.values())
        b.materialize(o);

      return o;
    }

  protected void add (Object o, SQLiteDatabase db)
    {

    }

}
