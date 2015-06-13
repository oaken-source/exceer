/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.app.Activity;
import android.util.Log;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.managers.ContextManager;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainingList implements Serializable
{

  private ArrayList<Training> trainings = new ArrayList<>();
  private int currentTrainingId = 0;

  public TrainingList (Activity gui)
    {
      XmlNode root;
      try
        {
          root = new XmlNode(ContextManager.get().getResources().getXml(R.xml.training));
        }
      catch (Exception e)
        {
          Log.e("Training", "failed to parse training.xml", e);
          return;
        }

      for (XmlNode training : root.getChildren("training"))
        trainings.add(new Training(training, gui));
    }

  public Training getCurrentTraining ()
    {
      return trainings.get(currentTrainingId);
    }

  public Exercisable getCurrentExercisable ()
    {
      return getCurrentTraining().getCurrentExercisable();
    }

  public void setCurrentTraining (String name)
    {
      for (int i = 0; i < trainings.size(); ++i)
        if (trainings.get(i).getName().equals(name))
          currentTrainingId = i;

      getCurrentTraining().getCurrentExercisable().prepare();
    }

}
