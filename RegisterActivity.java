package com.example.ruchi.cowapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ruchi.cowapp.R;
import com.example.ruchi.cowapp.data.Model;
import com.example.ruchi.cowapp.data.Resource;
import com.example.ruchi.cowapp.utils.AppUtil;
import com.example.ruchi.cowapp.widget.MImageLoader;
import com.example.ruchi.cowapp.widget.Preferences;
import com.kbeanie.imagechooser.api.ImageChooserActivity;

import java.io.File;
import java.util.ArrayList;

import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class RegisterActivity extends Activity {
    public String PUBLISHER_ID = "1";
    public EditText mNamereg,mEmailreg,mPasswordreg,mPhone_noreg,mOccupationreg,mCityreg;
    private Dialog mPg;
    private int mSelectedImagePos;
    private ImageView mSelectedImageView;
    private static final int IMAGE_CHOOSER = 100;
    private ArrayList<Resource> mResourceList;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_register);
        mNamereg=(EditText)findViewById(R.id.txt_name);
        mPasswordreg=(EditText)findViewById(R.id.txt_passwordreg);
        mEmailreg=(EditText)findViewById(R.id.txt_emailreg);
        mPhone_noreg=(EditText)findViewById(R.id.txt_phone);
        mOccupationreg=(EditText)findViewById(R.id.txt_occupation);
        mCityreg=(EditText)findViewById(R.id.txt_city);
        mResourceList = new ArrayList<Resource>();

        initScreen();
        submit();
    }

    private void submit() {
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msName = mNamereg.getText().toString();
                String msEmail = mEmailreg.getText().toString();
                String msPassword = mPasswordreg.getText().toString();
                String msPhone = mPhone_noreg.getText().toString();
                String msOccupation = mOccupationreg.getText().toString();
                String msCity = mCityreg.getText().toString();

                if (msName.isEmpty()|| msName.length() < 3) {
                    mNamereg.setError("at least 3 characters");
                }else if(msEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(msEmail).matches()) {
                    mEmailreg.setError("enter a valid email address");
                }else if (msPassword.isEmpty() ||msPassword.length() < 4 || msPassword.length() > 10) {
                    mPasswordreg.setError("between 4 and 10 alphanumeric characters");
                }else if (msPhone.isEmpty()  || msPhone.length() < 10||msPhone.length()>10) {
                    mPhone_noreg.setError("Enter 10 numeric characters");
                }else if (msOccupation.isEmpty())  {
                    mOccupationreg.setError("please enter occupation");
                }else if (msCity.isEmpty()) {
                    mCityreg.setError("Please Enter name of city");
                }

                validate();
            }
        });
    }

    private void initScreen() {
        findViewById(R.id.btn_add_photo_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedImagePos = 0;
                mSelectedImageView = (ImageView) findViewById(R.id.image_2);
                showImageChooserActivity();
            }
        });
    }

    private void validate() {
        if((mResourceList==null||mResourceList.size()==0)){
            AppUtil.showErrorDialog("", RegisterActivity.this);
            return;
        }
        uploadImages();
    }


    private void uploadImages() {
        ArrayList<String> imageStringPath=new ArrayList<String>();
        ArrayList<String> mImageType=new ArrayList<String>();

        if(mResourceList!=null && mResourceList.size()>0){
            for(int i=0;i<mResourceList.size();i++){
                if(mResourceList.get(i).getUri()!=null && mResourceList.get(i).isDirty()){
                    String imagepath=AppUtil.getRealPathFromURI(Uri.parse(mResourceList.get(i).getUri()), RegisterActivity.this);
                    if(Preferences.DEBUG) Log.d("Activity", "***image path:" + imagepath);
                    imageStringPath.add(imagepath);
                }
                else {
                    if(mResourceList.get(i).getUrl()!=null){
                        imageStringPath.add(mResourceList.get(i).getUrl());
                        mImageType.add("null");
                    }
                }
            }
        }

        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        TypedFile typedFile = new TypedFile(".jpg", new File(imageStringPath.get(0)));
        multipartTypedOutput.addPart("name", new TypedString(mNamereg.getText().toString()));
        multipartTypedOutput.addPart("email",new TypedString(mEmailreg.getText().toString()));
        multipartTypedOutput.addPart("passwd",new TypedString(mPasswordreg.getText().toString()));
        multipartTypedOutput.addPart("phone",new TypedString(mPhone_noreg.getText().toString()));
        multipartTypedOutput.addPart("occupation",new TypedString(mOccupationreg.getText().toString()));
        multipartTypedOutput.addPart("city",new TypedString(mCityreg.getText().toString()));
        multipartTypedOutput.addPart("publisher_id",new TypedString(PUBLISHER_ID));
        multipartTypedOutput.addPart("profile_pic", typedFile);

        com.example.ruchi.cowapp.rest.RestClient.getRegisterApi(RegisterActivity.this).register(multipartTypedOutput, new com.example.ruchi.cowapp.rest.RestCallback<Model>() {


            @Override
            public void failure(String restErrors, boolean networkError) {

            }

            @Override
            public void success(Model model, Response response) {
                String str=new String();
                str.concat(model.getApi_key());


            }
        });
    }


    private void showImageChooserActivity() {
        Intent intent = new Intent(RegisterActivity.this, ImageChooserActivity.class);
        startActivityForResult(intent, IMAGE_CHOOSER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_CHOOSER){
            if(resultCode == RESULT_OK) {
                String path = data.getStringExtra(ImageChooserActivity.INTENT_IMAGE_BANNER_PATH);
                Uri selectedImage = Uri.fromFile(new File(path));

                onImageSelected(selectedImage);
                if(mSelectedImageView!=null)
                    MImageLoader.displayImage(RegisterActivity.this, selectedImage.toString(), mSelectedImageView, R.drawable.user_stub);
            } else {
                if(data!=null){
                    String reason = data.getStringExtra(ImageChooserActivity.INTENT_ERROR_MESSAGE);
                    AppUtil.showErrorDialog(reason, RegisterActivity.this);
                }
            }
        }
    }

    private void onImageSelected(Uri imageUri){
        //Put Uri into arraylist
        if(mSelectedImagePos<mResourceList.size()){
            mResourceList.get(mSelectedImagePos).setDirty(true);
            mResourceList.get(mSelectedImagePos).setUri(imageUri.toString());
        }
        else{
            Resource r=new Resource();
            r.setDirty(true);
            r.setUri(imageUri.toString());
            mResourceList.add(r);
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
}