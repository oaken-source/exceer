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
import android.support.annotation.NonNull;

public class DatabaseRevision
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
