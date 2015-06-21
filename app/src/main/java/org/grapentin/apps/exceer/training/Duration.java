package org.grapentin.apps.exceer.training;

import android.support.annotation.NonNull;

public class Duration
{

  private long duration;

  public Duration (String value)
    {
      duration = parseLong(value);
    }

  public static long parseLong (@NonNull String s)
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

  @NonNull
  @SuppressWarnings("WeakerAccess")
  public static String toString (@NonNull Duration d)
    {
      if (d.duration == 0)
        return "0";
      if (d.duration % (60 * 1000) == 0)
        return "" + (d.duration / (60 * 1000)) + "min";
      if (d.duration % 1000 == 0)
        return "" + (d.duration / 1000) + "s";
      return "" + d.duration;
    }

  @NonNull
  public String toString ()
    {
      return toString(this);
    }

  public long get ()
    {
      return duration;
    }

  public boolean greaterOrEqual (@NonNull Duration other)
    {
      return this.duration >= other.duration;
    }

  public void increment (long diff)
    {
      duration += diff;
    }

}
