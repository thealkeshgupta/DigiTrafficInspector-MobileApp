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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class registerComplaintPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] users = {"Truck", "Private Car", "Taxi", "Govt. Car", "Motorcycle"};
    String timeStamp, plateValidity = "Valid", pic1, pic2, pic3, pic4, dl, ip, vn, rc, location = "N/A", fAvailable = "no", lAvailable = "no", bAvailable = "no", rAvailable = "no";
    StorageReference storageReference;
    double lat, lng;
    FirebaseFirestore db;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Button frontPic, leftPic, backPic, rightPic, submitBtn, drivingLicense, insurancePaper, rcPaper, checkNumber, homeBtn;
    ImageView simpleImageView;
    CheckBox checkBox, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    EditText vehicleNumberInput;
    TextView tv1, tv2;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver;
    LoadingDialogue loadingDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complaint_page);
        pic1 = getIntent().getStringExtra("pic1");
        pic2 = getIntent().getStringExtra("pic2");
        pic3 = getIntent().getStringExtra("pic3");
        pic4 = getIntent().getStringExtra("pic4");
        dl = getIntent().getStringExtra("dl");
        ip = getIntent().getStringExtra("ip");
        rc = getIntent().getStringExtra("rc");
        fAvailable = getIntent().getStringExtra("fa");
        lAvailable = getIntent().getStringExtra("la");
        bAvailable = getIntent().getStringExtra("ba");
        rAvailable = getIntent().getStringExtra("ra");
        vn = getIntent().getStringExtra("vn");
        frontPic = (Button) findViewById(R.id.frontPic);
        leftPic = (Button) findViewById(R.id.leftPic);
        backPic = (Button) findViewById(R.id.backPic);
        rightPic = (Button) findViewById(R.id.rightPic);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        drivingLicense = (Button) findViewById(R.id.drivingLicense);
        insurancePaper = (Button) findViewById(R.id.insurancePaper);
        rcPaper = (Button) findViewById(R.id.rcPaper);
        checkNumber = (Button) findViewById(R.id.checkNumber);
        homeBtn = (Button) findViewById(R.id.homeBtn);
        vehicleNumberInput = (EditText) findViewById(R.id.vehicleNumberInput);

        tv1 = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.txtVw);

        vehicleNumberInput.setText(vn);

        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        resultReceiver = new registerComplaintPage.AddressResultReceiver(new Handler());
        simpleImageView = (ImageView) findViewById(R.id.simpleImageView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        Typeface face = Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        frontPic.setTypeface(face);
        leftPic.setTypeface(face);
        backPic.setTypeface(face);
        rightPic.setTypeface(face);
        submitBtn.setTypeface(face);
        drivingLicense.setTypeface(face);
        insurancePaper.setTypeface(face);
        rcPaper.setTypeface(face);
        checkNumber.setTypeface(face);
        vehicleNumberInput.setTypeface(face);
        checkBox.setTypeface(face);
        checkBox2.setTypeface(face);
        checkBox3.setTypeface(face);
        checkBox4.setTypeface(face);
        checkBox5.setTypeface(face);
        checkBox6.setTypeface(face);
        submitBtn.setTypeface(face);
        homeBtn.setTypeface(face);
        tv1.setTypeface(face);
        tv2.setTypeface(face);

        loadingDialogue = new LoadingDialogue(registerComplaintPage.this);

        if (!pic1.equals("null.png")) {
            frontPic.setText("Front View Captured");
            frontPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!pic2.equals("null.png")) {
            leftPic.setText("Left View Captured");
            leftPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!pic3.equals("null.png")) {
            backPic.setText("Back View Captured");
            backPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!pic4.equals("null.png")) {
            rightPic.setText("Right View Captured");
            rightPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!dl.equals("null")) {
            drivingLicense.setText("Driving License Captured");
            drivingLicense.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!ip.equals("null")) {
            insurancePaper.setText("Insurance Paper Captured");
            insurancePaper.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!rc.equals("null")) {
            rcPaper.setText("Road Challan Captured");
            rcPaper.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!fAvailable.equals("no")) {
            frontPic.setText("Front View Available");
            frontPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!lAvailable.equals("no")) {
            leftPic.setText("Left View Available");
            leftPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!bAvailable.equals("no")) {
            backPic.setText("Back View Available");
            backPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }
        if (!rAvailable.equals("no")) {
            rightPic.setText("Right View Available");
            rightPic.setBackgroundResource(R.drawable.buttonavailabledesign);
        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();

            }
        });

        checkNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(registerComplaintPage.this, "Checking for Avaialable Images.", Toast.LENGTH_SHORT).show();
                db.collection("Complaints")
                        .whereEqualTo("VEHICLE_NUMBER", vehicleNumberInput.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        pic1 = document.getData().get("FRONT_VIEW").toString();
                                        pic2 = document.getData().get("LEFT_VIEW").toString();
                                        pic3 = document.getData().get("BACK_VIEW").toString();
                                        pic4 = document.getData().get("RIGHT_VIEW").toString();
                                        frontPic.setText("Take Vehicle Front Picture");
                                        leftPic.setText("Take Vehicle Left Picture");
                                        backPic.setText("Take Vehicle Back Picture");
                                        rightPic.setText("Take Vehicle Right Picture");
                                        if (!pic1.equals("null.png")) {
                                            fAvailable = "yes";
                                            frontPic.setText("Front View Available");
                                            frontPic.setBackgroundResource(R.drawable.buttonavailabledesign);
                                        }
                                        if (!pic2.equals("null.png")) {
                                            lAvailable = "yes";
                                            leftPic.setText("Left View Available");
                                            leftPic.setBackgroundResource(R.drawable.buttonavailabledesign);
                                        }
                                        if (!pic3.equals("null.png")) {
                                            bAvailable = "yes";
                                            backPic.setText("Back View Available");
                                            backPic.setBackgroundResource(R.drawable.buttonavailabledesign);
                                        }
                                        if (!pic4.equals("null.png")) {
                                            rAvailable = "yes";
                                            rightPic.setText("Right View Available");
                                            rightPic.setBackgroundResource(R.drawable.buttonavailabledesign);
                                        }
                                    }
                                } else {
//                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

//
            }
        });


        frontPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fAvailable.equals("no")) {
                    Toast.makeText(registerComplaintPage.this, "Image Already Available.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(registerComplaintPage.this, CapturePicture.class);
                    intent.putExtra("pic1", pic1);
                    intent.putExtra("pic2", pic2);
                    intent.putExtra("pic3", pic3);
                    intent.putExtra("pic4", pic4);
                    intent.putExtra("dl", dl);
                    intent.putExtra("ip", ip);
                    intent.putExtra("rc", rc);
                    intent.putExtra("fa", fAvailable);
                    intent.putExtra("la", lAvailable);
                    intent.putExtra("ba", bAvailable);
                    intent.putExtra("ra", rAvailable);
                    intent.putExtra("vn", vehicleNumberInput.getText().toString());
                    intent.putExtra("turn", "1");
                    startActivity(intent);
                    finish();
                }

            }
        });

        leftPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lAvailable.equals("no")) {
                    Toast.makeText(registerComplaintPage.this, "Image Already Available.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(registerComplaintPage.this, CapturePicture.class);
                    intent.putExtra("pic1", pic1);
                    intent.putExtra("pic2", pic2);
                    intent.putExtra("pic3", pic3);
                    intent.putExtra("pic4", pic4);
                    intent.putExtra("dl", dl);
                    intent.putExtra("ip", ip);
                    intent.putExtra("rc", rc);
                    intent.putExtra("fa", fAvailable);
                    intent.putExtra("la", lAvailable);
                    intent.putExtra("ba", bAvailable);
                    intent.putExtra("ra", rAvailable);
                    intent.putExtra("vn", vehicleNumberInput.getText().toString());
                    intent.putExtra("turn", "2");
                    startActivity(intent);
                    finish();
                }
            }
        });

        backPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bAvailable.equals("no")) {
                    Toast.makeText(registerComplaintPage.this, "Image Already Available.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(registerComplaintPage.this, CapturePicture.class);
                    intent.putExtra("pic1", pic1);
                    intent.putExtra("pic2", pic2);
                    intent.putExtra("pic3", pic3);
                    intent.putExtra("pic4", pic4);
                    intent.putExtra("dl", dl);
                    intent.putExtra("ip", ip);
                    intent.putExtra("rc", rc);
                    intent.putExtra("fa", fAvailable);
                    intent.putExtra("la", lAvailable);
                    intent.putExtra("ba", bAvailable);
                    intent.putExtra("ra", rAvailable);
                    intent.putExtra("vn", vehicleNumberInput.getText().toString());
                    intent.putExtra("turn", "3");
                    startActivity(intent);
                    finish();
                }
            }
        });

        rightPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rAvailable.equals("no")) {
                    Toast.makeText(registerComplaintPage.this, "Image Already Available.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(registerComplaintPage.this, CapturePicture.class);
                    intent.putExtra("pic1", pic1);
                    intent.putExtra("pic2", pic2);
                    intent.putExtra("pic3", pic3);
                    intent.putExtra("pic4", pic4);
                    intent.putExtra("dl", dl);
                    intent.putExtra("ip", ip);
                    intent.putExtra("rc", rc);
                    intent.putExtra("fa", fAvailable);
                    intent.putExtra("la", lAvailable);
                    intent.putExtra("ba", bAvailable);
                    intent.putExtra("ra", rAvailable);
                    intent.putExtra("vn", vehicleNumberInput.getText().toString());
                    intent.putExtra("turn", "4");
                    startActivity(intent);
                    finish();
                }
            }
        });

        drivingLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(registerComplaintPage.this, scanDocument.class);
                intent.putExtra("pic1", pic1);
                intent.putExtra("pic2", pic2);
                intent.putExtra("pic3", pic3);
                intent.putExtra("pic4", pic4);
                intent.putExtra("dl", dl);
                intent.putExtra("ip", ip);
                intent.putExtra("rc", rc);
                intent.putExtra("fa", fAvailable);
                intent.putExtra("la", lAvailable);
                intent.putExtra("ba", bAvailable);
                intent.putExtra("ra", rAvailable);
                intent.putExtra("vn", vehicleNumberInput.getText().toString());
                intent.putExtra("turn", "5");
                startActivity(intent);
                finish();

            }
        });

        insurancePaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(registerComplaintPage.this, scanDocument.class);
                intent.putExtra("pic1", pic1);
                intent.putExtra("pic2", pic2);
                intent.putExtra("pic3", pic3);
                intent.putExtra("pic4", pic4);
                intent.putExtra("dl", dl);
                intent.putExtra("ip", ip);
                intent.putExtra("rc", rc);
                intent.putExtra("fa", fAvailable);
                intent.putExtra("la", lAvailable);
                intent.putExtra("ba", bAvailable);
                intent.putExtra("ra", rAvailable);
                intent.putExtra("vn", vehicleNumberInput.getText().toString());
                intent.putExtra("turn", "6");
                startActivity(intent);
                finish();

            }
        });

        rcPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(registerComplaintPage.this, scanDocument.class);
                intent.putExtra("pic1", pic1);
                intent.putExtra("pic2", pic2);
                intent.putExtra("pic3", pic3);
                intent.putExtra("pic4", pic4);
                intent.putExtra("dl", dl);
                intent.putExtra("ip", ip);
                intent.putExtra("rc", rc);
                intent.putExtra("fa", fAvailable);
                intent.putExtra("la", lAvailable);
                intent.putExtra("ba", bAvailable);
                intent.putExtra("ra", rAvailable);
                intent.putExtra("vn", vehicleNumberInput.getText().toString());
                intent.putExtra("turn", "7");
                startActivity(intent);
                finish();

            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
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
                File f4 = new File(pic4);
                pic4 = f4.getName();
                Intent mediaScanIntent4 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri4 = Uri.fromFile(f4);
                mediaScanIntent4.setData(contentUri4);
                uploadImageToFirebase(f4.getName(), contentUri4);


                getCurrentLocation();

            }
        });

        // Spinner element
        Spinner spin = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if (users[position] == "Truck") {
            simpleImageView.setImageResource(R.drawable.truckplate);
        }
        if (users[position] == "Private Car") {
            simpleImageView.setImageResource(R.drawable.privatecar);
        }
        if (users[position] == "Taxi") {
            simpleImageView.setImageResource(R.drawable.taxiplate);
        }
        if (users[position] == "Govt. Car") {
            simpleImageView.setImageResource(R.drawable.govtcarplate);
        }
        if (users[position] == "Motorcycle") {
            simpleImageView.setImageResource(R.drawable.motorcycleplate);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
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

//                Toast.makeText(registerComplaintPage.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(registerComplaintPage.this, "Image Unavailable. Don't Worry! Still Process is Continuing...", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void addNewComplaint() {

        if (checkBox.isChecked()) {
            plateValidity = "Not Valid";
        }

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Map<String, Object> newCase = new HashMap<>();
        newCase.put("VEHICLE_NUMBER", vehicleNumberInput.getText().toString());
        newCase.put("DL", dl);
        newCase.put("RC", rc);
        newCase.put("IP", ip);
        newCase.put("FRONT_VIEW", pic1);
        newCase.put("LEFT_VIEW", pic2);
        newCase.put("BACK_VIEW", pic3);
        newCase.put("RIGHT_VIEW", pic4);
        newCase.put("UID", currentuser);
        newCase.put("LOCATION", location);
        newCase.put("LATITUDE", lat);
        newCase.put("LONGITUDE", lng);
        newCase.put("VALIDITY_OF_NUMBER_PLATE", plateValidity);
        newCase.put("DATE_TIME_STAMP", timeStamp);
        newCase.put("WRONG_SIDE_DRIVING", checkBox2.isChecked());
        newCase.put("USING_MOBILE_WHILE_DRIVING", checkBox3.isChecked());
        newCase.put("ILLEGAL_LANE_CHANGES", checkBox4.isChecked());
        newCase.put("CROSSED_RED_LIGHT", checkBox5.isChecked());
        newCase.put("DRIVING_WITHOUT_HELMET", checkBox6.isChecked());
        newCase.put("SOLVED", "no");
        newCase.put("SOLVED_BY", "none");
        newCase.put("SCORE", "Not Solved");
        db.collection("Complaints").document(timeStamp).set(newCase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(registerComplaintPage.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                        loadingDialogue.dismissDialogue();
                        startActivity(new Intent(registerComplaintPage.this, ProfileActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(registerComplaintPage.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        loadingDialogue.dismissDialogue();
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
        LocationServices.getFusedLocationProviderClient(registerComplaintPage.this).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(registerComplaintPage.this).removeLocationUpdates(this);
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

    private class AddressResultReceiver extends ResultReceiver{
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if(resultCode== Constants.SUCCESS_RESULT){
                Toast.makeText(registerComplaintPage.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_LONG).show();
                location = resultData.getString(Constants.RESULT_DATA_KEY);
                addNewComplaint();
            }else{
                Toast.makeText(registerComplaintPage.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }

        }
    }

}
