package com.example.yf.rnlist;

import android.view.View;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nullable;

/**
 * Created by yf on 2016/6/15.
 */
public class RnRecyclerViewManager extends
        ViewGroupManager<RnRecyclerView> {

    public static final String COMPNENT_NAME = "RnRecyclerView";


    @Override
    public String getName() {
        return COMPNENT_NAME;
    }

    @Override
    protected RnRecyclerView createViewInstance(ThemedReactContext reactContext) {
        RnRecyclerView view = new RnRecyclerView(reactContext);
        return view;
    }

    @Override
    public void onDropViewInstance(RnRecyclerView view) {
        super.onDropViewInstance(view);
    }

    //index as the view type
    @Override
    public void addView(RnRecyclerView parent, View child, int index) {

        parent.addViewType(child, index);
    }


    @Override
    public View getChildAt(RnRecyclerView parent, int index) {
        return parent.getChildAt(index);
    }

    @Override
    public void removeViewAt(RnRecyclerView parent, int index) {
        parent.removeViewAt(index);
    }



    @ReactProp(name="emptyViewHeight")
    public  void setEmptyViewHeight(RnRecyclerView parent, int height){
        parent.setEmptyViewHeight(height);
    }

    @ReactProp(name="viewTypesMap")
    public void setViewTypesMap(RnRecyclerView parent, ReadableMap map){
        parent.setTypesMap(map);
    }

    @ReactProp(name="itemClickable")
    public void setItemClickable(RnRecyclerView parent, boolean clickable){
        parent.setItemClickable(clickable);
    }

    @Override
    public void receiveCommand(RnRecyclerView root, int commandId, @Nullable ReadableArray args) {
        if(commandId == 1 && args.size() > 0){
            //scroll event
            root.scrollPosition(args.getInt(0));
        }else if(commandId  == 2){
            //data change event
            root.setData(args);
        }
    }
}
