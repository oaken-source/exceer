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

public class DurationTest extends InstrumentationTestCase
{

  public void testEmptyConstructor () throws Exception
    {
      Duration d = new Duration();
      assertTrue(d.get() == 0);
    }

  public void testFromString () throws Exception
    {
      Duration d = Duration.fromString("0");
      assertTrue(d.get() == 0);

      d = Duration.fromString("2");
      assertTrue(d.get() == 2);

      d = Duration.fromString("2s");
      assertTrue(d.get() == 2 * 1000);

      d = Duration.fromString("2min");
      assertTrue(d.get() == 2 * 60 * 1000);

      d = Duration.fromString(" 2 min ");
      assertTrue(d.get() == 2 * 60 * 1000);

      try
        {
          Duration.fromString("");
          fail();
        }
      catch (Duration.DurationFormatException e)
        {
          // success
        }

      try
        {
          Duration.fromString("foo");
          fail();
        }
      catch (Duration.DurationFormatException e)
        {
          // success
        }

      try
        {
          Duration.fromString("2.5");
          fail();
        }
      catch (Duration.DurationFormatException e)
        {
          // success
        }
    }

  public void testIncrement () throws Exception
    {
      Duration d = new Duration();
      d.increment(new Duration());
      assertTrue(d.get() == 0);

      d.increment(Duration.fromString("5s"));
      assertTrue(d.get() == 5 * 1000);

      d.increment(Duration.fromString("5min"));
      assertTrue(d.get() == 5 * 1000 + 5 * 60 * 1000);
    }

  public void testToString () throws Exception
    {
      assertTrue(new Duration().toString().equals("0"));

      assertTrue(Duration.fromString("0").toString().equals("0"));
      assertTrue(Duration.fromString("2s").toString().equals("2s"));
      assertTrue(Duration.fromString("2min").toString().equals("2min"));
      assertTrue(Duration.fromString(" 2 min ").toString().equals("2min"));

      assertTrue(Duration.toString(Duration.fromString("0")).equals("0"));
      assertTrue(Duration.toString(Duration.fromString("2s")).equals("2s"));
      assertTrue(Duration.toString(Duration.fromString("2min")).equals("2min"));
      assertTrue(Duration.toString(Duration.fromString(" 2 min ")).equals("2min"));

      Duration d = Duration.fromString("5min");
      d.increment(Duration.fromString("5s"));
      assertTrue(d.toString().equals("305s"));
    }

  public void testCompareTo () throws Exception
    {
      assertTrue(new Duration().compareTo(new Duration()) == 0);

      assertTrue(Duration.fromString("1").compareTo(Duration.fromString("0")) > 0);
      assertTrue(Duration.fromString("1").compareTo(Duration.fromString("1")) == 0);
      assertTrue(Duration.fromString("0").compareTo(Duration.fromString("1")) < 0);

      assertTrue(Duration.fromString("2s").compareTo(Duration.fromString("1999")) > 0);
      assertTrue(Duration.fromString("2s").compareTo(Duration.fromString("2000")) == 0);
      assertTrue(Duration.fromString("2s").compareTo(Duration.fromString("2001")) < 0);

      assertTrue(Duration.fromString("2min").compareTo(Duration.fromString("119s")) > 0);
      assertTrue(Duration.fromString("2min").compareTo(Duration.fromString("120s")) == 0);
      assertTrue(Duration.fromString("2min").compareTo(Duration.fromString("121s")) < 0);

      assertTrue(Duration.fromString("2").compareTo(Duration.fromString("2s")) < 0);
      assertTrue(Duration.fromString("2s").compareTo(Duration.fromString("2min")) < 0);
    }

}
