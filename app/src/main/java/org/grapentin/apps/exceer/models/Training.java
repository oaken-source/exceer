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

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.service.DatabaseService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@DatabaseTable
public class Training implements Serializable
{

  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField
  private String name;

  @ForeignCollectionField
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private ForeignCollection<Exercise> exercisesField;
  private ArrayList<Exercise> exercises = null;

  public static void fromXml (@NonNull XmlNode root)
    {
      Training t = new Training();
      t.name = root.getAttribute("name");
      DatabaseService.add(t);

      for (XmlNode exercise : root.getChildren("exercise"))
        Exercise.fromXml(exercise, t);
    }

  @Nullable
  public static Training get (int id)
    {
      //noinspection unchecked
      return (Training)DatabaseService.query(Training.class).get(id);
    }

  public int getId ()
    {
      return id;
    }

  public String getName ()
    {
      return name;
    }

  public int getNumberOfExercises ()
    {
      if (exercises == null)
        collectExercises();
      return exercises.size();
    }

  public Exercise getExercise (int position)
    {
      if (exercises == null)
        collectExercises();
      return exercises.get(position);
    }

  private void collectExercises ()
    {
      exercises = new ArrayList<>();
      Iterator<Exercise> i = exercisesField.closeableIterator();
      while (i.hasNext())
        i.next().collectExercises(exercises);
    }

}
