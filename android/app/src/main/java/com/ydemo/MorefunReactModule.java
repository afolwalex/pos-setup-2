package com.ydemo;
import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.util.Log;
import android.os.Bundle;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import android.os.RemoteException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.reader.mag.MagCardInfoEntity;
import com.morefun.yapi.device.reader.mag.MagCardReader;
import com.morefun.yapi.device.reader.mag.OnSearchMagCardListener;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.MulPrintStrEntity;
import com.morefun.yapi.device.printer.OnPrintListener;
import java.util.List;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MorefunReactModule extends ReactContextBaseJavaModule {

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    public Context myContext;

    public MorefunReactModule(ReactApplicationContext reactContext) {

        super(reactContext);

        myContext = reactContext;
    }

    @Override
    public String getName() {
        return "MorefunReactModule";
    }

    @ReactMethod
    public void getDeviceInfo(Promise promise) {
        Log.d("TAG", "###getDeviceInfo>>>");
        try {
            Bundle devInfo = DeviceHelper.getDeviceService().getDevInfo();
            String vendor = devInfo.getString(DeviceInfoConstrants.COMMOM_VENDOR);
            String model = devInfo.getString(DeviceInfoConstrants.COMMOM_MODEL);
            String osVer = devInfo.getString(DeviceInfoConstrants.COMMOM_OS_VER);
            String sn = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
            String tusn = devInfo.getString(DeviceInfoConstrants.TID_SN);
            String versionCode = devInfo.getString(DeviceInfoConstrants.COMMON_SERVICE_VER);
            String hardware = devInfo.getString("hardware");
            
            JSONObject json = new JSONObject();
            json.put("vendor", vendor);
            json.put("model", model);
            json.put("osVer", osVer);
            json.put("sn", sn);
            json.put("tusn", tusn);
            json.put("versionCode", versionCode);
            json.put("hardware", hardware);
            promise.resolve(json.toString());
        } catch (RemoteException e) {
            promise.reject(e);
        } catch (JSONException e) {
            e.printStackTrace();
            promise.reject(e);
		}
    }

    @ReactMethod
    public void readMagCard(Promise promise) {
        Log.d("TAG", "###readMagCard>>>");
        try {
            final MagCardReader magCardReader = DeviceHelper.getMagCardReader();

            magCardReader.searchCard(new OnSearchMagCardListener.Stub() {
                @Override
                public void onSearchResult(int ret, MagCardInfoEntity magCardInfoEntity) throws RemoteException {
                    try {
                        if (ret == ServiceResult.Success) {
                            JSONObject json = new JSONObject();
                            json.put("cardNo", magCardInfoEntity.getCardNo());
                            json.put("track1", magCardInfoEntity.getTk1());
                            json.put("track2", magCardInfoEntity.getTk2());
                            json.put("track3", magCardInfoEntity.getTk3());
                            json.put("nServiceCode", magCardInfoEntity.getServiceCode());
                            promise.resolve(json.toString());
                        } else {
                            promise.reject(ret + "");
                        }
                    } catch(JSONException e) {
                        promise.reject(e);
                    }
                }
            }, 10, new Bundle());
        } catch (RemoteException e) {
            e.printStackTrace();
            promise.reject(e);
        } 
    }

    @ReactMethod
    public void print(ReadableArray printData, Promise promise) {
        Log.d("TAG", "###print>>>");
        try {
            List<MulPrintStrEntity> list = new ArrayList<>();
            int fontSize = FontFamily.MIDDLE;
            Bundle config = new Bundle();

            if (printData != null && printData.size() > 0) {
                for (int i = 0; i < printData.size(); i++) {
                    list.add(new MulPrintStrEntity(printData.getString(i), fontSize));
                }
            }
            
            DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int result) throws RemoteException {

                }
            }, config);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
