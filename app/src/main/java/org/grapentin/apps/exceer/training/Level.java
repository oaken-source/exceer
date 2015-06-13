/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.app.Activity;
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

  public Level (XmlNode root, Properties properties, Activity gui, Exercise parent, int level)
    {
      super(properties, gui);

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
      TextView currentExerciseLabel = (TextView)gui.findViewById(R.id.TrainingActivityCurrentExerciseLabel);
      TextView currentExerciseLevelLabel1 = (TextView)gui.findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel1);
      TextView currentExerciseLevelLabel2 = (TextView)gui.findViewById(R.id.TrainingActivityCurrentExerciseLevelLabel2);

      currentExerciseLabel.setText(parent.getName());
      currentExerciseLevelLabel1.setText(ContextManager.get().getString(R.string.TrainingActivityCurrentExerciseLevelInt) + level);
      currentExerciseLevelLabel2.setText(getName());

      super.prepare();
    }

}
