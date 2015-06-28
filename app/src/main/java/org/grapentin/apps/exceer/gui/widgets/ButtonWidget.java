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
import android.widget.Button;

import org.grapentin.apps.exceer.gui.widgets.interfaces.TextContainer;

public class ButtonWidget extends Button implements TextContainer
{

  public ButtonWidget (Context context)
    {
      super(context);
    }

  public ButtonWidget (Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

  public ButtonWidget (Context context, AttributeSet attrs, int defStyleAttr)
    {
      super(context, attrs, defStyleAttr);
    }

}
