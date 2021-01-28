package com.example.digitrafficinspectorv0;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class CaptureViolation extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    Button cameraBtn,submitBtn,homeBtn;
    EditText vehicleNumberInput;
    String currentPhotoPath;
    StorageReference storageReference;
    String pic1="",vn="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_violation);

        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.cameraBtn);
        homeBtn = findViewById(R.id.homeBtn);
        storageReference = FirebaseStorage.getInstance().getReference();
        submitBtn = findViewById(R.id.proceedBtn);
        vehicleNumberInput = findViewById(R.id.vehicleNumberInput);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });
        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        submitBtn.setTypeface(face);
        cameraBtn.setTypeface(face);
        homeBtn.setTypeface(face);
        vehicleNumberInput.setTypeface(face);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ProfileActivity2.class));
                finish();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vn = vehicleNumberInput.getText().toString();
                if (vn.equals("") || pic1.equals("")){
                    Toast.makeText(CaptureViolation.this, "Vehicle number And image evidence are must.", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(CaptureViolation.this, registerComplaintPage2.class);
                    intent.putExtra("pic1", pic1);
                    intent.putExtra("pic2", "null.png");
                    intent.putExtra("pic3", "null.png");
                    intent.putExtra("vn", vn);
                    intent.putExtra("imgCount", 1);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                    pic1 = currentPhotoPath;

            }

        }




    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Toast.makeText(this, "Q and Above", Toast.LENGTH_SHORT).show();
        }
        else{
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            Toast.makeText(this, "Below Q", Toast.LENGTH_SHORT).show();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.digitrafficinspectorv0",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


}