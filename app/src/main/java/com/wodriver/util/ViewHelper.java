package com.wodriver.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by user on 2016-11-24.
 */

public class ViewHelper {
    /**
     * Gets the String value from an EditText control.
     *
     * @param activity invoking Activity
     * @param viewId resource ID
     * @return the String value from the EditText control
     */

    public static String getStringValue(final Activity activity, final int viewId){
        if(null == activity)
            return "";

        return ((EditText) activity.findViewById(viewId)).getText().toString();
    }

    /**
     * Displays a modal dialog with an OK button.
     *
     * @param activity invoking activity
     * @param title title to display for the dialog
     * @param body content of the dialog
     */
    public static void showDialog(final Activity activity, final String title, final String body){
        if(null == activity)
            return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setNeutralButton("OK", null);
        builder.show();
    }
}
