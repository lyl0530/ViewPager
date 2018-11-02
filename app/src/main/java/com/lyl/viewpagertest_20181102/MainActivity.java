package com.lyl.viewpagertest_20181102;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "lyl123";

    private final int MSG_UPDATE_IMG = 0;
    private final int DELAY_TIME = 2000;

    private ViewPager vp_image;
    private LinearLayout ll_dot;
    private TextView tv_description;

    private int prePostion = 0;

    private ArrayList<ImageView> imgs;
    private int[] icons = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
    private String[] description = {"picture1", "picture2", "picture3", "picture4", "picture5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp_image = (ViewPager) findViewById(R.id.vp_image);
        ll_dot = (LinearLayout) findViewById(R.id.ll_dot);
        tv_description = (TextView) findViewById(R.id.tv_description);

        //init image
        imgs = new ArrayList<>();
        for (int i = 0; i < icons.length; i++){
            ImageView img = new ImageView(this);
            img.setBackgroundResource(icons[i]);
            imgs.add(img);

            ImageView dot = new ImageView(this);
            dot.setBackgroundResource(R.drawable.dot);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
            if (0 == i){
                dot.setEnabled(true);
            } else {
                dot.setEnabled(false);
                lp.leftMargin = 12;
            }
            dot.setLayoutParams(lp);
            ll_dot.addView(dot);
        }

        //set adapter
        vp_image.setAdapter(new MyPagerAdapter());

        //set listener
        vp_image.addOnPageChangeListener(new MyOnPageChangeListener());

        //Set the currently selected page: must after set adapter, if not, it's value is 0
        int item = Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2)%(icons.length);
        Log.e(TAG, "onCreate: item = " + item);
        vp_image.setCurrentItem(item);//从第０个开始

        //定时更新图片
        handler.sendEmptyMessageDelayed(MSG_UPDATE_IMG, DELAY_TIME);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            handler.removeCallbacksAndMessages(null);

            int item = vp_image.getCurrentItem();
            vp_image.setCurrentItem(item+1);
            handler.sendEmptyMessageDelayed(MSG_UPDATE_IMG, DELAY_TIME);

        }
    };

    private boolean isDragging = false;
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /*
        onPageScrolled
        当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
        position :当前页面，即你点击滑动的页面（从A滑B，则是A页面的position。 ）
        positionOffset:当前页面偏移的百分比
        positionOffsetPixels:当前页面偏移的像素位置
        */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        /*
        onPageSelected
        此方法是页面跳转完后得到调用，从A滑动到B，position就是B的位置，即滑动后的位置
        */
        @Override
        public void onPageSelected(int position) {
            int curPosition = position%(icons.length);
            Log.e(TAG, "onPageScrolled: pos = " + curPosition);
            tv_description.setText(description[curPosition]);

            ll_dot.getChildAt(prePostion).setEnabled(false);
            ll_dot.getChildAt(curPosition).setEnabled(true);

            prePostion = curPosition;
        }
        /*
        onPageScrollStateChanged
        此方法是在状态改变的时候调用，其中state这个参数有三种状态：
            SCROLL_STATE_DRAGGING（1）表示用户手指“按在屏幕上并且开始拖动”的状态
                （手指按下但是还没有拖动的时候还不是这个状态，只有按下并且手指开始拖动后log才打出。）
            SCROLL_STATE_IDLE（0）滑动动画做完的状态。
            SCROLL_STATE_SETTLING（2）在“手指离开屏幕”的状态。
        一个完整的滑动动作，三种状态的出发顺序为（1，2，0）
        */
        @Override
        public void onPageScrollStateChanged(int state) {
            if (ViewPager.SCROLL_STATE_DRAGGING == state){//用户拖拽时，停止滑动，停止当前定时器
                //Log.e(TAG, "SCROLL_STATE_DRAGGING");
                handler.removeCallbacksAndMessages(null);
                isDragging = true;
            } else if (ViewPager.SCROLL_STATE_SETTLING == state) {
                //Log.e(TAG, "SCROLL_STATE_SETTLING");
            } else if (ViewPager.SCROLL_STATE_IDLE == state && isDragging){//滑动完成，则开始自己滑动
                //Log.e(TAG, "SCROLL_STATE_IDLE");
                handler.sendEmptyMessageDelayed(MSG_UPDATE_IMG, DELAY_TIME);
            }
        }
    }

    /* When you implement a PagerAdapter, you must override the following methods at minimum:
        <li>{@link #instantiateItem(ViewGroup, int)}</li>
        <li>{@link #destroyItem(ViewGroup, int, Object)}</li>
        <li>{@link #getCount()}</li>
        <li>{@link #isViewFromObject(View, Object)}</li>
    */

    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //Log.e(TAG, "position = " + position);
            int curPosition = position%(icons.length);
            //Log.e(TAG, "curPos = " + curPosition + ", pos = " + position);
            //获取当前位置的ImageView
            final ImageView imgView = imgs.get(curPosition);
            //把ImageView添加到ViewPager中
            container.addView(imgView);

            //对ImageView添加事件监听：用户按下或拖拽的时候停止滑动
            imgView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN://用户按下的时候停止滑动
                            handler.removeCallbacksAndMessages(null);
                            Log.e(TAG, "onTouch: down");
                            break;
                        case MotionEvent.ACTION_UP://用户释放后开始滑动
                            Log.e(TAG, "onTouch: up");
                            handler.removeCallbacksAndMessages(null);
                            handler.sendEmptyMessageDelayed(0, 2000);
                            break;
                    }
                    return false;//true:此处若返回true，则imgView的点击事件会被拦截，点击无反应。
                }
            });

            //对ImageView添加点击监听
            imgView.setTag(position);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onClick: " + imgView.getTag());
                }
            });
            return imgView;
        }
    }

}
