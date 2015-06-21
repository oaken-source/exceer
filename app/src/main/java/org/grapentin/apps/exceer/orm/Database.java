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

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.activity.MainActivity;
import org.grapentin.apps.exceer.helpers.Context;
import org.grapentin.apps.exceer.helpers.Reflection;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.models.Training;

import java.util.HashMap;

public class Database extends SQLiteOpenHelper
{

  private final static int DATABASE_VERSION = 1;
  private final static String DATABASE_NAME = "TrainingStorage.db";
  private final static Revision revisions[] = new Revision[]{
      new Revision("", "")
  };
  private final static HashMap<Class, LongSparseArray<BaseModel>> cache = new HashMap<>();
  @Nullable
  private static Database instance = null;

  private boolean deferCallback = false;

  private Database ()
    {
      super(Context.get(), DATABASE_NAME, null, DATABASE_VERSION);
    }

  @NonNull
  private static Database getInstance ()
    {
      if (instance == null)
        instance = new Database();
      return instance;
    }

  public static void init ()
    {
      getInstance();

      if (!getInstance().deferCallback)
        ((MainActivity)MainActivity.getInstance()).afterDatabaseInit();
    }

  @NonNull
  public static SQLiteDatabase getSession ()
    {
      return getInstance().getWritableDatabase();
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

  private static void importDefaults ()
    {
      XmlNode root;
      try
        {
          root = new XmlNode(Context.get().getResources().getXml(R.xml.trainings_default));
        }
      catch (Exception e)
        {
          throw new Error(e);
        }

      for (XmlNode n : root.getChildren("training"))
        add(Training.fromXml(n));
    }

  public void onCreate (@NonNull SQLiteDatabase db)
    {
      deferCallback = true;

      final ProgressDialog progress = new ProgressDialog(MainActivity.getInstance());
      progress.setTitle("Updating Database");
      progress.setMessage("Please wait while the database is updated...");
      progress.show();

      Runnable runnable = new Runnable()
      {
        @Override
        public void run ()
          {
            for (Class model : Reflection.getSubclassesOf(BaseModel.class))
              BaseModel.onCreate(model);

            importDefaults();

            progress.dismiss();
            ((MainActivity)MainActivity.getInstance()).afterDatabaseInit();
          }
      };
      new Thread(runnable).start();
    }

  public void onUpgrade (@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
    {
      deferCallback = true;

      final ProgressDialog progress = new ProgressDialog(MainActivity.getInstance());
      progress.setTitle("Updating Database");
      progress.setMessage("Please wait while the database is updated...");
      progress.show();

      Runnable runnable = new Runnable()
      {
        @Override
        public void run ()
          {
            for (int i = oldVersion + 1; i <= newVersion; ++i)
              revisions[i - 2].runUpgrade(db);

            progress.dismiss();
            ((MainActivity)MainActivity.getInstance()).afterDatabaseInit();
          }
      };
      new Thread(runnable).start();
    }

  public void onDowngrade (@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
    {
      deferCallback = true;

      final ProgressDialog progress = new ProgressDialog(MainActivity.getInstance());
      progress.setTitle("Updating Database");
      progress.setMessage("Please wait while the database is updated...");
      progress.show();

      Runnable runnable = new Runnable()
      {
        @Override
        public void run ()
          {
            for (int i = oldVersion; i > newVersion; --i)
              revisions[i - 2].runDowngrade(db);

            progress.dismiss();
            ((MainActivity)MainActivity.getInstance()).afterDatabaseInit();
          }
      };
      new Thread(runnable).start();
    }

  public void onDrop ()
    {
      for (Class model : Reflection.getSubclassesOf(BaseModel.class))
        BaseModel.onDrop(model);
    }

  private static class Revision
  {
    private final String upgradeSql;
    private final String downgradeSql;

    public Revision (@SuppressWarnings("SameParameterValue") String upgradeSql, @SuppressWarnings("SameParameterValue") String downgradeSql)
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

}
