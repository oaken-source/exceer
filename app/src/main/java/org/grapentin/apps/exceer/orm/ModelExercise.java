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


import org.grapentin.apps.exceer.helpers.XmlNode;

import java.util.ArrayList;

public class ModelExercise extends BaseModel
{

  protected final static String TABLE_NAME = "exercises";

  public Column name = new Column("name");

  public Relation levels = makeRelation("levels", ModelLevel.class);
  public Relation exercises = makeRelation("exercises", ModelExercise.class);
  public Relation properties = makeRelation("properties", ModelProperty.class);

  public static ModelExercise fromXml (XmlNode root)
    {
      ModelExercise m = new ModelExercise();

      m.name.set(root.getAttribute("name"));

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(ModelProperty.fromXml(property));
      for (XmlNode exercise : root.getChildren("exercise"))
        m.exercises.add(ModelExercise.fromXml(exercise));
      for (XmlNode level : root.getChildren("level"))
        m.levels.add(ModelLevel.fromXml(level));

      return m;
    }

  public static ModelExercise get (long id)
    {
      return (ModelExercise)BaseModel.get(ModelExercise.class, id);
    }

  public static ArrayList<ModelExercise> getAll ()
    {
      ArrayList<ModelExercise> out = new ArrayList<>();

      for (long id : BaseModel.getAllIds(ModelExercise.class))
        out.add(get(id));

      return out;
    }

}
