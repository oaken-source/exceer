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

package org.grapentin.apps.exceer.models;

import android.support.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.grapentin.apps.exceer.service.DatabaseService;

@DatabaseTable
public class Session
{

  @DatabaseField(generatedId = true)
  int id;

  @DatabaseField
  private long date;

  @DatabaseField(foreign = true)
  private Workout workout;

  public Session ()
    {

    }

  public Session (Workout workout)
    {
      this.date = System.currentTimeMillis();
      this.workout = workout;
    }

  public static long count ()
    {
      return DatabaseService.query(Session.class).count();
    }

  @Nullable
  public static Session getLast ()
    {
      return (Session)DatabaseService.query(Session.class).orderBy("date", false).first();
    }

  public long getDate ()
    {
      return date;
    }

}
