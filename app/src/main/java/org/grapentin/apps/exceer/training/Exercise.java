/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import android.util.Log;

import org.grapentin.apps.exceer.helpers.XmlNode;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercise implements Serializable
{

  private String name = null;

  private Properties properties;

  private ArrayList<Exercise> exercises = new ArrayList<>();
  private ArrayList<Level> levels = new ArrayList<>();

  public Exercise (XmlNode root, Properties properties)
    {
      this.name = root.getAttribute("name");
      this.properties = new Properties(properties);

      for (XmlNode property : root.getChildren("property"))
        this.properties.set(property.getAttribute("name"), property.getValue());
      for (XmlNode exercise : root.getChildren("exercise"))
        this.exercises.add(new Exercise(exercise, properties));
      for (XmlNode level : root.getChildren("level"))
        this.levels.add(new Level(level, properties));
    }

}
