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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Column
{

  @SuppressWarnings("WeakerAccess")
  public static final String TYPE_TEXT = "TEXT";
  public static final String TYPE_INT = "INTEGER";
  public static final String TYPE_LONG = "INTEGER";

  public final String name;
  public final String type;
  public final String params;

  @Nullable
  private String value = null;

  public Column (@NonNull String name)
    {
      this(name, TYPE_TEXT);
    }

  public Column (@NonNull String name, @NonNull String type)
    {
      this(name, type, "");
    }

  public Column (@NonNull String name, @NonNull String type, @NonNull String params)
    {
      this.name = name;
      this.type = type;
      this.params = params;
    }

  @Nullable
  public String get ()
    {
      return this.value;
    }

  public long getLong ()
    {
      return Long.parseLong(this.value);
    }

  public int getInt ()
    {
      return Integer.parseInt(this.value);
    }

  public void set (@Nullable String value)
    {
      this.value = value;
    }

  public void set (long value)
    {
      set(Long.toString(value));
    }

}
