package org.grapentin.apps.exceer;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class AboutActivity extends Activity
{

  @Override
  protected void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_about);

      TextView iconCopyrightLabel = (TextView)findViewById(R.id.AboutActivityIconCopyrightLabel);
      iconCopyrightLabel.setMovementMethod((LinkMovementMethod.getInstance()));

      TextView copyrightLabel = (TextView)findViewById(R.id.AboutActivityCopyrightLabel);
      copyrightLabel.setMovementMethod((LinkMovementMethod.getInstance()));
    }

}
