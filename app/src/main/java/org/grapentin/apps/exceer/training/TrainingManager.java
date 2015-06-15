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

import android.app.Activity;
import android.widget.Toast;

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

  public static void init ()
    {
      XmlNode root;
      try
        {
          root = new XmlNode(ContextManager.get().getResources().getXml(R.xml.training));
        }
      catch (Exception e)
        {
          Toast.makeText(ContextManager.get(), "failed to parse config: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
