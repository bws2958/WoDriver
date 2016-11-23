package com.wodriver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;
import com.amazonaws.mobile.user.signin.SignInManager;
import com.amazonaws.mobile.user.signin.SignInProvider;

import java.util.concurrent.CountDownLatch;

/**
 * Created by user on 2016-11-21.
 */

public class SplashActivity extends Activity{
    private static final String LOG_TAG = SplashActivity.class.getSimpleName();
    private final CountDownLatch timeoutLatch = new CountDownLatch(1);
    private SignInManager signInManager;

    private class SignInResultHandler implements IdentityManager.SignInResultsHandler{
        public void onSuccess(final IdentityProvider provider){
            Log.d(LOG_TAG, String.format("User sign-in with previous %s provider succeeded", provider.getDisplayName()));

            SignInManager.dispose();

            Toast.makeText(SplashActivity.this, String.format("Sign-in with %s succeeded", provider.getDisplayName()), Toast.LENGTH_LONG).show();

            AWSMobileClient.defaultMobileClient().getIdentityManager().loadUserInfoAndImage(provider, new Runnable() {
                @Override
                public void run() {
                    goMain();
                }
            });
        }

        public void onCancel(final IdentityProvider provider){
            Log.wtf(LOG_TAG, "Cancel can't happen when handling a previously sign-in user.");
        }

        public void onError(final IdentityProvider provider, Exception ex){
            Log.e(LOG_TAG, String.format("Cognito credentials refresh with %s provider failed. Error: %s", provider.getDisplayName(), ex.getMessage()), ex);

            Toast.makeText(SplashActivity.this, String.format("Sign-in with %s failed.", provider.getDisplayName()), Toast.LENGTH_LONG).show();
            goMain();
        }
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(LOG_TAG, "success onCreate");


        final Thread thread = new Thread(new Runnable(){
           public void run(){
               signInManager = SignInManager.getInstance(SplashActivity.this);

               final SignInProvider provider = signInManager.getPreviouslySignedInProvider();

               // if the user was already previously in to a provider.
               if(provider != null){
                   // asynchronously handle refreshing credentials and call our handler.
                   signInManager.refreshCredentialsWithProvider(SplashActivity.this, provider, new SignInResultHandler());
                   Log.d(LOG_TAG, "in if phrase");
               }else{
                   // Asyncronously go to the main activity (after the splash delay has expired).
                   goMain();
                   Log.d(LOG_TAG, "in else phrase");
               }

               try{
                   Thread.sleep(2000);
               }catch (InterruptedException e){}

                timeoutLatch.countDown();
           }
        });
        thread.start();
    }

    // if view touched, go to main activity right now
    public boolean onTouchEvent(MotionEvent event){
        timeoutLatch.countDown();
        return true;
    }

    private void goAfterSplashTimeout(final Intent intent){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    timeoutLatch.await();
                }catch (InterruptedException e){

                }

                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
        thread.start();
    }

    protected void goMain(){
        Log.d(LOG_TAG, "Launching Main Activity...");
        goAfterSplashTimeout(new Intent(this, MainActivity.class));
    }

    protected void goSignIn(){
        Log.d(LOG_TAG, "Launching Sign-In Activity...");
        goAfterSplashTimeout(new Intent(this, SignInActivity.class));
    }

    protected  void onResume(){
        super.onResume();

        AWSMobileClient.defaultMobileClient().handleOnResume();
    }

    protected  void onPause(){
        super.onPause();

        AWSMobileClient.defaultMobileClient().handleOnPause();
    }
}
