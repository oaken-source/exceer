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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.orm.Database;
import org.grapentin.apps.exceer.orm.annotations.DatabaseColumn;
import org.grapentin.apps.exceer.orm.annotations.DatabaseTable;

@DatabaseTable
public class Property
{

  @DatabaseColumn(id = true)
  private int id;

  @DatabaseColumn
  private String key;
  @DatabaseColumn
  private String value;

  public static Property fromXml (@NonNull XmlNode root)
    {
      Property m = new Property();

      m.key = root.getAttribute("name");
      m.value = root.getValue();

      return m;
    }

  @Nullable
  public static Property get (int id)
    {
      return (Property)Database.query(Property.class).get(id);
    }

  public String getKey ()
    {
      return key;
    }

  public String getValue ()
    {
      return value;
    }

}
