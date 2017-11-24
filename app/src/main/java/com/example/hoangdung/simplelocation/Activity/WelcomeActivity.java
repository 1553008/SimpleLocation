package com.example.hoangdung.simplelocation.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Interface.FirebaseAuthCommand;
import com.example.hoangdung.simplelocation.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    //For Debugging
    private final String TAG = WelcomeActivity.class.getSimpleName();
    //Request code
    private final int FINE_LOCATION_REQUEST = 0;
    //Delay time
    private final int delayTime = 0;
    //Login Button
    public @BindView(R.id.loginBtn) Button mLoginBtn;
    private CallbackManager mCallbackManager;
    //Facebook and Firebase
    //Read Permission
    private List<String> readPermissions = Arrays.asList(
            "email","public_profile");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ButterKnife.bind(this);
        getLocationPermission();
        loginSetup();

    }

    /**
     * Request users to permit ACCESS_FINE_LOCATION
     */
    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission");
        //If the applications is not permitted, ask for permission
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_REQUEST);
        }
    }
    /**
     *
     */
    private void loginSetup(){
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        // start keeping track of my place list in database
        FirebaseCenter.getInstance().listenForMyPlaceDatabase();
        //if already loggined
        if(checkAlreadyLoggined()){
            mLoginBtn.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToMapsActivity();
                    finish();
                }
            }, delayTime);
            return;
        }
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest =  GraphRequest.newMeRequest(
                        loginResult.getAccessToken()
                        , new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Intent intent = new Intent(WelcomeActivity.this,MapsActivity.class);
                                try {
                                    //Store User Information From Facebook into SharePreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email",object.getString("email"));
                                    editor.putString("first_name",object.getString("first_name"));
                                    editor.putString("last_name",object.getString("last_name"));
                                    editor.putString("picture",object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    editor.commit();

                                    // Firebase authentication with facebook
                                    FirebaseCenter.getInstance().handleFacebookAccessToken(loginResult.getAccessToken(),
                                            object,
                                            new FirebaseAuthCommand() {
                                                @Override
                                                public void onSuccess() {
                                                    goToMapsActivity();
                                                    finish();
                                                }

                                                @Override
                                                public void onFail() {
                                                    finish();
                                                }
                                            });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );//graphRequest
                Bundle params = new Bundle();
                params.putString("fields","email,first_name,last_name,picture");
                graphRequest.setParameters(params);
                graphRequest.executeAsync();

            }
            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private boolean checkAlreadyLoggined(){
        //If the user is already loggin, go straight to the MapsActivity
        return AccessToken.getCurrentAccessToken()!= null && Profile.getCurrentProfile() != null;
    }
    private void goToMapsActivity(){
        Intent intent = new Intent(WelcomeActivity.this,MapsActivity.class);
        WelcomeActivity.this.startActivity(intent);
    }
    private void startLogin(){
        LoginManager.getInstance().logInWithReadPermissions(WelcomeActivity.this,readPermissions);
    }
    //------------------------------Facebook--------------------------------------

    /**
     * Request Result Callback Method
     * @param requestCode
     * @param permissions
     * @param grantResults if fail, this param is empty
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case FINE_LOCATION_REQUEST:{
                if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    finish();
                else{

                }
            }//Case Fine_Location_Request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
