/*
 * comment
 */

package org.grapentin.apps.exceer.managers;

import android.content.Context;

public class ContextManager
{

  private static ContextManager instance = null;
  private Context context = null;

  private ContextManager ()
    {
    }

  private static ContextManager getInstance ()
    {
      if (instance == null)
        instance = new ContextManager();
      return instance;
    }

  public static void init (Context context)
    {
      getInstance().context = context;
    }

  public static Context get ()
    {
      return getInstance().context;
    }

}
