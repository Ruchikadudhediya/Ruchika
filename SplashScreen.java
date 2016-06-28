package com.example.ruchi.cowapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.ruchi.cowapp.Constants;
import com.example.ruchi.cowapp.R;

/**
 * Created by Ruchi on 6/1/2016.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(6000);
                    String api_key = com.example.ruchi.cowapp.activity.MainActivity.getSharedPreferencesString(getApplicationContext(), Constants.API_KEY, null);
                    if(api_key==null){
                        Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else if(api_key!=null){
                        Intent intentr=new Intent(SplashScreen.this,ListActivity.class);
                        startActivity(intentr);
                    }

                }catch(InterruptedException e){
                    e.printStackTrace();
                }


            }
        };
        timerThread.start();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
