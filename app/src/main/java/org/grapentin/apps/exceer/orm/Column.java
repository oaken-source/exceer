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

public class Column
{

  @SuppressWarnings("unused") // to be used by Models
  public static final String TYPE_TEXT = "TEXT";
  @SuppressWarnings("unused") // to be used by Models
  public static final String TYPE_INT = "INTEGER";
  @SuppressWarnings("unused") // to be used by Models
  public static final String TYPE_LONG = "INTEGER";

  public String name;
  public String type;
  public String params;

  private String value = null;

  public Column (String name)
    {
      this(name, TYPE_TEXT);
    }

  public Column (String name, String type)
    {
      this(name, type, "");
    }

  public Column (String name, String type, String params)
    {
      this.name = name;
      this.type = type;
      this.params = params;
    }

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

  public void set (String value)
    {
      this.value = value;
    }

  public void set (long value)
    {
      set(Long.toString(value));
    }

}
