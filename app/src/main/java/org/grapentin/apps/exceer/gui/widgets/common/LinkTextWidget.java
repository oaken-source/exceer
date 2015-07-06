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

package org.grapentin.apps.exceer.gui.widgets.common;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

public class LinkTextWidget extends TextView
{

  public LinkTextWidget (Context context)
    {
      super(context);
      setMovementMethod(LinkMovementMethod.getInstance());
    }

  public LinkTextWidget (Context context, AttributeSet attrs)
    {
      super(context, attrs);
      setMovementMethod(LinkMovementMethod.getInstance());
    }

  public LinkTextWidget (Context context, AttributeSet attrs, int defStyleAttr)
    {
      super(context, attrs, defStyleAttr);
      setMovementMethod(LinkMovementMethod.getInstance());
    }

}
