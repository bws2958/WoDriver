package com.wodriver;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobile.user.signin.SignInManager;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;

import com.amazonaws.mobile.user.signin.FacebookSignInProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by user on 2016-11-19.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = SignInActivity.class.getSimpleName();
    private SignInManager signInManager;

    private SignInButton signInButton;

    /** Permission Request Code (Must be < 256). */
    private static final int GET_ACCOUNTS_PERMISSION_REQUEST_CODE = 93;

    /** The Google OnClick listener, since we must override it to get permissions on Marshmallow and above. */
    private View.OnClickListener googleOnClickListener;

    private TextView signUp;
    private TextView forgotPassword;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    private IdentityManager identityManager;

    protected void onCreate(final Bundle savedInstancdState){
        Log.e(LOG_TAG, String.format("Sign-in activity start"));
        super.onCreate(savedInstancdState);
        setContentView(R.layout.activity_sign_in);

        signUp = (TextView)findViewById(R.id.signIn_textView_CreateNewAccount);
        signUp.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.signIn_textView_ForgotPassword);
        forgotPassword.setOnClickListener(this);

        signInManager = SignInManager.getInstance(this);
        signInManager.setResultsHandler(this, new SignInResultsHandler());

        // Initialize sign-in buttons.
        signInManager.initializeSignInButton(FacebookSignInProvider.class, this.findViewById(R.id.fb_login_button));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        signInButton = (SignInButton) findViewById(R.id.g_login_button);
        signInButton.setOnClickListener(this);


        signInManager.initializeSignInButton(CognitoUserPoolsSignInProvider.class,
                this.findViewById(R.id.signIn_imageButton_login));
    }

    public void onClick(final View view){
        if(view == signUp){
            startActivity(new Intent(this, com.wodriver.AWS.userpool.SignUpActivity.class));
            finish();
        }
        if(view == forgotPassword){
            startActivity(new Intent(this, com.wodriver.AWS.userpool.ForgotPasswordActivity.class));
            finish();
        }
        if(view == signInButton){
            signIn();
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

//        // 성공하면 넘긴다
//        startActivity(new Intent(SignInActivity.this, LogIn.class)
//                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
//        finish();

    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), "bye", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, final String permissions[],
                                           final int[] grantResults) {
        if (requestCode == GET_ACCOUNTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.findViewById(R.id.g_login_button).callOnClick();
            } else {
                Log.i(LOG_TAG, "Permissions not granted for Google sign-in. :(");
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signInManager.handleActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        IdentityManager.SignInResultsHandler resultsHandler;
        Log.d(LOG_TAG, "handleSignInResult : " + result.isSuccess());
        if(result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();
            Toast.makeText(getApplicationContext(), account.getEmail(), Toast.LENGTH_LONG).show();

            startActivity(new Intent(SignInActivity.this, LogIn.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            // finish should always be called on the main thread.
            finish();
        }else {

        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // pause/resume Mobile Analytics collection
        AWSMobileClient.defaultMobileClient().handleOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // pause/resume Mobile Analytics collection
        AWSMobileClient.defaultMobileClient().handleOnPause();
    }

    /**
     * SignInResultsHandler handles the final result from sign in. Making it static is a best
     * practice since it may outlive the SplashActivity's life span.
     */
    private class SignInResultsHandler implements IdentityManager.SignInResultsHandler {
        /**
         * Receives the successful sign-in result and starts the main activity.
         * @param provider the identity provider used for sign-in.
         */
        @Override
        public void onSuccess(final IdentityProvider provider){
            Log.d(LOG_TAG, String.format("User sign-in with %s succeeded",
                    provider.getDisplayName()));

            // The sign-in manager is no longer needed once signed in.
            SignInManager.dispose();

            Toast.makeText(SignInActivity.this, String.format("Sign-in with %s succeeded.",
                    provider.getDisplayName()), Toast.LENGTH_LONG).show();

            // Load user name and image.
            AWSMobileClient.defaultMobileClient()
                    .getIdentityManager().loadUserInfoAndImage(provider, new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "Launching Main Activity...");
                    startActivity(new Intent(SignInActivity.this, LogIn.class)
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    // finish should always be called on the main thread.
                    finish();
                }
            });
        }

        /**
         * Receives the sign-in result indicating the user canceled and shows a toast.
         * @param provider the identity provider with which the user attempted sign-in.
         */
        @Override
        public void onCancel(final IdentityProvider provider) {
            Log.d(LOG_TAG, String.format("User sign-in with %s canceled.",
                    provider.getDisplayName()));

            Toast.makeText(SignInActivity.this, String.format("Sign-in with %s canceled.",
                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
        }

        /**
         * Receives the sign-in result that an error occurred signing in and shows a toast.
         * @param provider the identity provider with which the user attempted sign-in.
         * @param ex the exception that occurred.
         */
        @Override
        public void onError(final IdentityProvider provider, final Exception ex) {
            Log.e(LOG_TAG, String.format("User Sign-in failed for %s : %s",
                    provider.getDisplayName(), ex.getMessage()), ex);

            final AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(SignInActivity.this);
            errorDialogBuilder.setTitle("Sign-In Error");
            errorDialogBuilder.setMessage(
                    String.format("Sign-in with %s failed.\n%s", provider.getDisplayName(), ex.getMessage()));
            errorDialogBuilder.setNeutralButton("Ok", null);
            errorDialogBuilder.show();
        }
    }

}
