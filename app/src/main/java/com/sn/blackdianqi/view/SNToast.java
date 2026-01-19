package com.sn.blackdianqi.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sn.blackdianqi.R;


public class SNToast extends Toast {

    private static SNToast toast;

    private static View view;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    private SNToast(Context context) {
        super(context);
    }

    public static SNToast makeText(Context context, CharSequence text, int duration) {
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.view_toast, null);
        view = v;
        TextView tv = (TextView)v.findViewById(R.id.toast_text);
        tv.setText(text);
        toast = new SNToast(context);
        toast.setView(v);
        toast.setDuration(duration);
        return toast;
    }

    @Override
    public void setText(int resId) {
        if (view == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView)view.findViewById(R.id.toast_text);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(resId);
    }

    @Override
    public void setText(CharSequence s) {
        if (view == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView)view.findViewById(R.id.toast_text);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }
}
