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
public class FaultDebugDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextView faultPartTV;
    private TextView faultTypeTV;

    private TextView cancel;

    public FaultDebugDialog(@NonNull Context context) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        initView(context);
    }

    public FaultDebugDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_fault_debug, null);
        faultPartTV = view.findViewById(R.id.tv_fault_part);
        faultTypeTV = view.findViewById(R.id.tv_fault_type);
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public void show() {
        super.show();
    }


    public void setFaultBody(String partVal, String causeVal) {
        String faultPart = "";
        if ("6008".equals(partVal) || "4002".equals(partVal)) {
            faultPart = mContext.getString(R.string.toubu);
        } else if ("6009".equals(partVal) || "4004".equals(partVal)) {
            faultPart = mContext.getString(R.string.beibu);
        } else if ("600C".equals(partVal)) {
            faultPart = mContext.getString(R.string.zuobiantunbu);
        } else if ("600D".equals(partVal)) {
            faultPart = mContext.getString(R.string.youbiantunbu);
        } else if ("6007".equals(partVal)) {
            faultPart = mContext.getString(R.string.zuobiantuibu);
        } else if ("600A".equals(partVal)) {
            faultPart = mContext.getString(R.string.youbiantuibu);
        } else if ("60CD".equals(partVal) || "400D".equals(partVal)) {
            faultPart = mContext.getString(R.string.tunbu);
        } else if ("607A".equals(partVal) || "400A".equals(partVal)) {
            faultPart = mContext.getString(R.string.tuibu);
        }
        faultPartTV.setText(faultPart);

        String faultCause = "";
        if ("000A".equals(causeVal)) {
            faultCause = mContext.getString(R.string.dianjisuohuai);
        } else if ("0014".equals(causeVal)) {
            faultCause = mContext.getString(R.string.dianjiguozai);
        } else if ("001E".equals(causeVal)) {
            faultCause = mContext.getString(R.string.dianjiduanlu);
        } else if ("00C8".equals(causeVal)) {
            faultCause = mContext.getString(R.string.cejusuohuai);
        } else if ("00D2".equals(causeVal)) {
            faultCause = mContext.getString(R.string.tongzucejusuohuai);
        } else if ("00DC".equals(causeVal)) {
            faultCause = mContext.getString(R.string.julichazhiguoda);
        } else if ("00E6".equals(causeVal)) {
            faultCause = mContext.getString(R.string.dianjifanxiangdongzuo);
        } else if ("0064".equals(causeVal)) {
            faultCause = mContext.getString(R.string.julibuzaifangwei);
        } else if ("006E".equals(causeVal)) {
            faultCause = mContext.getString(R.string.julituobian);
        } else if ("0078".equals(causeVal)) {
            faultCause = mContext.getString(R.string.mubiaoweizhipianli);
        } else if ("0000".equals(causeVal)) {
            faultCause = mContext.getString(R.string.shebeiyiqiezhengchang);
        }
        faultTypeTV.setText(faultCause);
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
