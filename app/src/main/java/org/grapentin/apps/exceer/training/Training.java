/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.util.Log;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.helpers.XmlNode;
import org.grapentin.apps.exceer.managers.ContextManager;

import java.io.Serializable;
import java.util.ArrayList;

public class Training implements Serializable
{

  private String name = null;

  private Properties properties = new Properties();

  private ArrayList<Exercise> exercises = new ArrayList<>();

  public Training (String name)
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
        if (training.getAttribute("name").equals(name))
          {
            this.name = name;
            for (XmlNode property : training.getChildren("property"))
              this.properties.set(property.getAttribute("name"), property.getValue());
            for (XmlNode exercise : training.getChildren("exercise"))
              this.exercises.add(new Exercise(exercise, properties));
            return;
          }

      Log.e("Training", "not found in data: " + name);
    }

}
