/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.app.Activity;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;

import java.io.Serializable;
import java.util.ArrayList;

public class Training extends Exercisable implements Serializable
{

  private String name = null;

  private ArrayList<Exercise> exercises = new ArrayList<>();
  private int currentExerciseId = 0;

  public Training (XmlNode root, Activity gui)
    {
      super(new Properties(), gui);

      this.name = root.getAttribute("name");

      for (XmlNode property : root.getChildren("property"))
        setProperty(property.getAttribute("name"), property.getValue());
      for (XmlNode exercise : root.getChildren("exercise"))
        this.exercises.add(new Exercise(exercise, properties, gui));
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

  @Override
  public void prepare ()
    {
      TextView currentExerciseLabel = (TextView)gui.findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      TextView currentExerciseLevelLabel1 = (TextView)gui.findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel1);
      TextView currentExerciseLevelLabel2 = (TextView)gui.findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel2);

      currentExerciseLabel.setText(getName());
      currentExerciseLevelLabel1.setText("");
      currentExerciseLevelLabel2.setText("");

      super.prepare();
    }

}
