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

package org.grapentin.apps.exceer.gui.widgets.main;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import org.grapentin.apps.exceer.models.Session;

import java.util.Timer;
import java.util.TimerTask;

public class TimeSinceLastSessionWidget extends TextView
{

  private Session session = null;

  public TimeSinceLastSessionWidget (Context context)
    {
      super(context);
      if (!isInEditMode())
        start();
    }

  public TimeSinceLastSessionWidget (Context context, AttributeSet attrs)
    {
      super(context, attrs);
      if (!isInEditMode())
        start();
    }

  public TimeSinceLastSessionWidget (Context context, AttributeSet attrs, int defStyleAttr)
    {
      super(context, attrs, defStyleAttr);
      if (!isInEditMode())
        start();
    }

  private void start ()
    {
      update();

      new Timer().scheduleAtFixedRate(new TimerTask()
      {
        @Override
        public void run ()
          {
            post(new Runnable()
            {
              @Override
              public void run ()
                {
                  update();
                }
            });
          }
      }, 0, 1000);
    }

  private void update ()
    {
      long last = (session == null ? System.currentTimeMillis() : session.getDate());

      String s = (String)DateUtils.getRelativeTimeSpanString(last, System.currentTimeMillis(), 1000, DateUtils.FORMAT_ABBREV_ALL);
      s = s.substring(0, s.length() - 4);
      setText(s);
    }

}
