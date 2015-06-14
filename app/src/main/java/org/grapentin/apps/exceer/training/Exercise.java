/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.util.Log;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercise extends Exercisable implements Serializable
{

  private String name = null;

  private ArrayList<Exercise> exercises = new ArrayList<>();
  private int currentExerciseId = 0;

  private ArrayList<Level> levels = new ArrayList<>();
  private int currentLevelId = 0;

  public Exercise (XmlNode root, Properties properties)
    {
      super(properties);

      this.name = root.getAttribute("name");

      for (XmlNode property : root.getChildren("property"))
        setProperty(property.getAttribute("name"), property.getValue());
      for (XmlNode exercise : root.getChildren("exercise"))
        this.exercises.add(new Exercise(exercise, properties));

      ArrayList<XmlNode> levels = root.getChildren("level");
      for (int i = 0; i < levels.size(); ++i)
        this.levels.add(new Level(levels.get(i), this.properties, this, i + 1));

      currentLevelId = TrainingStorage.getLastLevel(this.name);

      if (currentLevelId > this.levels.size() - 1 && !this.levels.isEmpty())
        currentLevelId = this.levels.size() - 1;

      for (int i = 0; i < exercises.size(); ++i)
        if (TrainingStorage.getLastResult(exercises.get(i).getName(), exercises.get(i).currentLevelId) != null)
          {
            currentExerciseId = (i + 1) % exercises.size();
            break;
          }
    }

  public Exercisable getCurrentExercisable ()
    {
      if (!exercises.isEmpty())
        return exercises.get(currentExerciseId).getCurrentExercisable();
      if (!levels.isEmpty())
        return levels.get(currentLevelId).getCurrentExercisable();
      return this;
    }

  public String getName ()
    {
      return this.name;
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
      TrainingStorage.recordResult(name, currentLevelId, result);
    }

  public String fetchResult ()
    {
      if (levels.isEmpty())
        return TrainingStorage.getLastResult(name, 0);

      return TrainingStorage.getLastResult(name, currentLevelId);
    }

  public boolean levelUp ()
    {
      Log.d("Exercise", "levelup!");
      if (currentLevelId >= levels.size() - 1)
        return false;

      currentLevelId++;
      getCurrentExercisable().prepare();
      return true;
    }

}
