/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import org.grapentin.apps.exceer.helpers.XmlNode;

import java.io.Serializable;

public class Level implements Serializable
{

  private String name = null;

  private Properties properties;

  public Level (XmlNode root, Properties properties)
    {
      this.name = root.getAttribute("name");
      this.properties = new Properties(properties);

      for (XmlNode property : root.getChildren("property"))
        this.properties.set(property.getAttribute("name"), property.getValue());
    }

}
