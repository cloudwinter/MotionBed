package com.sn.blackdianqi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sn.blackdianqi.MainActivity;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LocaleUtils;
import com.sn.blackdianqi.util.Prefer;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * 语言对话框
 */
public class LanguageDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private RelativeLayout rlFrench;
    private ImageView imgSelectedFrench;
    private RelativeLayout rlEnglish;
    private ImageView imgSelectedEnglish;
    private TextView cancel;

    public LanguageDialog(@NonNull Context context) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        initView(context);
    }

    public LanguageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_language, null);
        rlFrench = view.findViewById(R.id.rl_french);
        rlFrench.setOnClickListener(this);
        rlEnglish = view.findViewById(R.id.rl_english);
        rlEnglish.setOnClickListener(this);
        imgSelectedFrench = view.findViewById(R.id.img_selected_fr);
        imgSelectedEnglish = view.findViewById(R.id.img_selected_en);
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void show() {
        super.show();
        // 判断当前选中的语言
        if (Prefer.getInstance().getSelectedLanguage().equals("fr")) {
            imgSelectedFrench.setVisibility(View.VISIBLE);
            imgSelectedEnglish.setVisibility(View.GONE);
        } else {
            imgSelectedFrench.setVisibility(View.GONE);
            imgSelectedEnglish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.rl_french:
                imgSelectedFrench.setVisibility(View.VISIBLE);
                imgSelectedEnglish.setVisibility(View.GONE);
                Prefer.getInstance().setSelectedLanguage("fr");
                if (LocaleUtils.needUpdateLocale(mContext, LocaleUtils.LOCALE_FRENCH)) {
                    LocaleUtils.updateLocale(mContext, LocaleUtils.LOCALE_FRENCH);
                    restartAct();
                }
                dismiss();
                break;
            case R.id.rl_english:
                imgSelectedFrench.setVisibility(View.GONE);
                imgSelectedEnglish.setVisibility(View.VISIBLE);
                Prefer.getInstance().setSelectedLanguage("en");
                Resources resources = mContext.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();
                // 应用用户选择语言
                config.locale = Locale.ENGLISH;
                resources.updateConfiguration(config, dm);
                dismiss();
                restartAct();
                break;

        }
    }


    /**
     * 重启当前Activity
     */
    private void restartAct() {
//        finish();
        Intent _Intent = new Intent(mContext, MainActivity.class);
        _Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(_Intent);
        //清除Activity退出和进入的动画
//        overridePendingTransition(0, 0);
    }
}
