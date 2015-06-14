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

public class TrainingManager implements Serializable
{

  private static TrainingManager instance = null;
  private ArrayList<Training> trainings = new ArrayList<>();
  private int currentTrainingId = 0;

  private Activity gui = null;

  private TrainingManager ()
    {
    }

  public static TrainingManager getInstance ()
    {
      if (instance == null)
        instance = new TrainingManager();
      return instance;
    }

  public static void setInstance (TrainingManager inst)
    {
      instance = inst;
    }

  public static void init ()
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
        getInstance().trainings.add(new Training(training));
    }

  public static Training getCurrentTraining ()
    {
      return getInstance().trainings.get(getInstance().currentTrainingId);
    }

  public static void setCurrentTraining (String name)
    {
      for (int i = 0; i < getInstance().trainings.size(); ++i)
        if (getInstance().trainings.get(i).getName().equals(name))
          getInstance().currentTrainingId = i;

      getCurrentTraining().getCurrentExercisable().prepare();
    }

  public static Exercisable getCurrentExercisable ()
    {
      return getCurrentTraining().getCurrentExercisable();
    }

  public static void next ()
    {
      getCurrentTraining().next();
    }

  public static void clear ()
    {
      getCurrentTraining().clear();
    }

  public static Activity getGui ()
    {
      return getInstance().gui;
    }

  public static void setGui (Activity gui)
    {
      getInstance().gui = gui;
    }

}
