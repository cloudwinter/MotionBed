package com.sn.blackdianqi.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.view.AnjianDuanWeitiaoView;
import com.sn.blackdianqi.view.AnjianWeitiaoView;
import com.sn.blackdianqi.view.ChildTouchListener;
import com.sn.blackdianqi.view.JiyiView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 微调
 */
public class WeitiaoW7Fragment extends WeitiaoBaseFragment implements View.OnTouchListener {

    public static final String TAG = "WeitiaoFragment";


    @BindView(R.id.img_anjian_top_icon)
    ImageView topIconImgView;
    @BindView(R.id.text_anjian_top_title)
    TextView topTitleTextView;
    @BindView(R.id.layout_head)
    LinearLayout headLayout;

    // 分体
    @BindView(R.id.layout_fenti)
    LinearLayout fentiLayout;
    @BindView(R.id.view_fenti)
    JiyiView fentiView;
    @BindView(R.id.view_fenti_beibutiaozheng_left)
    AnjianDuanWeitiaoView fentiBeibutiaozhengLeftView;
    @BindView(R.id.view_fenti_beibutiaozheng_right)
    AnjianDuanWeitiaoView fentiBeibutiaozhengRightView;
    @BindView(R.id.view_fenti_tuibutiaozheng_left)
    AnjianDuanWeitiaoView fentiTuibutiaozhengLeftView;
    @BindView(R.id.view_fenti_tuibutiaozheng_right)
    AnjianDuanWeitiaoView fentiTuibutiaozhengRightView;

    // 同步
    @BindView(R.id.layout_tongbu)
    LinearLayout tongbuLayout;
    @BindView(R.id.view_tongbu)
    JiyiView tongbuView;
    @BindView(R.id.view_tongbu_beibutiaozheng)
    AnjianWeitiaoView tongbuBeibutiaozhengView;
    @BindView(R.id.view_tongbu_tuibutiaozheng)
    AnjianWeitiaoView tongbuTuibutiaozhengView;


    AnimationDrawable animationDrawable = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_weitiao_w7, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        fentiView.setOnTouchListener(this);
        tongbuView.setOnTouchListener(this);
        fentiBeibutiaozhengLeftView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 03 97 01");
                    startAnimation(R.drawable.weitiao_beibu_top_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_beibutiaozheng_1, R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 04 D6 C3");
                    startAnimation(R.drawable.weitiao_beibu_bottom_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_beibutiaozheng_1, R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }
        });

        fentiBeibutiaozhengRightView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 0D 16 C5");
                    startAnimation(R.drawable.weitiao_beibu_top_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_beibutiaozheng_1, R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 0E 56 C4");
                    startAnimation(R.drawable.weitiao_beibu_bottom_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_beibutiaozheng_1, R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }
        });

        fentiTuibutiaozhengLeftView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 01 16 C0");
                    startAnimation(R.drawable.weitiao_tuibu_top_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_tuibutiaozheng_1, R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 02 56 C1");
                    startAnimation(R.drawable.weitiao_tuibu_bottom_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.xr_tuibuxunhuan_da, R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }
        });

        fentiTuibutiaozhengRightView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 06 57 02");
                    startAnimation(R.drawable.weitiao_tuibu_top_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_tuibutiaozheng_1, R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 07 96 C2");
                    startAnimation(R.drawable.weitiao_tuibu_bottom_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.xr_tuibuxunhuan_da, R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }
        });

        tongbuBeibutiaozhengView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 35 17 17");
                    startAnimation(R.drawable.weitiao_beibu_top_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_beibutiaozheng_1, R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 36 57 16");
                    startAnimation(R.drawable.weitiao_beibu_bottom_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_beibutiaozheng_1, R.string.beibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }
        });

        tongbuTuibutiaozhengView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 37 96 D6");
                    startAnimation(R.drawable.weitiao_tuibu_top_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_tuibutiaozheng_1, R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    setTitle(R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 38 D6 D2");
                    startAnimation(R.drawable.weitiao_tuibu_bottom_animation);
                } else if (isUPorCancel(event.getAction())) {
                    setTopIconAndTitle(R.mipmap.animation_tuibutiaozheng_1, R.string.tuibutiaozheng);
                    sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                    stopAnimation();
                }
            }
        });

    }

    private void startAnimation(int animationId) {
        topIconImgView.setBackground(ContextCompat.getDrawable(getContext(), animationId));
        animationDrawable = (AnimationDrawable) topIconImgView.getBackground();
        animationDrawable.start();
    }

    private void stopAnimation() {
        if (animationDrawable != null) {
            animationDrawable.stop();
            animationDrawable = null;
        }
    }

    /**
     * 设置顶部icon和title
     *
     * @param iconResId
     * @param titleResId
     */
    private void setTopIconAndTitle(int iconResId, int titleResId) {
        topIconImgView.setBackground(ContextCompat.getDrawable(getContext(), iconResId));
        topTitleTextView.setText(getString(titleResId));
    }


    /**
     * 设置顶部的title
     *
     * @param titleResId
     */
    private void setTitle(int titleResId) {
        topTitleTextView.setText(getString(titleResId));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.view_fenti:
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(FENTI_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(FENTI_WHAT);
                }
                break;
            case R.id.view_tongbu:
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(TONGBU_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(TONGBU_WHAT);
                }
                break;
        }
        return true;
    }


    private long eventDownTime = 0L;

    private static final int FENTI_WHAT = 1;
    private static final int TONGBU_WHAT = 2;

    /**
     * 记忆1 1
     * 记忆2  2
     * 看电视 3
     * 零压力 4
     * 止鼾 5
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FENTI_WHAT:
                    fentiLongClick();
                    break;
                case TONGBU_WHAT:
                    tongbuLongClick();
                    break;
                default:
                    break;
            }
        }
    };

    private void fentiLongClick() {
        fentiLayout.setVisibility(View.GONE);
        tongbuLayout.setVisibility(View.VISIBLE);
        tongbuView.setSelected(true);
    }

    private void tongbuLongClick() {
        tongbuLayout.setVisibility(View.GONE);
        fentiLayout.setVisibility(View.VISIBLE);
        fentiView.setSelected(true);
    }
}
