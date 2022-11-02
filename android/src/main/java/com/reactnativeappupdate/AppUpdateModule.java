package com.reactnativeappupdate;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

@ReactModule(name = AppUpdateModule.NAME)
public class AppUpdateModule extends ReactContextBaseJavaModule {
    public static final String NAME = "AppUpdate";

    public AppUpdateModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

  public static final String ERROR_GET_APP_INFO_FAILED = "Unable to get app info.";
  /** Update result: update ok. */
  public static final int UPDATE_OK = 0;
  /** Update result: update canceled. */
  public static final int UPDATE_CANCELED = 1;
  /** Update result: update failed. */
  public static final int UPDATE_FAILED = 2;
  /** Update result: update not available. */
  public static final int UPDATE_NOT_AVAILABLE = 3;
  /** Update result: update not allowed. */
  public static final int UPDATE_NOT_ALLOWED = 4;
  /** Update result: update info missing. */
  public static final int UPDATE_INFO_MISSING = 5;
  /** Request code for immediate update */
  protected static final int REQUEST_IMMEDIATE_UPDATE = 10;
  /** Request code for flexible update */
  protected static final int REQUEST_FLEXIBLE_UPDATE = 11;
  private AppUpdateManager appUpdateManager;
  private AppUpdateInfo appUpdateInfo;
  private InstallStateUpdatedListener listener;


  public void  initialize() {
    this.appUpdateManager = AppUpdateManagerFactory.create(this.getReactApplicationContext());
  }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }



  @ReactMethod
  public void openAppStore(Promise promise) {
    String packageName = this.getReactApplicationContext().getPackageName();
    Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    try {
      getCurrentActivity().startActivity(launchIntent);
    } catch (ActivityNotFoundException ex) {
      launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
      getCurrentActivity().startActivity(launchIntent);
    }
    promise.resolve("");
  }


  @ReactMethod
  public void getAppUpdateInfo(Promise promise) {
    Task<AppUpdateInfo> appUpdateInfoTask = this.appUpdateManager.getAppUpdateInfo();
    appUpdateInfoTask.addOnSuccessListener(
      appUpdateInfo -> {
        this.appUpdateInfo = appUpdateInfo;
        PackageInfo pInfo;
        try {
          pInfo = this.getPackageInfo();
        } catch (PackageManager.NameNotFoundException e) {
          promise.reject(ERROR_GET_APP_INFO_FAILED);
          return;
        }

        WritableMap ret = Arguments.createMap();
        ret.putString("currentVersion", String.valueOf(pInfo.versionCode));
        ret.putString("availableVersion", String.valueOf(appUpdateInfo.availableVersionCode()));
        ret.putInt("updateAvailability", appUpdateInfo.updateAvailability());
        ret.putInt("updatePriority", appUpdateInfo.updatePriority());
        ret.putBoolean("immediateUpdateAllowed", appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE));
        ret.putBoolean("flexibleUpdateAllowed", appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
        Integer clientVersionStalenessDays = appUpdateInfo.clientVersionStalenessDays();
        if (clientVersionStalenessDays != null) {
          ret.putInt("clientVersionStalenessDays", clientVersionStalenessDays);
        }
        ret.putInt("installStatus", appUpdateInfo.installStatus());

        promise.resolve(ret);
      }
    );
    appUpdateInfoTask.addOnFailureListener(
      failure -> {
        String message = failure.getMessage();
        promise.reject(message);
      }
    );
  }

  private PackageInfo getPackageInfo() throws PackageManager.NameNotFoundException {
    String packageName = getReactApplicationContext().getPackageName();
    return getReactApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
  }
}
