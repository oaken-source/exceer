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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.grapentin.apps.exceer.R;
import org.grapentin.apps.exceer.gui.base.CustomBaseActivity;
import org.grapentin.apps.exceer.gui.widgets.TimeSinceLastSessionWidget;
import org.grapentin.apps.exceer.models.Session;

public class MainActivity extends CustomBaseActivity
{

  private ViewPager viewPager;

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      viewPager = (ViewPager) findViewById(R.id.MainActivityViewPager);
      viewPager.setAdapter(new ViewPagerAdapter());
    }

  @Override
  protected void onResume ()
    {
      super.onResume();

      TimeSinceLastSessionWidget lastSession = (TimeSinceLastSessionWidget) findViewById(R.id.MainActivityLastSessionLabel);
      lastSession.setSession(Session.getLast());

      TextView numSession = (TextView) findViewById(R.id.MainActivityNumSessionLabel);
      numSession.setText(String.format("%1$d", Session.count()));
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
        case R.id.action_about:
          Intent aboutIntent = new Intent(this, AboutActivity.class);
          startActivity(aboutIntent);
          break;
        }

      return super.onOptionsItemSelected(item);
    }

  public void onTrainButtonClicked (View view)
    {
      Intent intent = new Intent(this, TrainingActivity.class);
      startActivity(intent);
    }

  private class ViewPagerAdapter extends PagerAdapter
  {
    @Override
    public int getCount ()
      {
        return viewPager.getChildCount();
      }

    @Override
    public Object instantiateItem (ViewGroup container, int position)
      {
        return viewPager.getChildAt(position);
      }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object)
      {
        // nothing here
      }

    @Override
    public boolean isViewFromObject (View view, Object object)
      {
        return view == object;
      }
  }

}
