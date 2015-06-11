/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import org.grapentin.apps.exceer.helpers.DurationString;

import java.io.Serializable;
import java.util.ArrayList;

public class Duration implements Serializable
{

  private ArrayList<Long> parts = new ArrayList<>();

  public Duration (String s)
    {
      for (String part : s.split(","))
        parts.add(DurationString.parseLong(s));
    }

}
