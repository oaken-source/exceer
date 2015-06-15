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

package org.grapentin.apps.exceer.training;

import android.widget.Button;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.managers.ContextManager;

import java.io.Serializable;
import java.util.ArrayList;

public class Training extends Exercisable implements Serializable
{

  private String name = null;

  private ArrayList<Exercise> exercises = new ArrayList<>();
  private int currentExerciseId = 0;

  private boolean finished = false;

  public Training (XmlNode root)
    {
      super(new Properties());

      this.name = root.getAttribute("name");

      for (XmlNode property : root.getChildren("property"))
        setProperty(property.getAttribute("name"), property.getValue());
      for (XmlNode exercise : root.getChildren("exercise"))
        this.exercises.add(new Exercise(exercise, properties));
    }

  public Exercisable getCurrentExercisable ()
    {
      if (!exercises.isEmpty())
        return exercises.get(currentExerciseId).getCurrentExercisable();
      return this;
    }

  public String getName ()
    {
      return this.name;
    }

  public boolean isFinished ()
    {
      return finished;
    }

  @Override
  public void prepare ()
    {
      TextView currentExerciseLabel = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      TextView currentExerciseLevelLabel1 = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel1);
      TextView currentExerciseLevelLabel2 = (TextView)TrainingManager.getGui().findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel2);

      currentExerciseLabel.setText(getName());
      currentExerciseLevelLabel1.setText("");
      currentExerciseLevelLabel2.setText("");

      super.prepare();
    }

  public void recordResult (String result)
    {
      TrainingStorage.recordResult(name, 0, result);
    }

  public String fetchResult ()
    {
      return TrainingStorage.getLastResult(name, 0);
    }

  public boolean levelUp ()
    {
      return false;
    }

  public void next ()
    {
      if (currentExerciseId >= exercises.size() - 1)
        {
          Button contextButton = (Button)TrainingManager.getGui().findViewById(R.id.TrainingActivityContextButton);
          contextButton.setText(ContextManager.get().getString(R.string.TrainingActivityContextButtonTextFinish));
          finished = true;
        }
      else
        {
          currentExerciseId++;
          getCurrentExercisable().prepare();
        }
    }

  public void clear ()
    {
      getCurrentExercisable().pause();
      currentExerciseId = 0;
      finished = false;
      getCurrentExercisable().prepare();
    }

}
