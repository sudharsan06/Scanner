package com.veetech.hiremee.hm_qr_scanner.AppUtilities;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.veetech.hiremee.hm_qr_scanner.R;

/**
 * Created by SUDV2E08542 on 6/15/2017.
 */

//TODO ** For Custom progress dialog

public class CustomProgressDialog extends Dialog {

    private ImageView imageView;
    public CustomProgressDialog(Context context, int themeResId) {
        super(context, R.style.CustomProgressDialog);

        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setCancelable(false);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60,60);
        imageView = new ImageView(context);
        imageView.setImageResource(themeResId);
        layout.addView(imageView, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2200);
        imageView.setAnimation(anim);
        imageView.startAnimation(anim);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
