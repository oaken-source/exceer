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

package org.grapentin.apps.exceer.training;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.grapentin.apps.exceer.managers.ContextManager;

import java.util.ArrayList;

public class TrainingStorage extends SQLiteOpenHelper
{
  // If you change the database schema, you must increment the database version.
  private static final int DATABASE_VERSION = 2;
  private static final String DATABASE_NAME = "TrainingStorage.db";

  private static final String TEXT_TYPE = " TEXT";
  private static final String LONG_TYPE = " INTEGER";
  private static final String COMMA_SEP = ",";
  private static final String SQL_CREATE_SESSIONS = "CREATE TABLE " + SessionEntry.TABLE_NAME + " (" +
      SessionEntry._ID + " INTEGER PRIMARY KEY," +
      SessionEntry.COLUMN_NAME_DATE + LONG_TYPE + COMMA_SEP +
      SessionEntry.COLUMN_NAME_NAME + TEXT_TYPE +
      " )";
  private static final String SQL_CREATE_EXERCISES = "CREATE TABLE " + ExerciseEntry.TABLE_NAME + " (" +
      ExerciseEntry._ID + " INTEGER PRIMARY KEY," +
      ExerciseEntry.COLUMN_NAME_SESSION_ID + LONG_TYPE + COMMA_SEP +
      ExerciseEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
      ExerciseEntry.COLUMN_NAME_LEVEL + LONG_TYPE + COMMA_SEP +
      ExerciseEntry.COLUMN_NAME_VALUE + TEXT_TYPE +
      " )";

  private static final String SQL_DELETE_SESSIONS = "DROP TABLE IF EXISTS " + SessionEntry.TABLE_NAME;
  private static final String SQL_DELETE_EXERCISES = "DROP TABLE IF EXISTS " + ExerciseEntry.TABLE_NAME;

  private static TrainingStorage instance = null;

  private long lastSession = 0;
  private ArrayList<Result> old_results = new ArrayList<>();
  private ArrayList<Result> new_results = new ArrayList<>();

  private TrainingStorage ()
    {
      super(ContextManager.get(), DATABASE_NAME, null, DATABASE_VERSION);
    }

  public static TrainingStorage getInstance ()
    {
      if (instance == null)
        instance = new TrainingStorage();
      return instance;
    }

  public static void init ()
    {
      getInstance();

      fetch();
    }

  public static void fetch ()
    {
      getInstance().old_results.clear();
      getInstance().new_results.clear();

      SQLiteDatabase db = getInstance().getReadableDatabase();
      Cursor c = db.query(SessionEntry.TABLE_NAME, new String[]{ SessionEntry._ID, SessionEntry.COLUMN_NAME_DATE }, null, null, null, null, SessionEntry.COLUMN_NAME_DATE + " DESC", "1");

      if (c.getCount() <= 0)
        return;

      c.moveToFirst();
      getInstance().lastSession = c.getLong(c.getColumnIndex(SessionEntry.COLUMN_NAME_DATE));

      int sessionId = c.getInt(c.getColumnIndex(SessionEntry._ID));

      c.close();

      c = db.query(ExerciseEntry.TABLE_NAME, new String[]{ ExerciseEntry.COLUMN_NAME_NAME, ExerciseEntry.COLUMN_NAME_LEVEL, ExerciseEntry.COLUMN_NAME_VALUE }, ExerciseEntry.COLUMN_NAME_SESSION_ID + " = " + sessionId, null, null, null, null, null);

      c.moveToFirst();

      while (!c.isAfterLast())
        {
          getInstance().old_results.add(new Result(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_NAME_NAME)), c.getInt(c.getColumnIndex(ExerciseEntry.COLUMN_NAME_LEVEL)), c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_NAME_VALUE))));
          c.moveToNext();
        }
    }

  public static long getLastTrainingDate ()
    {
      if (getInstance().lastSession == 0)
        return System.currentTimeMillis();
      return getInstance().lastSession;
    }

  public static void recordResult (String name, int level, String value)
    {
      getInstance().new_results.add(new Result(name, level, value));
    }

  public static void finish ()
    {
      SQLiteDatabase db = getInstance().getWritableDatabase();

      // Create a new map of values, where column names are the keys
      ContentValues values = new ContentValues();
      values.put(SessionEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
      values.put(SessionEntry.COLUMN_NAME_NAME, TrainingManager.getCurrentTraining().getName());

      // Insert the new row, returning the primary key value of the new row
      long sessionId = db.insert(SessionEntry.TABLE_NAME, null, values);

      for (Result r : getInstance().new_results)
        {
          values = new ContentValues();
          values.put(ExerciseEntry.COLUMN_NAME_SESSION_ID, sessionId);
          values.put(ExerciseEntry.COLUMN_NAME_NAME, r.name);
          values.put(ExerciseEntry.COLUMN_NAME_LEVEL, r.level);
          values.put(ExerciseEntry.COLUMN_NAME_VALUE, r.value);

          db.insert(ExerciseEntry.TABLE_NAME, null, values);
        }

      fetch();
    }

  public static int getLastLevel (String name)
    {
      for (Result result : getInstance().old_results)
        if (result.name.equals(name))
          return result.level;

      return 0;
    }

  public static String getLastResult (String name, int level)
    {
      for (Result result : getInstance().old_results)
        if (result.name.equals(name) && result.level == level)
          return result.value;

      return null;
    }

  public void onCreate (SQLiteDatabase db)
    {
      db.execSQL(SQL_CREATE_SESSIONS);
      db.execSQL(SQL_CREATE_EXERCISES);
    }

  public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
      // This database is only a cache for online data, so its upgrade policy is
      // to simply to discard the data and start over
      db.execSQL(SQL_DELETE_SESSIONS);
      db.execSQL(SQL_DELETE_EXERCISES);
      onCreate(db);
    }

  public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
      onUpgrade(db, oldVersion, newVersion);
    }

  private static class Result
  {
    public String name;
    public int level;
    public String value;

    public Result (String name, int level, String value)
      {
        this.name = name;
        this.level = level;
        this.value = value;
      }
  }

  public static abstract class SessionEntry implements BaseColumns
  {
    public static final String TABLE_NAME = "sessions";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_NAME = "name";
  }

  public static abstract class ExerciseEntry implements BaseColumns
  {
    public static final String TABLE_NAME = "exercises";
    public static final String COLUMN_NAME_SESSION_ID = "sessionId";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_LEVEL = "level";
    public static final String COLUMN_NAME_VALUE = "value";
  }

}
