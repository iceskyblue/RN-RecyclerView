package com.example.yf.rnlist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.touch.OnInterceptTouchEventListener;
import com.facebook.react.uimanager.MeasureSpecAssertions;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIImplementation;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.image.ReactImageManager;
import com.facebook.react.views.image.ReactImageView;
import com.facebook.react.views.text.ReactTextView;
import com.facebook.react.views.view.ReactViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by yf on 2016/6/15.
 */
public class RnRecyclerView extends RecyclerView {

    private Hashtable<Integer, Vector<View>> mTypeOfViews;
    private JSONArray mData;
    private int mEmptyViewHeight = 20;
    private RnListAdapter mAdapter;
    private SparseIntArray mViewTypeMap;
    private boolean mItemClickable = false;
    private RnRecyclerEventHelper mEventHelper;
    private ThemedReactContext mRnContext;

    public RnRecyclerView(ThemedReactContext context) {
        super(context);
        init(context);

    }

    public RnRecyclerView(ThemedReactContext context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setEmptyViewHeight(int height){
        this.mEmptyViewHeight = height;
    }

    public RnRecyclerView(ThemedReactContext context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(ThemedReactContext ctx){
        this.mRnContext = ctx;
        this.mEventHelper = new RnRecyclerEventHelper(ctx);
        this.mTypeOfViews = new Hashtable<Integer, Vector<View>>();
        this.mData = new JSONArray();
        this.mViewTypeMap = new SparseIntArray();
        LinearLayoutManager llm = new LinearLayoutManager(ctx);
        llm.setRecycleChildrenOnDetach(true);
        setLayoutManager(llm);
        this.setHasFixedSize(true);
        this.mAdapter = new RnListAdapter();
        this.setItemViewCacheSize(0);
        this.getRecycledViewPool().setMaxRecycledViews(0, 0);

        setAdapter(this.mAdapter);

    }

    public synchronized void addViewType(View v, int index){
        if(null != v.getParent()){
            return;
        }
        int type = mViewTypeMap.get(index, 0);
        Vector<View> typeViews = this.mTypeOfViews.get(type);
        if(null == typeViews){
            typeViews = new Vector<>();
            this.mTypeOfViews.put(type, typeViews);
        }

        if(!typeViews.contains(v)){
            typeViews.add(v);
        }
    }

    public synchronized void recyclerViewType(View v, int type){
        Vector<View> typeViews = this.mTypeOfViews.get(type);
        if(null != typeViews && (!typeViews.contains(v)))
            typeViews.add(v);
    }

    private ReactViewGroup cloneView(ReactViewGroup view, ThemedReactContext ctx){
        ReactViewGroup root = new ReactViewGroup(ctx);
        root.measure(MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(),MeasureSpec.EXACTLY));
        root.setBackgroundColor(view.getBackgroundColor());
        int childCount = view.getChildCount();
        for(int i=0 ; i<childCount; i++){
            View v = view.getChildAt(i);
            if(v instanceof ReactViewGroup){
                ReactViewGroup child = (ReactViewGroup)v;
                ReactViewGroup rvg = cloneView(child, mRnContext);
                rvg.setId(child.getId());
                root.addView(rvg);
                rvg.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            }
            if(v instanceof ReactTextView){
                ReactTextView child = (ReactTextView)v;
                ReactTextView copyChild = new ReactTextView(ctx);
                copyChild.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(),MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),MeasureSpec.EXACTLY));
                copyChild.setTextSize(child.getTextSize()/mRnContext.getBaseContext().getResources().getDisplayMetrics().density);
                copyChild.setTextColor(child.getTextColors());
                copyChild.setGravity(child.getGravity());
                copyChild.setId(child.getId());
                copyChild.setTag(child.getTag());
                root.addView(copyChild);
                copyChild.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                copyChild.setText(child.getText());
            }

            if(v instanceof ReactImageView){
                ReactImageView child = (ReactImageView)v;
                UIImplementation uii = ctx.getNativeModule(UIManagerModule.class).getUIImplementation();
                ReactImageManager rim = null;
                try {
                    Method resolveViewManager = UIImplementation.class.getDeclaredMethod("resolveViewManager", String.class);
                    resolveViewManager.setAccessible(true);
                    rim = (ReactImageManager)resolveViewManager.invoke(uii, "RCTImageView");
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                ReactImageView copyChild = rim.createViewInstance(ctx);
                copyChild.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(),MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),MeasureSpec.EXACTLY));
                copyChild.setId(child.getId());
                copyChild.setTag(child.getTag());

                root.addView(copyChild);
                copyChild.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                copyChild.setScaleType(child.getScaleType());
                try {
                    Field scaleType = child.getClass().getDeclaredField("mScaleType");
                    scaleType.setAccessible(true);
                    copyChild.setScaleType((ScalingUtils.ScaleType)scaleType.get(child));
                    Field imgSource = child.getClass().getDeclaredField("mSources");
                    imgSource.setAccessible(true);
                    Map<String, Double> source = (Map)imgSource.get(child);
                    Map<String, Double> copySource = new HashMap<>();
                    Set<Map.Entry<String, Double>> items = source.entrySet();
                    Iterator<Map.Entry<String, Double>> iter = items.iterator();
                    String imageName = null;
                    while(iter.hasNext()){
                        Map.Entry<String, Double> entry = iter.next();
                        if(null == imageName){
                            imageName = entry.getKey();
                        }
                        copySource.put(entry.getKey(), entry.getValue());
                    }
                    Field newSource = copyChild.getClass().getDeclaredField("mSources");
                    newSource.setAccessible(true);
                    newSource.set(copyChild,copySource);
                    Field dirty = copyChild.getClass().getDeclaredField("mIsDirty");
                    dirty.setAccessible(true);
                    dirty.setBoolean(copyChild, true);
                    if(null != imageName && copyDrawable(imageName) >= 0){
                        copyChild.setScaleType(ImageView.ScaleType.FIT_XY);
                        copyChild.setImageResource(copyDrawable(imageName));

                    }else{
                        copyChild.maybeUpdateView();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return root;
    }

    private int  copyDrawable(String name){
        int resId = getResources().getIdentifier(name, "drawable", getContext().getApplicationContext().getPackageName());
        return resId;
    }

    public void setItemClickable(boolean clickable){
        this.mItemClickable = clickable;
    }

    public boolean setData(String jsonData){
        this.mData = new JSONArray();

        boolean res = this.addData(jsonData);
        this.scrollPosition(0);
        return res;
    }

    public void setData(ReadableArray data){
        if(null != data){
            this.setData(data.toString());
        }

    }


    public boolean addData(String jsonData){
        boolean res = false;
        try {
            JSONArray data = new JSONArray(jsonData);
            int length = null==data ? 0 : data.length();
            for(int i=0; i<length; i++){
                this.mData.put(data.get(i));
            }
            mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemRangeChanged(0, 0 == data.length() ? 0 : 1);
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public void scrollPosition(int pos){
        int position = pos - 1;
        if(position < 0){
            position = 0;
        }

        this.scrollToPosition(position);
        ((LinearLayoutManager)getLayoutManager()).scrollToPositionWithOffset(position, 0);
        mAdapter.notifyDataSetChanged();
        mAdapter.notifyItemRangeChanged(position, 0 == mData.length() ? 0 : 1 );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        MeasureSpecAssertions.assertExplicitMeasureSpec(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDetachedFromWindow() {
        if(null != mTypeOfViews){
            Enumeration<Vector<View>> em = mTypeOfViews.elements();
            while(em.hasMoreElements()){
                em.nextElement().clear();
            }

            mTypeOfViews.clear();
        }
        super.onDetachedFromWindow();
    }

    public void setTypesMap(ReadableMap map){
        ReadableMapKeySetIterator iter = map.keySetIterator();
        while(iter.hasNextKey()){
            String type = iter.nextKey();
            int typeInt = 0;
            try{
                typeInt = Integer.valueOf(type);
                this.getRecycledViewPool().setMaxRecycledViews(typeInt, 0);
            }catch (Exception e){
                e.printStackTrace();
            }
            ReadableArray indexs = map.getArray(type);
            for(int i=0; i<indexs.size(); i++){
                try{
                    this.mViewTypeMap.put(indexs.getInt(i), typeInt);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    class RnListAdapter extends Adapter<RnViewHolder> {


        @Override
        public RnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Vector<View> views = mTypeOfViews.get(viewType);
            View v = null;
            if(views.size() == 0){
                v = new EmptyView(getContext());
                v.setMinimumHeight(mEmptyViewHeight);
            }else{
                View child = null;
                if(views.size() > 1){
                    child = views.remove(0);
                }else{
                    child = cloneView((ReactViewGroup) views.get(0), mRnContext);
                }

                if(null == child.getLayoutParams()){
                    int measureH = child.getMeasuredHeight();
                    child.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, measureH));
                }
                v = child;
            }

            RnViewHolder holder = new RnViewHolder(v, viewType, mItemClickable);
            if(null != holder.clickListener){
                holder.clickListener.eventHelper = mEventHelper;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RnViewHolder holder, int position) {
            if(holder.itemView instanceof EmptyView){
                return;
            }

            if(null != holder.clickListener){
                holder.clickListener.position = position;
            }

            View v = holder.itemView;
            ReactViewGroup rvg = (ReactViewGroup)v;
            rvg.invalidate();
            try {
                JSONObject data = mData.getJSONObject(position);

                Iterator<String> iter = data.keys();
                while (iter.hasNext()){
                    String key = iter.next();
                    View child = v.findViewWithTag(key);
                    if(key.startsWith("__")){
                        child = v.findViewWithTag(key.substring(2));
                        if(null != child){
                            int isVisible = data.optInt(key, View.VISIBLE);
                            switch (isVisible){
                                case View.VISIBLE:
                                    child.setVisibility(View.VISIBLE);
                                    break;
                                case View.GONE:
                                    child.setVisibility(View.GONE);
                                    break;
                                case View.INVISIBLE:
                                    child.setVisibility(View.INVISIBLE);
                                    break;
                            }

                        }
                        continue;
                    }
                    if(child instanceof ReactTextView){
                        ReactTextView txt = ((ReactTextView)child);
                        String str = data.optString(key);
                        Spanned newStr = null;
                        if(str.indexOf("</font>") >= 0){
                            newStr = Html.fromHtml(str);
                        }else{
                            newStr = copySpanned(data.optString(key),(SpannedString)txt.getText());
                        }
                        txt.setText(newStr);

                        float w = txt.getPaint().measureText(newStr, 0, newStr.length());
                        updateRightBrotherLocation(child, (int)(w + 0.5f + child.getPaddingRight() + child.getPaddingLeft()));
                    }else if(child instanceof ReactImageView){
                        JavaOnlyArray source = new JavaOnlyArray();
                        JavaOnlyMap map = new JavaOnlyMap();
                        map.putString("uri", data.optString(key));
                        source.pushMap(map);
                        ((ReactImageView)child).setSource(source);
                        ((ReactImageView)child).maybeUpdateView();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private Spannable copySpanned(String dst, SpannedString src){
            SpannableStringBuilder ss = new SpannableStringBuilder(dst);
            Object[] spanneds = src.getSpans(0, src.length(), Object.class);
            for(Object obj : spanneds){
                ss.setSpan(obj, 0, dst.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            return ss;
        }

        private void updateRightBrotherLocation(View view, int newWidth){
            ViewParent parent = view.getParent();
            if(null == parent){
                return;
            }

            int offset = newWidth - view.getWidth();
            boolean findBrother = false;
            ViewGroup vg = ((ViewGroup)parent);
            View brother = null;
            for(int i=0; i<vg.getChildCount(); i++){
                View child = vg.getChildAt(i);
                if(findBrother && ((child.getTop()>=view.getTop()&&child.getTop()<view.getBottom()) ||
                child.getBottom()<=view.getBottom()&&child.getBottom()>view.getTop())){
                    brother = child;
                    break;
                }
                if(view == child){
                    findBrother = true;
                }
            }

            if(null != brother){
                brother.layout(brother.getLeft()+offset, brother.getTop(), brother.getRight()+offset, brother.getBottom());
                view.layout(view.getLeft(), view.getTop(), view.getLeft()+newWidth, view.getBottom());
            }else if(offset > 0){
                view.layout(view.getLeft(), view.getTop(), view.getLeft()+newWidth, view.getBottom());
            }

        }


        @Override
        public int getItemCount() {
            return null==mData? 0 : mData.length();
        }

        @Override
        public int getItemViewType(int position) {
            int type = 0;
            try {
                JSONObject itemData = mData.getJSONObject(position);
                type = itemData.optInt("viewType");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return type;
        }

        @Override
        public void onViewRecycled(RnViewHolder holder) {
            super.onViewRecycled(holder);
            if(holder.itemView instanceof  EmptyView){
                return;
            }

            ((ViewGroup) holder.itemView).removeView(holder.itemView);

            recyclerViewType(holder.itemView, holder.viewType);

        }
    }


    private static class RnViewHolder extends ViewHolder {
        public int viewType = -1;
        public boolean clickable;
        public OnItemTouchListener clickListener;
        public RnViewHolder(View itemView, int type, boolean clickable) {
            super(itemView);
            this.clickable = clickable;
            this.viewType = type;
            if(this.clickable && itemView instanceof ReactViewGroup){
                clickListener = new OnItemTouchListener();
                ((ReactViewGroup)itemView).setOnInterceptTouchEventListener(clickListener);
           }
        }
    }

    private static class EmptyView extends View{
        public EmptyView(Context context) {
            super(context);
        }
    }

    private static class OnItemTouchListener implements OnInterceptTouchEventListener {

        public int position = -1;
        public long pressDownTime;
        public RnRecyclerEventHelper eventHelper;
        @Override
        public boolean onInterceptTouchEvent(ViewGroup v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressDownTime = event.getEventTime();
                    break;
                case MotionEvent.ACTION_UP:
                    if(event.getEventTime() - pressDownTime <= ViewConfiguration.getTapTimeout()){
                        if(null != eventHelper){
                            eventHelper.sendItemClickEvent(position);
                        }
                    }
                    break;
            }
            return false;
        }
    }
}
