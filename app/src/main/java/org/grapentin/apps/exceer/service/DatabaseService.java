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

package org.grapentin.apps.exceer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.models.Exercise;
import org.grapentin.apps.exceer.models.Level;
import org.grapentin.apps.exceer.models.Property;
import org.grapentin.apps.exceer.models.Session;
import org.grapentin.apps.exceer.models.Training;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;


public class DatabaseService extends Service
{

  // database version and name
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "Exceer.db";

  // revisions
  private static final DatabaseRevision revisions[] = new DatabaseRevision[]
      {
          new DatabaseRevision("")
      };
  private static LocalBinder local = null;
  private final IBinder binder = new LocalBinder();
  public CountDownLatch initLock = new CountDownLatch(1);
  // database wrapper
  private DatabaseOpenHelper database;

  @NonNull
  public static DatabaseQuery query (Class c)
    {
      return local.query(c);
    }

  public static void add (Object o)
    {
      try
        {
          local.add(o);
        }
      catch (SQLException e)
        {
          throw new DatabaseAccessException("failed to add object of type " + o.getClass().getSimpleName(), e);
        }
    }

  @Override
  public void onCreate ()
    {
      super.onCreate();

      new Thread(new Runnable()
      {
        @Override
        public void run ()
          {
            initialize();
          }
      }).start();
    }

  @Override
  public void onDestroy ()
    {
      Log.d("DatabaseService", "onDestroy");
    }

  void initialize ()
    {
      Log.d("DatabaseService", "starting Initialization");

      database = new DatabaseOpenHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
      database.initDaos();
      database.getWritableDatabase();

      initLock.countDown();

      Log.d("DatabaseService", "finished Initialization");
    }

  @Override
  public IBinder onBind (Intent intent)
    {
      local = (LocalBinder)binder;
      return binder;
    }

  private static class DatabaseRevision
  {
    private final String upgradeSql;

    public DatabaseRevision (String upgradeSql)
      {
        this.upgradeSql = upgradeSql;
      }

    public void runUpgrade (@NonNull SQLiteDatabase db)
      {
        db.execSQL(this.upgradeSql);
      }
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

  public static class DatabaseQuery<T, K>
  {
    private Dao<T, K> dao;
    private QueryBuilder<T, K> builder;

    protected DatabaseQuery (@NonNull Dao<T, K> dao)
      {
        this.dao = dao;
        this.builder = dao.queryBuilder();
      }

    public T get (K id)
      {
        try
          {
            return dao.queryForId(id);
          }
        catch (SQLException e)
          {
            throw new DatabaseAccessException("query failed", e);
          }
      }

    public T first ()
      {
        try
          {
            return builder.queryForFirst();
          }
        catch (SQLException e)
          {
            throw new DatabaseAccessException("query failed", e);
          }
      }

    public DatabaseQuery orderBy (String order, boolean ascending)
      {
        builder.orderBy(order, ascending);
        return this;
      }

    public long count ()
      {
        try
          {
            return builder.countOf();
          }
        catch (SQLException e)
          {
            throw new DatabaseAccessException("query failed", e);
          }
      }
  }

  private class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper
  {
    public Dao<Training, Integer> DaoTraining;
    public Dao<Exercise, Integer> DaoExercise;
    public Dao<Level, Integer> DaoLevel;
    public Dao<Property, Integer> DaoProperty;
    public Dao<Session, Integer> DaoSession;

    public DatabaseOpenHelper (Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion)
      {
        super(context, databaseName, factory, databaseVersion);
      }

    @Override
    public void onCreate (SQLiteDatabase db, ConnectionSource source)
      {
        Log.d("DatabaseService", "onCreate");
        try
          {
            TableUtils.createTable(connectionSource, Training.class);
            TableUtils.createTable(connectionSource, Exercise.class);
            TableUtils.createTable(connectionSource, Level.class);
            TableUtils.createTable(connectionSource, Property.class);
            TableUtils.createTable(connectionSource, Session.class);

            XmlNode root = new XmlNode(getResources().getXml(R.xml.trainings_default));
            for (XmlNode n : root.getChildren("training"))
              Training.fromXml(n);
          }
        catch (Exception e)
          {
            throw new DatabaseAccessException("failed to access database", e);
          }
      }

    @Override
    public void onUpgrade (SQLiteDatabase db, ConnectionSource source, int oldVersion, int newVersion)
      {
        for (int i = oldVersion + 1; i <= newVersion; ++i)
          revisions[i - 2].runUpgrade(db);
      }

    public void initDaos ()
      {
        try
          {
            DaoTraining = getDao(Training.class);
            DaoExercise = getDao(Exercise.class);
            DaoLevel = getDao(Level.class);
            DaoProperty = getDao(Property.class);
            DaoSession = getDao(Session.class);
          }
        catch (SQLException e)
          {
            throw new DatabaseAccessException("failed to access database", e);
          }
      }
  }

  public class LocalBinder extends Binder
  {
    public DatabaseQuery query (Class c)
      {
        if (c == Training.class)
          return new DatabaseQuery<>(database.DaoTraining);
        if (c == Exercise.class)
          return new DatabaseQuery<>(database.DaoExercise);
        if (c == Level.class)
          return new DatabaseQuery<>(database.DaoLevel);
        if (c == Property.class)
          return new DatabaseQuery<>(database.DaoProperty);
        if (c == Session.class)
          return new DatabaseQuery<>(database.DaoSession);
        throw new DatabaseAccessException(c.getSimpleName() + ": queried class is no model");
      }

    public void add (Object o) throws SQLException
      {
        Class c = o.getClass();
        if (c == Training.class)
          database.DaoTraining.create((Training)o);
        else if (c == Exercise.class)
          database.DaoExercise.create((Exercise)o);
        else if (c == Level.class)
          database.DaoLevel.create((Level)o);
        else if (c == Property.class)
          database.DaoProperty.create((Property)o);
        else if (c == Session.class)
          database.DaoSession.create((Session)o);
        else
          throw new DatabaseAccessException(o.getClass().getSimpleName() + ": queried class is no model");
      }

    public void await ()
      {
        while (initLock.getCount() > 0)
          try
            {
              initLock.await();
            }
          catch (InterruptedException e)
            {
              // just retry
            }
      }
  }

}
