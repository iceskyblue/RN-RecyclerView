package com.example.yf.rnlist;

import android.content.Context;
import android.view.View;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yf on 2016/6/1.
 */
public class RnListPackage implements ReactPackage {

    private RowSelectListener mRowSelectLisener;
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    public void setRowSelectListener(RowSelectListener listener) {
        this.mRowSelectLisener = listener;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
                new RnRecyclerViewManager()
        );
    }

    public interface RowSelectListener {
        public void rowSelected(int rowId);
    }
}
