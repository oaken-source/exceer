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

import android.test.InstrumentationTestCase;

public class RepsTest extends InstrumentationTestCase
{

  public void testEmptyConstructor () throws Exception
    {
      Reps r = new Reps();
      assertEquals(r.sets.size(), 0);
    }

  public void testCopyConstructor () throws Exception
    {
      Reps r1 = new Reps();
      r1.sets.add(5L);
      Reps r2 = new Reps(r1);

      assertTrue(r2.sets.size() == r1.sets.size());
      assertTrue(r2.sets.get(0).equals(r1.sets.get(0)));

      r1.sets.set(0, r1.sets.get(0) + 1);
      r1.sets.add(6L);

      assertFalse(r2.sets.size() == r1.sets.size());
      assertFalse(r2.sets.get(0).equals(r1.sets.get(0)));
    }

  public void testFromString () throws Exception
    {
      Reps r = Reps.fromString("");
      assertTrue(r.sets.size() == 0);

      r = Reps.fromString("1");
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 1);

      r = Reps.fromString("1,2,3");
      assertTrue(r.sets.size() == 3);
      assertTrue(r.sets.get(0) == 1);
      assertTrue(r.sets.get(1) == 2);
      assertTrue(r.sets.get(2) == 3);

      try
        {
          Reps.fromString("foo");
          fail();
        }
      catch (Reps.RepsFormatException e)
        {
          // success
        }

      try
        {
          Reps.fromString("foo,bar");
          fail();
        }
      catch (Reps.RepsFormatException e)
        {
          // success
        }

      try
        {
          Reps.fromString(",,");
          fail();
        }
      catch (Reps.RepsFormatException e)
        {
          // success
        }

      try
        {
          Reps.fromString("foo,,bar");
          fail();
        }
      catch (Reps.RepsFormatException e)
        {
          // success
        }
    }

  public void testEmpty () throws Exception
    {
      Reps r1 = Reps.fromString("1,2,3");
      Reps r2 = r1.empty();

      assertTrue(r1.sets.size() == 3);
      assertTrue(r1.sets.get(0) == 1);
      assertTrue(r1.sets.get(1) == 2);
      assertTrue(r1.sets.get(2) == 3);

      assertTrue(r2.sets.size() == 3);
      assertTrue(r2.sets.get(0) == 0);
      assertTrue(r2.sets.get(1) == 0);
      assertTrue(r2.sets.get(2) == 0);
    }

  public void testIncrement () throws Exception
    {
      Reps r = new Reps();
      r.increment(5, new Reps(), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 0);

      r = Reps.fromString("1");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("1");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("1");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("1");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("2");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("2");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("2");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("2");
      r.increment(1, Reps.fromString("2"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 2);

      r = Reps.fromString("2");
      r.increment(5, Reps.fromString("3"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 3);

      r = Reps.fromString("2");
      r.increment(5, Reps.fromString("3"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 3);

      r = Reps.fromString("2");
      r.increment(5, Reps.fromString("3"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 3);

      r = Reps.fromString("2");
      r.increment(5, Reps.fromString("3"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 1);
      assertTrue(r.sets.get(0) == 3);

      r = Reps.fromString("1,1,1");
      r.increment(5, Reps.fromString("4,4,4"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 3);
      assertTrue(r.sets.get(0) == 3);
      assertTrue(r.sets.get(1) == 3);
      assertTrue(r.sets.get(2) == 2);

      r = Reps.fromString("1,1,1");
      r.increment(5, Reps.fromString("4,4,4"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.balanced);
      assertTrue(r.sets.size() == 3);
      assertTrue(r.sets.get(0) == 2);
      assertTrue(r.sets.get(1) == 3);
      assertTrue(r.sets.get(2) == 3);

      r = Reps.fromString("1,1,1");
      r.increment(5, Reps.fromString("4,4,4"), Reps.IncrementDirection.front_to_back, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 3);
      assertTrue(r.sets.get(0) == 4);
      assertTrue(r.sets.get(1) == 3);
      assertTrue(r.sets.get(2) == 1);

      r = Reps.fromString("1,1,1");
      r.increment(5, Reps.fromString("4,4,4"), Reps.IncrementDirection.back_to_front, Reps.IncrementStyle.fill_sets);
      assertTrue(r.sets.size() == 3);
      assertTrue(r.sets.get(0) == 1);
      assertTrue(r.sets.get(1) == 3);
      assertTrue(r.sets.get(2) == 4);
    }

  public void testToString () throws Exception
    {
      assertTrue(new Reps().toString().equals(""));

      assertTrue(Reps.fromString("").toString().equals(""));
      assertTrue(Reps.fromString("1").toString().equals("1"));
      assertTrue(Reps.fromString("1,1").toString().equals("1,1"));
      assertTrue(Reps.fromString("1,2,3").toString().equals("1,2,3"));

      assertTrue(Reps.toString(Reps.fromString("")).equals(""));
      assertTrue(Reps.toString(Reps.fromString("1")).equals("1"));
      assertTrue(Reps.toString(Reps.fromString("1,1")).equals("1,1"));
      assertTrue(Reps.toString(Reps.fromString("1,2,3")).equals("1,2,3"));
    }

  public void testCompareTo () throws Exception
    {
      assertTrue(new Reps().compareTo(new Reps()) == 0);

      assertTrue(Reps.fromString("1").compareTo(Reps.fromString("0")) > 0);
      assertTrue(Reps.fromString("1").compareTo(Reps.fromString("1")) == 0);
      assertTrue(Reps.fromString("0").compareTo(Reps.fromString("1")) < 0);

      assertTrue(Reps.fromString("2,2,2").compareTo(Reps.fromString("2,2,2")) == 0);
      assertTrue(Reps.fromString("2,2,2").compareTo(Reps.fromString("2,2,3")) < 0);
      assertTrue(Reps.fromString("2,2,2").compareTo(Reps.fromString("2,3,2")) < 0);
      assertTrue(Reps.fromString("2,2,2").compareTo(Reps.fromString("3,2,2")) < 0);
      assertTrue(Reps.fromString("3,2,2").compareTo(Reps.fromString("2,2,2")) > 0);
      assertTrue(Reps.fromString("2,3,2").compareTo(Reps.fromString("2,2,2")) > 0);
      assertTrue(Reps.fromString("2,2,3").compareTo(Reps.fromString("2,2,2")) > 0);

      assertTrue(Reps.fromString("2,3,2").compareTo(Reps.fromString("2,2,9")) > 0);

      try
        {
          new Reps().compareTo(Reps.fromString("1"));
          fail();
        }
      catch (Reps.RepsMismatchException e)
        {
          // success
        }

      try
        {
          Reps.fromString("1").compareTo(new Reps());
          fail();
        }
      catch (Reps.RepsMismatchException e)
        {
          // success
        }

      try
        {
          Reps.fromString("1").compareTo(Reps.fromString("1,2"));
          fail();
        }
      catch (Reps.RepsMismatchException e)
        {
          // success
        }

      try
        {
          Reps.fromString("1,2").compareTo(Reps.fromString("1"));
          fail();
        }
      catch (Reps.RepsMismatchException e)
        {
          // success
        }
    }

}
