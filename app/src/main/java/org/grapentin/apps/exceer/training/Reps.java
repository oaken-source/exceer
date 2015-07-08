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

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Reps implements Serializable, Comparable
{

  public final ArrayList<Long> sets = new ArrayList<>();

  public Reps ()
    {

    }

  public Reps (@NonNull Reps reps)
    {
      for (long part : reps.sets)
        this.sets.add(part);
    }

  @NonNull
  public static Reps fromString (@NonNull String s) throws RepsFormatException
    {
      Reps r = new Reps();
      if (s.trim().length() == 0)
        return r;

      try
        {
          for (String part : s.split(",", -1))
            r.sets.add(Long.parseLong(part));
        }
      catch (NumberFormatException e)
        {
          throw new RepsFormatException("Invalid format: '" + s + "'", e);
        }
      return r;
    }

  @NonNull
  public static String toString (@NonNull Reps r)
    {
      String res = "";
      for (long part : r.sets)
        res += (res.equals("") ? "" : ",") + part;
      return res;
    }

  @NonNull
  public Reps empty ()
    {
      Reps res = new Reps();
      for (int i = 0; i < this.sets.size(); ++i)
        res.sets.add(0L);
      return res;
    }

  public void increment (long increment, @NonNull Reps finish, @NonNull IncrementDirection incrementDirection, @NonNull IncrementStyle incrementStyle)
    {
      int start = (incrementDirection == IncrementDirection.front_to_back) ? 0 : sets.size() - 1;
      int end = (incrementDirection == IncrementDirection.front_to_back) ? sets.size() : -1;
      int inc = (incrementDirection == IncrementDirection.front_to_back) ? 1 : -1;

      for (int i = 0; i < increment; ++i)
        for (int pos = start; pos != end; pos = pos + inc)
          if (sets.get(pos) < finish.sets.get(pos) && (incrementStyle == IncrementStyle.fill_sets || sets.get(pos) <= Collections.min(sets)))
            {
              sets.set(pos, sets.get(pos) + 1);
              break;
            }
    }

  @NonNull
  public String toString ()
    {
      return toString(this);
    }

  @Override
  public int compareTo (@NonNull Object another) throws RepsMismatchException
    {
      Reps r = (Reps)another;
      if (sets.size() != r.sets.size())
        throw new RepsMismatchException("Reps of size " + sets.size() + " can not be compared to reps of size " + r.sets.size());

      for (int i = 0; i != sets.size(); ++i)
        if (!sets.get(i).equals(r.sets.get(i)))
          return (int)(sets.get(i) - r.sets.get(i));

      return 0;
    }

  public String toProgressString ()
    {
      String out = "";
      for (Long set : sets)
        out += (out.equals("") ? "" : "  ") + "0/" + set;
      return out;
    }

  public int getProgressMax ()
    {
      int max = 0;
      for (Long set : sets)
        max += set;
      return max;
    }

  public enum IncrementDirection
  {
    front_to_back,
    back_to_front
  }

  public enum IncrementStyle
  {
    balanced,
    fill_sets
  }

  public static class RepsFormatException extends RuntimeException
  {
    public RepsFormatException (@NonNull String msg, @NonNull Exception inner)
      {
        super(msg, inner);
      }
  }

  public static class RepsMismatchException extends RuntimeException
  {
    public RepsMismatchException (@NonNull String msg)
      {
        super(msg);
      }
  }

}
