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
import android.util.Log;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.SplashActivity;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.models.Exercise;
import org.grapentin.apps.exceer.models.Level;
import org.grapentin.apps.exceer.models.Property;
import org.grapentin.apps.exceer.models.Session;
import org.grapentin.apps.exceer.models.Training;
import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

import java.util.HashMap;

public class Database
{

  // database version and name
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "Exceer.db";
  // database structure
  public static final HashMap<Class, Model> models = new HashMap<>();

  // revisions
  private static final DatabaseRevision revisions[] = new DatabaseRevision[]{ new DatabaseRevision("", "") };

  // database wrapper
  private static SQLiteOpenHelper database = new SQLiteOpenHelper(BaseActivity.getContext(), DATABASE_NAME, null, DATABASE_VERSION)
  {
    @Override
    public void onCreate (@NonNull SQLiteDatabase db)
      {
        for (Model m : models.values())
          m.onCreate(db);
        importDefaults(db);
      }

    @Override
    public void onUpgrade (@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
      {
        for (int i = oldVersion + 1; i <= newVersion; ++i)
          revisions[i - 2].runUpgrade(db);
      }

    @Override
    public void onDowngrade (@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
      {
        for (int i = oldVersion; i > newVersion; --i)
          revisions[i - 2].runDowngrade(db);
      }
  };

  private static void reflectModels ()
    {
      try
        {
          models.put(Exercise.class, new Model(Exercise.class));
          models.put(Level.class, new Model(Level.class));
          models.put(Property.class, new Model(Property.class));
          models.put(Session.class, new Model(Session.class));
          models.put(Training.class, new Model(Training.class));

          for (Model model : models.values())
            model.link();
        }
      catch (Exception e)
        {
          throw new DatabaseAccessException("failed to reflect models", e);
        }
    }

  @NonNull
  public static SQLiteDatabase getSession ()
    {
      return database.getWritableDatabase();
    }

  public static void add (@NonNull Object o)
    {
      add(o, getSession());
    }

  private static void add (@NonNull Object o, SQLiteDatabase db)
    {
      if (!o.getClass().isAnnotationPresent(DatabaseTable.class))
        throw new DatabaseAccessException(o.getClass().getSimpleName() + ": is not a model");

      models.get(o.getClass()).add(o, db);
    }

  public static void init ()
    {
      new Thread(new Runnable()
      {
        public void run ()
          {
            Log.d("Database", "starting Initialization");
            long start = System.currentTimeMillis();

            reflectModels();

            // trigger migrations
            database.getWritableDatabase();

            Log.d("Database", "finished Initialization (took " + (System.currentTimeMillis() - start) + "ms)");
          }
      }).start();
    }

  public static DatabaseQuery query (Class c)
    {
      if (!c.isAnnotationPresent(DatabaseTable.class))
        throw new DatabaseAccessException(c.getSimpleName() + ": is not a model");

      return new DatabaseQuery(c);
    }

  private static void importDefaults (SQLiteDatabase db)
    {
      XmlNode root;

      try
        {
          root = new XmlNode(BaseActivity.getContext().getResources().getXml(R.xml.trainings_default));
        }
      catch (Exception e)
        {
          throw new DatabaseAccessException("failed to import defaults", e);
        }

      for (XmlNode n : root.getChildren("training"))
        add(Training.fromXml(n), db);
    }

  public static class DatabaseAccessException extends RuntimeException
  {
    public DatabaseAccessException (String msg)
      {
        super(msg);
      }

    public DatabaseAccessException (String msg, Exception e)
      {
        super(msg, e);
      }
  }

}
