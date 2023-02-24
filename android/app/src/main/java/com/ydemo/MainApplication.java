package com.ydemo;

import android.app.Application;
import android.content.Context;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import android.util.Log;
import com.ydemo.MorefunReactNativePackage;
import com.morefun.yapi.engine.DeviceServiceEngine;
import android.content.ServiceConnection;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.ydemo.DeviceHelper;

public class MainApplication extends Application implements ReactApplication {
  private final String TAG = "MainApplication";
  private final String SERVICE_ACTION = "com.morefun.ysdk.service";
  private final String SERVICE_PACKAGE = "com.morefun.ysdk";
  private DeviceServiceEngine deviceServiceEngine;

  private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
          packages.add(new MorefunReactNativePackage());
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // packages.add(new MyReactNativePackage());
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
      };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
    bindDeviceService();
  }

  /**
   * Loads Flipper in React Native templates. Call this in the onCreate method with something like
   * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
   *
   * @param context
   * @param reactInstanceManager
   */
  private static void initializeFlipper(
      Context context, ReactInstanceManager reactInstanceManager) {
    if (BuildConfig.DEBUG) {
      try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
        Class<?> aClass = Class.forName("com.ydemo.ReactNativeFlipper");
        aClass
            .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
            .invoke(null, context, reactInstanceManager);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  public DeviceServiceEngine getDeviceService() {
    return deviceServiceEngine;
}

public void bindDeviceService() {
    if (null != deviceServiceEngine) {
        return;
    }

    Intent intent = new Intent();
    intent.setAction(SERVICE_ACTION);
    intent.setPackage(SERVICE_PACKAGE);

    bindService(intent, connection, Context.BIND_AUTO_CREATE);
}

private ServiceConnection connection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
        deviceServiceEngine = null;
        Log.e(TAG, "======onServiceDisconnected======");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        deviceServiceEngine = DeviceServiceEngine.Stub.asInterface(service);
        Log.d(TAG, "======onServiceConnected======");

        try {
            DeviceHelper.reset();
            DeviceHelper.initDevices(MainApplication.this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        linkToDeath(service);
    }

    private void linkToDeath(IBinder service) {
        try {
            service.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Log.d(TAG, "======binderDied======");
                    deviceServiceEngine = null;
                    bindDeviceService();
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
  };
}
