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

package org.grapentin.apps.exceer.gui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.models.Session;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeSinceLastSessionWidget extends TextView
{

  private Session session = null;

  private PrettyTime formatter = new PrettyTime();

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
                  updateView();
                }
            });
          }
      }, 0, 1000);
    }

  private void updateView ()
    {
      if (session == null)
        setText(R.string.MainActivityLastSessionDateText);
      else
        setText(formatter.format(new Date(session.getDate())));
    }

  public void setSession (Session session)
    {
      this.session = session;
      updateView();
    }

}
