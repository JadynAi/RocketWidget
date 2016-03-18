package com.jadyn.rocketdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by JadynAi on 2016/3/18.
 * 1，做一个服务，开启火箭就是开启服务。关闭火箭就是关闭服务；
 * 2，做一个自定义的火箭类，用于显示火箭；
 * 3，在自定义火箭内：
 * 一，获取windosManager窗口管理器
 * 二，火箭的ImageView会被addView到这个窗口中，但之前必须给火箭View设置一个参数WindowManager.LayoutParams
 * 三，这个参数要设置位置和长宽，以及类型和格式（透明或者半透明）显示优先级
 * 四，要让火箭动起来，就是添加onTouch监听，在MotionEvent。ACTION_MOVE中，给火箭的布局参数params
 * 的x和y坐标添加改变，并且要窗口使用updateViewLayout(rocketView,params);
 * 五，火箭到底部组件的位置满足一定的条件，而让底部组件改变背景。通过组件的getLocationOnScreen(new int[2]);方法
 * 得到此组件实时的在屏幕的位置，但请注意是组件左上角以屏幕左上角为原点的坐标值来判断。
 * 六，将火箭移动到中间位置，不能通过params.gravity来改变，只能通过改变params的x坐标来实现
 * 七，发射火箭，使用属性动画。addUpdateListener实时改变。
 * 八，加一个烟雾发生的Activity。无非就是在此Activity中设置两张图片，给这两张图片设置属性动画，
 * 按照顺序播放，添加监听事件，事件完成后把Activity  finish掉
 * 九，注意在服务中启动活动，要给intent添加flag，setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 */
public class Rockets implements View.OnTouchListener {
    private Context rContext;
    private final WindowManager wMn;
    private final WindowManager.LayoutParams params;
    private ImageView rocketView;
    private float downX;
    private float downY;
    private ImageView bottomView;
    private WindowManager.LayoutParams bottomParams;
    private float moveX;
    private float moveY;

    public Rockets(Context context) {
        rContext = context;

        //得到一个管理打开的窗口程序
        wMn = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //像素格式，半透明（TRANSLUCENT）、透明的（TRANSPARENT）
        params.format = PixelFormat.TRANSLUCENT;
        //设置这个窗口的一些特性，不可点击、一直保持开启或者无焦点
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;


    }

    public void show() {
        rocketView = new ImageView(rContext);
        rocketView.setImageResource(R.mipmap.desktop_rocket_launch_1);

        rocketView.setBackgroundResource(R.drawable.rocket_bg);
        AnimationDrawable animation = (AnimationDrawable) rocketView.getBackground();
        animation.start();

        rocketView.setOnTouchListener(this);

        //将这个组件添加到窗口中
        wMn.addView(rocketView, params);
    }

    public void hide() {
        if (rocketView != null) {
            if (rocketView.getParent() != null) {
                wMn.removeView(rocketView);
            }
            rocketView = null;
        }
    }

    /**
     * 初始化火箭底部的参数
     */
    public void initBottomParams() {
        bottomParams = new WindowManager.LayoutParams();
        bottomParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        bottomParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        bottomParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //像素格式，半透明（TRANSLUCENT）、透明的（TRANSPARENT）
        bottomParams.format = PixelFormat.TRANSLUCENT;
        //设置这个窗口的一些特性，不可点击、一直保持开启或者无焦点
        bottomParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        bottomParams.type = WindowManager.LayoutParams.TYPE_TOAST;
    }


    /**
     * 展示火箭底部
     */
    public void showBottom() {
        bottomView = new ImageView(rContext);
        bottomView.setBackgroundResource(R.drawable.bottom_bg);

        initBottomParams();

        //帧动画播放
        AnimationDrawable drawable = (AnimationDrawable) bottomView.getBackground();
        drawable.start();

        wMn.addView(bottomView, bottomParams);
    }

    /**
     * 隐藏火箭底部
     */
    public void hideBottom() {
        if (bottomView != null) {
            if (bottomView.getParent() != null) {
                wMn.removeView(bottomView);
            }
            bottomView = null;
        }
    }


    //判断火箭是否满足发射条件的标记位
    boolean isReadyFly = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //getX和getRawX的区别：getX获得的是以组件左上角为坐标原点计算的x轴坐标，
                //getRawX获得的是以屏幕左上角为坐标原点计算的x轴坐标
                downX = event.getRawX();
                downY = event.getRawY();
                showBottom();
                break;
            case MotionEvent.ACTION_MOVE: //移动到合适的位置会改变底部的图片背景
                moveX = event.getRawX();
                moveY = event.getRawY();

                params.x += moveX - downX;
                params.y += moveY - downY;

                wMn.updateViewLayout(rocketView, params);

                downX = moveX;
                downY = moveY;

                if (readyToFly(event)) {
                    bottomView.setBackgroundResource(R.mipmap.desktop_bg_tips_3);
                    isReadyFly = true;
                } else {
                    bottomView.setBackgroundResource(R.drawable.bottom_bg);
                    //重新开始播放
                    AnimationDrawable drawable = (AnimationDrawable) bottomView.getBackground();
                    drawable.start();
                }

                break;
            case MotionEvent.ACTION_UP: //移动到合适的位置，手指抬起来才会发射

                //隐藏底部组件
                hideBottom();

                //这里不能使用readyToFly方法判断了，因为底部组件已经为null了
                if (isReadyFly) {
                    fly();
                }
                break;
        }
        return true;
    }


    /**
     * 使用属性动画让火箭飞起来
     */
    private void fly() {
        moveMidle();
        
        //属性动画
        ValueAnimator animator = ValueAnimator.ofInt(params.y, 0);

        animator.setDuration(4000);
        
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //得到动画过程中在duration期间匀速改变的属性的数值（属性就是你意欲改变的属性）
                //持续时间越长，改变的速度就越慢
                int value= (int) animation.getAnimatedValue();

                params.y = value;//将正在改变的数值赋值给参数y
                
                wMn.updateViewLayout(rocketView,params);//每一次改变都要更新组件
            }
        });
        
        animator.start();

        Intent intent = new Intent(rContext, SmokeActivity.class);

        //在服务内部，启动另外一个Activity，必须将这个Activity放到一个新的任务栈中
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rContext.startActivity(intent);
    }

    /**
     * 将火箭移动到中间位置，不能使用gravity。也就是给params的x坐标赋值为，一半屏幕宽度减去一半火箭宽度
     */
    private void moveMidle() {
        Display display = wMn.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        params.x = metrics.widthPixels / 2 - rocketView.getWidth() / 2;

        wMn.updateViewLayout(rocketView, params);
    }

    /**
     * 判断是否满足发射条件，也就是火箭移动到底部View的指定发射位置
     *
     * @param event touch事件
     * @return 是否满足发射条件
     */
    private boolean readyToFly(MotionEvent event) {

        int rockWid = rocketView.getWidth();
        int rockHeiHalf = rocketView.getHeight() / 2;
        int bottomWidth = bottomView.getWidth();

        //封装此组件目前在屏幕的x和y的坐标值
        int[] rLocation = new int[2];//火箭坐标
        int[] bLocation = new int[2];//底部坐标


        //getLocationInWindow(location);此方法内的数组不会被赋值
        rocketView.getLocationOnScreen(rLocation);

        bottomView.getLocationOnScreen(bLocation);

        boolean isBottom = bLocation[1] - rLocation[1] <= rockHeiHalf;
        boolean isLeft = bLocation[0] - rLocation[0] <= rockWid;
        boolean isRight = rLocation[0] - bLocation[0] <= bottomWidth;

        return isBottom && isLeft && isRight;
    }
}
