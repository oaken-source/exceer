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

import org.grapentin.apps.exceer.orm.Database;
import org.grapentin.apps.exceer.orm.annotations.DatabaseColumn;
import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

@DatabaseTable
public class Session
{

  @DatabaseColumn
  private long date;
  @DatabaseColumn
  private Training training;

  public Session (Training training)
    {
      this.date = System.currentTimeMillis();
      this.training = training;
    }

  @Nullable
  public static Session getLast ()
    {
      return (Session)Database.query(Session.class).orderBy("date DESC").first();
    }

  public long getDate ()
    {
      return date;
    }

}
