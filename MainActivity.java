package com.example.ruchi.cowapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.ruchi.cowapp.Constants;
import com.example.ruchi.cowapp.R;
import com.example.ruchi.cowapp.data.Model;

import retrofit.client.Response;

public class MainActivity extends Activity {
    public EditText mName,mPassword;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        putSharedPreferencesString(getApplicationContext(), Constants.API_KEY, "");

        verifyStoragePermissions(this);
        mName = (EditText) findViewById(R.id.txt_emailid);
        mPassword = (EditText) findViewById(R.id.txt_password);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mName.getText().toString();
                String passwd = mPassword.getText().toString();
                int publisher_id=1;

                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mName.setError("enter a valid email address");
                } else if (passwd.isEmpty() || passwd.length() < 4 || passwd.length() > 10) {
                    mPassword.setError("between 4 and 10 alphanumeric characters");
                } else
                    com.example.ruchi.cowapp.rest.RestClient.getLoginApi(MainActivity.this).login(email,passwd,publisher_id,new com.example.ruchi.cowapp.rest.RestCallback<Model>() {
                        @Override
                         public void failure(String restErrors, boolean networkError) {
                          Log.d("failure","restErrors");

                        }

                        @Override
                        public void success(Model model, Response response) {
                            Log.d("success","s");
                            Intent intent=new Intent(MainActivity.this,ListActivity.class);
                            startActivity(intent);

                        }
                    });
            }

        });


        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void putSharedPreferencesString(Context context, String key, String val){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit=preferences.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getSharedPreferencesString(Context context, String key, String _default){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, _default);
    }
}






