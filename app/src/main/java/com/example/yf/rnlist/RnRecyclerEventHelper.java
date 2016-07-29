package com.example.yf.rnlist;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ThemedReactContext;

/**
 * Created by yf on 2016/6/17.
 */
public class RnRecyclerEventHelper {

    private ThemedReactContext mCtx;

    public RnRecyclerEventHelper(ThemedReactContext ctx){
        this.mCtx = ctx;
    }

    public  void sendNeedViewEvent(int type){
        WritableMap params = Arguments.createMap();
        params.putInt("viewType", type);
        mCtx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("need_views", params);
    }

    public  void sendReachEndEvent(){
        mCtx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("end_reached", null);
    }

    public void sendItemClickEvent(int position){
        WritableMap params = Arguments.createMap();
        params.putInt("position", position);
        mCtx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("item_clicked", params);
    }
}
