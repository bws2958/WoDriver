package com.wodriver.AWS.userpool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.wodriver.R;
import com.wodriver.SignInActivity;
import com.wodriver.util.ViewHelper;

/**
 * Created by user on 2016-11-24.
 */

public class SignUpActivity extends Activity{
    private static final String LOG_TAG = SignInActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void signUp(final View view){
        final String username = ViewHelper.getStringValue(this, R.id.signup_username);
        final String password = ViewHelper.getStringValue(this, R.id.signup_password);
        final String givenName = ViewHelper.getStringValue(this, R.id.signup_given_name);
        final String email = ViewHelper.getStringValue(this, R.id.signup_email);
        final String phone = ViewHelper.getStringValue(this, R.id.signup_phone);

        Log.d(LOG_TAG, "User Name = " + username);
        Log.d(LOG_TAG, "Given Name = " + givenName);
        Log.d(LOG_TAG, "email = " + email);
        Log.d(LOG_TAG, "phone = " + phone);

        final Intent intent = new Intent();
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.USERNAME, username);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.PASSWORD, password);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.GIVEN_NAME, givenName);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.EMAIL_ADDRESS, email);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.PHONE_NUMBER, phone);

        setResult(RESULT_OK, intent);

        finish();
    }
}
