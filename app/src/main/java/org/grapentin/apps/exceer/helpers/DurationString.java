/*
 * comment
 */

package org.grapentin.apps.exceer.helpers;

public class DurationString
{

  public static long parseLong (String s)
    {
      long l = Long.parseLong(s.replaceAll("[\\D]", ""));
      String extension = s.replaceAll("[^a-zA-Z]", "");

      switch (extension)
        {
        case "s":
          return l * 1000;
        case "min":
          return l * 60 * 1000;
        default:
          return l;
        }
    }

}
