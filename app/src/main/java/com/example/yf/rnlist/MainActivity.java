package com.example.yf.rnlist;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

public class MainActivity extends AppCompatActivity  implements DefaultHardwareBackBtnHandler {

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    protected RnListPackage mPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReactRootView = new ReactRootView(this);
        initRn(getApplication());
        mReactRootView.startReactApplication(mReactInstanceManager, "test", null);
        RelativeLayout parent = (RelativeLayout)this.findViewById(R.id.root);
        parent.addView(mReactRootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }



    public void initRn(Application application){
        mPackage = new RnListPackage();
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(application)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .setUseDeveloperSupport(true)
                .addPackage(new MainReactPackage())
                .addPackage(mPackage)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        mReactInstanceManager.createReactContextInBackground();
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mReactRootView){
            mReactRootView.unmountReactApplication();
        }

        if (mReactInstanceManager != null) {
            mReactInstanceManager.detachRootView(mReactRootView);
            mReactInstanceManager.onHostDestroy();
            mReactInstanceManager.destroy();
        }
    }
}
