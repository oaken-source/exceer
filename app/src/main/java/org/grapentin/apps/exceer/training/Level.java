/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.managers.ContextManager;

import java.io.Serializable;

public class Level extends Exercisable implements Serializable
{

  private String name = null;

  private Exercise parent;

  private int level;

  public Level (XmlNode root, Properties properties, Exercise parent, int level)
    {
      super(properties);

      this.name = root.getAttribute("name");
      this.parent = parent;
      this.level = level;

      for (XmlNode property : root.getChildren("property"))
        setProperty(property.getAttribute("name"), property.getValue());
    }

  public Exercisable getCurrentExercisable ()
    {
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

      currentExerciseLabel.setText(parent.getName());
      currentExerciseLevelLabel1.setText(ContextManager.get().getString(R.string.TrainingActivityCurrentExerciseLevelInt) + level);
      currentExerciseLevelLabel2.setText(getName());

      super.prepare();
    }

  public void recordResult (String result)
    {
      parent.recordResult(result);
    }

  public String fetchResult ()
    {
      return parent.fetchResult();
    }

  public boolean levelUp ()
    {
      return parent.levelUp();
    }

}
