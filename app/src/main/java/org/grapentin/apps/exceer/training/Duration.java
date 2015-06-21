package org.grapentin.apps.exceer.training;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Duration implements Serializable, Comparable
{

  private long duration;

  public Duration ()
    {
      duration = 0;
    }

  public static Duration fromString (@NonNull String s)
    {
      Duration d = new Duration();

      try
        {
          d.duration = Long.parseLong(s.trim().replaceFirst("[\\D]*$", ""));
        }
      catch (NumberFormatException e)
        {
          throw new DurationFormatException("Invalid format: '" + s + "'", e);
        }

      String extension = s.trim().replaceFirst("^[^\\D]*", "").trim();
      switch (extension)
        {
        case "":
          break;
        case "s":
          d.duration *= 1000;
          break;
        case "min":
          d.duration *= 60 * 1000;
          break;
        default:
          throw new DurationFormatException("Invalid format: '" + s + "'");
        }

      return d;
    }

  @NonNull
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

  public void increment (Duration diff)
    {
      duration += diff.duration;
    }

  @Override
  public int compareTo (@NonNull Object another)
    {
      Duration d = (Duration)another;
      return (int)(duration - d.duration);
    }

  public static class DurationFormatException extends RuntimeException
  {
    public DurationFormatException (String msg)
      {
        super(msg);
      }
    public DurationFormatException (String msg, Exception e)
      {
        super(msg, e);
      }
  }

}
