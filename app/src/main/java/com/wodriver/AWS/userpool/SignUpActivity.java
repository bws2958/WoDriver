package com.wodriver.AWS.userpool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.wodriver.LogIn;
import com.wodriver.ManagerClass;
import com.wodriver.MapperClass;
import com.wodriver.R;
import com.wodriver.SignInActivity;
import com.wodriver.util.DBManager;
import com.wodriver.util.ViewHelper;

/**
 * Created by user on 2016-11-24.
 */

public class SignUpActivity extends Activity implements View.OnClickListener{
    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();

    Button button;

    String username;
    String password;
    String givenName;
    String email;
    String phone;

    EditText editText;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
//    final String username = ViewHelper.getStringValue(this, R.id.signup_username);
//    final String password = ViewHelper.getStringValue(this, R.id.signup_password);
//    final String givenName = ViewHelper.getStringValue(this, R.id.signup_given_name);
//    final String email = ViewHelper.getStringValue(this, R.id.signup_email);
//    final String phone = ViewHelper.getStringValue(this, R.id.signup_phone);

    private DBManager dbManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        button = (Button)findViewById(R.id.signup_button);
        button.setOnClickListener(this);

    }

    public void onClick(final View view){
        if(view == button){
            editText = (EditText)findViewById(R.id.signup_username);
            editText1 = (EditText)findViewById(R.id.signup_password);
            editText2 = (EditText)findViewById(R.id.signup_given_name);
            editText3 = (EditText)findViewById(R.id.signup_email);
            editText4 = (EditText)findViewById(R.id.signup_phone);

            username = editText.getText().toString();
            password = editText1.getText().toString();
            givenName = editText2.getText().toString();
            email = editText3.getText().toString();
            phone = editText4.getText().toString();

            new updatetable().execute();



//            dbManager = new DBManager(getApplicationContext(), "WoDriver.db", null, 1);

//            dbManager.delete("DELETE FROM USER_INFO");
//            dbManager.insert("insert into USER_INFO values(null, '" + username + "', '" + password + "', '" + givenName + "', '" + email + "', '" + phone + "');");

//            Toast.makeText(getApplicationContext(), dbManager.PrintData(), Toast.LENGTH_LONG).show();
        }

        finish();
    }

    private class updatetable extends AsyncTask<Void, Void, Integer>{
        protected Integer doInBackground(Void... params){
            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getcredentials(SignUpActivity.this);

            MapperClass mapperClass = new MapperClass();
            mapperClass.setName(username);
            mapperClass.setTitle(givenName);
            mapperClass.setPassword(password);

            if(credentialsProvider != null && managerClass != null){
                DynamoDBMapper dynamoDBMapper = managerClass.intiDynamoClient(credentialsProvider);
                dynamoDBMapper.save(mapperClass);
            }else{
                return 2;
            }
            return 1;
        }

        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(integer == 1){
                Toast.makeText(SignUpActivity.this, "update success", Toast.LENGTH_LONG).show();
            }else if(integer == 2){
                Toast.makeText(SignUpActivity.this, "fail updating", Toast.LENGTH_LONG).show();
            }
        }
    }

//    public void signUp(final View view){
//        final String username = ViewHelper.getStringValue(this, R.id.signup_username);
//        final String password = ViewHelper.getStringValue(this, R.id.signup_password);
//        final String givenName = ViewHelper.getStringValue(this, R.id.signup_given_name);
//        final String email = ViewHelper.getStringValue(this, R.id.signup_email);
//        final String phone = ViewHelper.getStringValue(this, R.id.signup_phone);
//
//        Log.d(LOG_TAG, "User Name = " + username);
//        Log.d(LOG_TAG, "Given Name = " + givenName);
//        Log.d(LOG_TAG, "email = " + email);
//        Log.d(LOG_TAG, "phone = " + phone);
//
//        final Intent intent = new Intent();
//        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.USERNAME, username);
//        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.PASSWORD, password);
//        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.GIVEN_NAME, givenName);
//        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.EMAIL_ADDRESS, email);
//        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.PHONE_NUMBER, phone);
//
//        setResult(RESULT_OK, intent);
//
//        Log.d(LOG_TAG, "finish");
//        finish();
//    }
}
