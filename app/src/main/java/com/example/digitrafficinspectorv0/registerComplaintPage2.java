package com.example.digitrafficinspectorv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class registerComplaintPage2 extends AppCompatActivity {
    String pic1 = "", vn = "", pic2 = "", pic3 = "", timeStamp = "", location = "", imei = "";
    int imgCount;
    Button morePic, submitReport, homeBtn;
    StorageReference storageReference;
    FirebaseFirestore db;
    double lat, lng;
    TelephonyManager tm;
    TextView textTitle;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ImageView simpleImageView;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    EditText moreDetails;
    LoadingDialogue loadingDialogue;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complaint_page2);
        textTitle = (TextView) findViewById(R.id.textTitle);
        morePic = (Button) findViewById(R.id.morePic);
        homeBtn = (Button) findViewById(R.id.homeBtn);
        submitReport = (Button) findViewById(R.id.submitReport);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        moreDetails = (EditText) findViewById(R.id.moreDetails);
        Typeface face = Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        textTitle.setTypeface(face);
        morePic.setTypeface(face);
        submitReport.setTypeface(face);
        checkBox1.setTypeface(face);
        checkBox2.setTypeface(face);
        checkBox3.setTypeface(face);
        checkBox4.setTypeface(face);
        checkBox5.setTypeface(face);
        homeBtn.setTypeface(face);
        moreDetails.setTypeface(face);
        pic1 = getIntent().getStringExtra("pic1");
        pic2 = getIntent().getStringExtra("pic2");
        pic3 = getIntent().getStringExtra("pic3");
        vn = getIntent().getStringExtra("vn");
        imgCount = getIntent().getIntExtra("imgCount", 0);
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        resultReceiver = new registerComplaintPage2.AddressResultReceiver(new Handler());
        simpleImageView = (ImageView) findViewById(R.id.simpleImageView);
        loadingDialogue = new LoadingDialogue(registerComplaintPage2.this);


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ProfileActivity2.class));
                finish();
            }
        });
        morePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgCount < 3) {
                    Intent intent = new Intent(registerComplaintPage2.this, CapturePicture2.class);
                    intent.putExtra("pic1", pic1);
                    intent.putExtra("pic2", pic2);
                    intent.putExtra("pic3", pic3);
                    intent.putExtra("vn", vn);
                    intent.putExtra("imgCount", imgCount);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(registerComplaintPage2.this, "Max. 3 images can only be captured", Toast.LENGTH_LONG).show();
                }

            }
        });


        submitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialogue.startLoadingDialogue();
                File f = new File(pic1);
                pic1 = f.getName();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                uploadImageToFirebase(f.getName(), contentUri);
                File f2 = new File(pic2);
                pic2 = f2.getName();
                Intent mediaScanIntent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri2 = Uri.fromFile(f2);
                mediaScanIntent2.setData(contentUri2);
                uploadImageToFirebase(f2.getName(), contentUri2);
                File f3 = new File(pic3);
                pic3 = f3.getName();
                Intent mediaScanIntent3 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri3 = Uri.fromFile(f3);
                mediaScanIntent3.setData(contentUri3);
                uploadImageToFirebase(f3.getName(), contentUri3);
                getCurrentLocation();

            }
        });


    }


    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });

//                Toast.makeText(registerComplaintPage2.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(registerComplaintPage2.this, "Image Unavailable.Don't Worry Still Process is Continuing...", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void addNewComplaint() {

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Map<String, Object> newCase = new HashMap<>();
        newCase.put("VEHICLE_NUMBER", vn);
        newCase.put("PICTURE_1", pic1);
        newCase.put("PICTURE_2", pic2);
        newCase.put("PICTURE_3", pic3);
        newCase.put("UID", currentuser);
        newCase.put("LOCATION", location);
        newCase.put("LATITUDE", lat);
        newCase.put("LONGITUDE", lng);
        newCase.put("DATE_TIME_STAMP", timeStamp);
        newCase.put("VIOLATION_1", checkBox1.isChecked());
        newCase.put("VIOLATION_2", checkBox2.isChecked());
        newCase.put("VIOLATION_3", checkBox3.isChecked());
        newCase.put("VIOLATION_4", checkBox4.isChecked());
        newCase.put("VIOLATION_5", checkBox5.isChecked());
        newCase.put("SOLVED", "no");
        newCase.put("SOLVED_BY", "none");
        newCase.put("SCORE", "Not Solved");
        newCase.put("MORE_DETAILS", moreDetails.getText().toString());
        db.collection("Complaints2").document(timeStamp).set(newCase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialogue.dismissDialogue();
                        Toast.makeText(registerComplaintPage2.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(registerComplaintPage2.this, ProfileActivity2.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialogue.dismissDialogue();
                        Toast.makeText(registerComplaintPage2.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }

    private void getCurrentLocation() {


        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(registerComplaintPage2.this).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(registerComplaintPage2.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                    lat = latitude;
                    lng = longitude;
                    Location location = new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    fetchAddressFromLatLong(location);

                } else {

                }

            }


        }, Looper.getMainLooper());


    }

    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(this,fetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode== Constants.SUCCESS_RESULT){
                location = resultData.getString(Constants.RESULT_DATA_KEY);
                addNewComplaint();
            }else{

            }

        }
    }





}
