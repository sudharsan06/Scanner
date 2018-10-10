package com.veetech.hiremee.hm_qr_scanner.AppUtilities;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by SUDV2E08542 on 8/30/2017.
 */

public class Constant {

    public static void ToastBottomMessage(Context context, String message) {
        Toast loginToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        loginToast.setGravity(Gravity.BOTTOM, 0, 0);
        loginToast.show();
    }

}
