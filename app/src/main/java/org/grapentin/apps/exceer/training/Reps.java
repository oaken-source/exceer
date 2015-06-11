/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import java.io.Serializable;
import java.util.ArrayList;

public class Reps implements Serializable
{

  private ArrayList<Long> parts = new ArrayList<>();

  public Reps (String s)
    {
      for (String part : s.split(","))
        parts.add(Long.parseLong(part));
    }

}
