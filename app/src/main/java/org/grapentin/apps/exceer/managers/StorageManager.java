/*
 * comment
 */

package org.grapentin.apps.exceer.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.grapentin.apps.exceer.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StorageManager
{

  private static StorageManager instance = null;

  private SharedPreferences sharedPreferences;

  private StorageManager ()
    {
      sharedPreferences = ContextManager.get().getSharedPreferences(ContextManager.get().getString(R.string.SharedPreferences), Context.MODE_PRIVATE);
    }

  private static StorageManager getInstance ()
    {
      if (instance == null)
        instance = new StorageManager();
      return instance;
    }

  public static void init ()
    {
      getInstance();
    }

  public static long getLong (int resource)
    {
      return getLong(resource, 0);
    }

  public static long getLong (int resource, long def)
    {
      return getInstance().sharedPreferences.getLong(ContextManager.get().getString(resource), def);
    }

  public static void putLong (int resource, long value)
    {
      SharedPreferences.Editor editor = getInstance().sharedPreferences.edit();
      editor.putLong(ContextManager.get().getString(resource), value);
      editor.commit();
    }

  public static Serializable getSerializable (int resource)
    {
      return getSerializable(resource, null);
    }

  public static Serializable getSerializable (int resource, Serializable def)
    {
      String encoded = getInstance().sharedPreferences.getString(ContextManager.get().getString(resource), null);
      if (encoded == null)
        return def;

      byte[] bytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
      Serializable object = null;
      try
        {
          ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
          object = (Serializable)objectInputStream.readObject();
        }
      catch (Exception e)
        {
          // ignore
        }
      return object;
    }

  public static void putSerializable (int resource, Serializable value)
    {
      String encoded = null;
      try
        {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
          objectOutputStream.writeObject(value);
          objectOutputStream.close();
          encoded = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        }
      catch (IOException e)
        {
          // ignore
        }

      SharedPreferences.Editor editor = getInstance().sharedPreferences.edit();
      editor.putString(ContextManager.get().getString(resource), encoded);
      editor.commit();
    }

}
