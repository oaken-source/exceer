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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.BaseActivity;
import org.grapentin.apps.exceer.gui.settings.MainSettingsActivity;

public class MainActivity extends BaseActivity
{

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
    }

  @Override
  public boolean onCreateOptionsMenu (Menu menu)
    {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
    }

  @Override
  public boolean onOptionsItemSelected (MenuItem item)
    {
      int id = item.getItemId();

      switch (id)
        {
        case R.id.action_settings:
          Intent settingsIntent = new Intent(this, MainSettingsActivity.class);
          startActivity(settingsIntent);
          break;
        case R.id.action_about:
          Intent aboutIntent = new Intent(this, AboutActivity.class);
          startActivity(aboutIntent);
          break;
        }

      return super.onOptionsItemSelected(item);
    }

  @Override
  public void onOptionsMenuClosed (Menu menu)
    {
      Log.d("Overflow", "closed :)");
    }

  public void onTrainButtonClicked (View view)
    {
      Intent intent = new Intent(this, TrainingActivity.class);
      startActivity(intent);
    }

}
