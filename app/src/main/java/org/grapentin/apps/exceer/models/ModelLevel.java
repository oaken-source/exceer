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

import org.grapentin.apps.exceer.helpers.XmlNode;

import java.util.ArrayList;

public class ModelLevel extends BaseModel
{

  protected final static String TABLE_NAME = "levels";

  public Column name = new Column("name");

  public Relation properties = makeRelation("properties", ModelProperty.class);

  public static ModelLevel fromXml (XmlNode root)
    {
      ModelLevel m = new ModelLevel();

      m.name.set(root.getAttribute("name"));

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(ModelProperty.fromXml(property));

      return m;
    }

  public static ModelLevel get (long id)
    {
      return (ModelLevel)BaseModel.get(ModelLevel.class, id);
    }

  public static ArrayList<ModelLevel> getAll ()
    {
      ArrayList<ModelLevel> out = new ArrayList<>();

      for (long id : BaseModel.getAllIds(ModelLevel.class))
        out.add(get(id));

      return out;
    }

}
