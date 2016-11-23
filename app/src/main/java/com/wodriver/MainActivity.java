package com.wodriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /** Class name for log messages. */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** The identity manager used to keep track of the current user account. */
    private IdentityManager identityManager;

    /** The toolbar view control. */
    private Toolbar toolbar;

    /** Bundle key for saving/restoring the toolbar title. */
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";

    /** Buttons in drawerlayout */
    private Button signOutButton;
    private Button signInButton;

    /** Buttons in main layout*/
    private Button showMap;

    private void setupToolbar(final Bundle savedInstanceState){
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if(savedInstanceState != null){
            assert getSupportActionBar() != null;

            getSupportActionBar().setTitle(
                    savedInstanceState.getCharSequence(BUNDLE_KEY_TOOLBAR_TITLE)
            );
        }
    }

    private void setupSignInButtons(){
        signOutButton = (Button) findViewById(R.id.button_signout);
        signOutButton.setOnClickListener(this);

        signInButton = (Button) findViewById(R.id.button_signin);
        signInButton.setOnClickListener(this);

        final boolean isUserSignedIn = identityManager.isUserSignedIn();
        signOutButton.setVisibility(isUserSignedIn ? View.VISIBLE : View.INVISIBLE);
        signInButton.setVisibility(!isUserSignedIn ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AWSMobileClient.initializeMobileClientIfNecessary(this);

        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.activity_main);

        showMap = (Button) findViewById(R.id.map);
        showMap.setOnClickListener(this);
    }

    public void onClick(final View view){
        if(view == signOutButton){
            // The user is currently signed in with a provider. Sign out of that provider.
            identityManager.signOut();
            // Show the sign-in button and hide the sign-out button.
            signOutButton.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);

            return;
        }
        if(view == signInButton){
            // Start the sign-in activity. Do not finish this activity to allow the user to navigate back.
            startActivity(new Intent(this, SignInActivity.class));

            return;
        }

        if(view == showMap){
            startActivity(new Intent(this, Map.class));

            return;
        }
    }

    protected void onResume(){
        super.onResume();

        setupSignInButtons();

        // Obtain a reference to the mobile client
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // pause/resume Mobile Analytics collection
        awsMobileClient.handleOnResume();
    }

    protected void onPause(){
        super.onPause();

        // Obtain a reference to the mobile client
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // pause/resume Mobile Analytics collection
        awsMobileClient.handleOnPause();
    }
}
