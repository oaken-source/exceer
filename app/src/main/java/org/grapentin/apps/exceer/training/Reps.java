/*
 * comment
 */

package org.grapentin.apps.exceer.training;

import java.io.Serializable;
import java.util.ArrayList;

public class Reps implements Serializable
{

  public ArrayList<Long> sets = new ArrayList<>();

  public Reps ()
    {

    }

  public Reps (Reps reps)
    {
      for (long part : reps.sets)
        this.sets.add(part);
    }

  public Reps (String s)
    {
      for (String part : s.split(","))
        this.sets.add(Long.parseLong(part));
    }

  public Reps empty ()
    {
      Reps res = new Reps();
      for (int i = 0; i < this.sets.size(); ++i)
        res.sets.add(0L);

      return res;
    }

  public boolean greaterOrEqual (Reps reps)
    {
      if (this.sets.size() < reps.sets.size())
        return false;

      for (int i = 0; i < this.sets.size(); ++i)
        if (this.sets.get(i) < reps.sets.get(i))
          return false;

      return true;
    }

  public void increment (Properties p)
    {
      for (int i = this.sets.size(); i < p.reps_finish.sets.size(); ++i)
        this.sets.add((p.reps_begin.sets.size() <= i) ? 0 : p.reps_begin.sets.get(i));

      for (int i = this.sets.size(); i > p.reps_finish.sets.size(); --i)
        this.sets.remove(i);

      if (p.reps_increment_direction == Properties.RepsIncrementDirection.front_to_back)
        for (int i = 0; i < this.sets.size(); ++i)
          if (incrementPosition(i, p))
            return;

      if (p.reps_increment_direction == Properties.RepsIncrementDirection.back_to_front)
        for (int i = this.sets.size() - 1; i >= 0; ++i)
          if (incrementPosition(i, p))
            return;
    }

  private boolean incrementPosition (int i, Properties p)
    {
      if (p.reps_increment_style == Properties.RepsIncrementStyle.fill_sets)
        if (this.sets.get(i) < p.reps_finish.sets.get(i))
          {
            this.sets.set(i, this.sets.get(i) + 1);
            return true;
          }

      if (p.reps_increment_style == Properties.RepsIncrementStyle.balanced)
        if (this.sets.get(i) < p.reps_finish.sets.get(i) && this.sets.get(i) == getMin())
          {
            this.sets.set(i, this.sets.get(i) + 1);
            return true;
          }

      return false;
    }

  private long getMin ()
    {
      long min = this.sets.get(0);
      for (long part : sets)
        if (part < min)
          min = part;
      return min;
    }

  public String toString ()
    {
      String res = "";
      for (long part : sets)
        res += (res.equals("") ? "" : ",") + part;
      return res;
    }

}
