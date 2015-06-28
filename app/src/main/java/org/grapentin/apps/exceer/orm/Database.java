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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.helpers.Reflection;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.models.Training;

import java.util.HashMap;

public class Database
{

  // database version and name
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "TrainingStorage.db";

  // database wrapper and revisions
  private static DatabaseWrapper database;
  private static final DatabaseRevision revisions[] = new DatabaseRevision[]{
      new DatabaseRevision("", "")
  };
  // object cache
  private static final HashMap<Class, LongSparseArray<BaseModel>> cache = new HashMap<>();

  private static boolean newly_created = false;

  static
    {
      new Thread(new Runnable()
      {
        public void run ()
          {
            Log.d("Database", "starting Initialization");

            database = new DatabaseWrapper();

            // trigger database migrations
            database.getWritableDatabase();
            if (newly_created)
              DatabaseWrapper.importDefaults();

            BaseActivity.initLock.countDown();
            Log.d("Database", "finished Initialization");
          }
      }).start();
    }

  @NonNull
  public static SQLiteDatabase getSession ()
    {
      return database.getWritableDatabase();
    }

  public static void add (@NonNull BaseModel b)
    {
      b.onInsert();
    }

  public static void addToCache (@NonNull BaseModel b)
    {
      if (!cache.containsKey(b.getClass()))
        cache.put(b.getClass(), new LongSparseArray<BaseModel>());
      cache.get(b.getClass()).put(b.getId(), b);
    }

  @Nullable
  public static BaseModel getFromCache (@NonNull Class c, long id)
    {
      if (!cache.containsKey(c))
        return null;
      if (cache.get(c).get(id, null) == null)
        return null;
      return cache.get(c).get(id);
    }

  public static void init ()
    {
      // nothing here. go look elsewhere.
    }

  private static class DatabaseRevision
  {
    private final String upgradeSql;
    private final String downgradeSql;

    public DatabaseRevision (String upgradeSql, String downgradeSql)
      {
        this.upgradeSql = upgradeSql;
        this.downgradeSql = downgradeSql;
      }

    public void runUpgrade (@NonNull SQLiteDatabase db)
      {
        db.execSQL(this.upgradeSql);
      }

    public void runDowngrade (@NonNull SQLiteDatabase db)
      {
        db.execSQL(this.downgradeSql);
      }
  }

  private static class DatabaseWrapper extends SQLiteOpenHelper
  {
    public DatabaseWrapper ()
      {
        super(BaseActivity.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
      }

    @Override
    public void onCreate (@NonNull SQLiteDatabase db)
      {
        Log.d("Database", "onCreate entered");
        for (Class model : Reflection.getSubclassesOf(BaseModel.class))
          BaseModel.onCreate(model, db);
        newly_created = true;
      }

    @Override
    public void onUpgrade (@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
      {
        Log.d("Database", "onUpgrade entered");
        for (int i = oldVersion + 1; i <= newVersion; ++i)
          revisions[i - 2].runUpgrade(db);
      }

    @Override
    public void onDowngrade (@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
      {
        Log.d("Database", "onDowngrade entered");
        for (int i = oldVersion; i > newVersion; --i)
          revisions[i - 2].runDowngrade(db);
      }

    public void onDrop ()
      {
        Log.d("Database", "onDrop entered");
        for (Class model : Reflection.getSubclassesOf(BaseModel.class))
          BaseModel.onDrop(model);
      }

    private static void importDefaults ()
      {
        Log.d("Database", "importDefaults entered");
        XmlNode root;
        try
          {
            root = new XmlNode(BaseActivity.getContext().getResources().getXml(R.xml.trainings_default));
          }
        catch (Exception e)
          {
            throw new Error(e);
          }

        for (XmlNode n : root.getChildren("training"))
          add(Training.fromXml(n));
      }
  }

}
