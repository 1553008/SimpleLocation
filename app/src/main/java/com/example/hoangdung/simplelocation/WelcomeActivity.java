package com.example.hoangdung.simplelocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;

public class WelcomeActivity extends AppCompatActivity {
    //For Debugging
    private final String TAG = WelcomeActivity.class.getSimpleName();
    //Request code
    private final int FINE_LOCATION_REQUEST = 0;
    //Delay time
    private final int delayTime = 2000;
    //Login Button
    private LoginButton mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
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
        mLoginBtn = findViewById(R.id.login_button);
        mLoginBtn.setText("Login with Facebook");
    }
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
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WelcomeActivity.this,MapsActivity.class);
                            WelcomeActivity.this.startActivity(intent);
                            WelcomeActivity.this.finish();
                        }
                    },delayTime);

                }
            }//Case Fine_Location_Request
        }
    }

}
