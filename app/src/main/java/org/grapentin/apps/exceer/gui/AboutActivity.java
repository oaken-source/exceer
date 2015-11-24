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

package org.grapentin.apps.exceer.gui;

import android.os.Bundle;
import android.widget.TextView;

import org.grapentin.apps.exceer.BuildConfig;
import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.ServiceBoundActivity;

public class AboutActivity extends ServiceBoundActivity
{

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);

      TextView titleLabel = (TextView) findViewById(R.id.AboutActivityTitleLabel);
      titleLabel.setText(String.format("%1$s-%2$s", getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }

  @Override
  protected int getContentView()
    {
      return R.layout.activity_about;
    }

}
