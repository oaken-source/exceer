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

public class ModelTraining extends BaseModel
{

  protected final static String TABLE_NAME = "trainings";

  public Column name = new Column("name");

  public Relation exercises = makeRelation("exercises", ModelExercise.class);
  public Relation properties = makeRelation("properties", ModelProperty.class);

  public static ModelTraining fromXml (XmlNode root)
    {
      ModelTraining m = new ModelTraining();

      m.name.set(root.getAttribute("name"));

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(ModelProperty.fromXml(property));
      for (XmlNode exercise : root.getChildren("exercise"))
        m.exercises.add(ModelExercise.fromXml(exercise));

      return m;
    }

  public static ModelTraining get (long id)
    {
      return (ModelTraining)BaseModel.get(ModelTraining.class, id);
    }

  public static ArrayList<ModelTraining> getAll ()
    {
      ArrayList<ModelTraining> out = new ArrayList<>();

      for (long id : BaseModel.getAllIds(ModelTraining.class))
        out.add(get(id));

      return out;
    }

}
