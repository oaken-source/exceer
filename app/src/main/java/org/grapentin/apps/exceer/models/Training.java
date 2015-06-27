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
import android.widget.Button;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.activity.TrainingActivity;
import org.grapentin.apps.exceer.activity.base.BaseActivity;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.orm.BaseModel;
import org.grapentin.apps.exceer.orm.Column;
import org.grapentin.apps.exceer.orm.Relation;
import org.grapentin.apps.exceer.training.BaseExercisable;
import org.grapentin.apps.exceer.training.Properties;

public class Training extends BaseModel
{

  @SuppressWarnings("unused") // accessed by reflection from BaseModel
  public final static String TABLE_NAME = "trainings";

  // database layout
  private final Column name = new Column("name");
  private final Relation exercises = makeRelation(Exercise.class);
  private final Relation properties = makeRelation(Property.class);

  // temporary runtime values
  private int currentExerciseId = 0;
  private boolean finished = false;

  public static Training fromXml (@NonNull XmlNode root)
    {
      Training m = new Training();

      m.name.set(root.getAttribute("name"));

      for (XmlNode property : root.getChildren("property"))
        m.properties.add(Property.fromXml(property));
      for (XmlNode exercise : root.getChildren("exercise"))
        m.exercises.add(Exercise.fromXml(exercise));

      return m;
    }

  @Nullable
  public static Training get (long id)
    {
      return (Training)BaseModel.get(Training.class, id);
    }

  @Nullable
  private Exercise getCurrentExercise ()
    {
      return (Exercise)exercises.at(currentExerciseId);
    }

  @Nullable
  public BaseExercisable getLeafExercisable ()
    {
      if (getCurrentExercise() == null)
        return null;
      return getCurrentExercise().getLeafExercisable();
    }

  public boolean isRunning ()
    {
      return (getCurrentExercise() != null && getCurrentExercise().isRunning());
    }

  public boolean isFinished ()
    {
      return finished;
    }

  public void prepare ()
    {
      Properties props = new Properties(properties);

      for (BaseModel e : exercises.all())
        ((Exercise)e).prepare(props);

      currentExerciseId = 0;
      if (getCurrentExercise() == null)
        {
          show();
          return;
        }

      getCurrentExercise().show();
    }

  private void show ()
    {
      TextView currentExerciseLabel = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      TextView currentExerciseLevelLabel1 = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel1);
      TextView currentExerciseLevelLabel2 = (TextView)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel2);

      currentExerciseLabel.setText(BaseActivity.getContext().getString(R.string.TrainingActivityNoExercises));
      currentExerciseLevelLabel1.setText("");
      currentExerciseLevelLabel2.setText("");
    }

  public void next ()
    {
      currentExerciseId++;
      if (getCurrentExercise() == null)
        {
          Button contextButton = (Button)TrainingActivity.getInstance().findViewById(R.id.TrainingActivityContextButton);
          contextButton.setText(BaseActivity.getContext().getString(R.string.TrainingActivityContextButtonTextFinish));
          finished = true;
          return;
        }

      getCurrentExercise().getLeafExercisable().show();
    }

  public void reset ()
    {
      currentExerciseId = 0;
      finished = false;

      for (BaseModel e : exercises.all())
        ((Exercise)e).reset();
    }

  public void start ()
    {
      if (getLeafExercisable() != null)
        getLeafExercisable().start();
    }

  public void pause ()
    {
      if (getLeafExercisable() != null)
        getLeafExercisable().pause();
    }

  public void wrapUp ()
    {
      currentExerciseId = 0;
      finished = false;

      for (BaseModel e : exercises.all())
        ((Exercise)e).wrapUp();
    }

}
