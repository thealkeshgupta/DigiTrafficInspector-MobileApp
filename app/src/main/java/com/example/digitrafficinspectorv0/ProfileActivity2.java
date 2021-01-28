package com.example.digitrafficinspectorv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ProfileActivity2 extends AppCompatActivity {
    TextView helloUser,userScore,welcomeText;
    FirebaseFirestore db;
    ImageView imageView;
    String profilePicSrc;
    Button rc,logout,sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        imageView = findViewById(R.id.imageView2);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        profilePicSrc = ""+account.getPhotoUrl();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        requestOptions.error(R.drawable.ic_launcher_background);
        Glide.with(ProfileActivity2.this)
                .load(profilePicSrc)
                .into(imageView);

        helloUser = findViewById(R.id.textView);
        userScore = findViewById(R.id.textView3);
        welcomeText = findViewById(R.id.textView2);
        rc = findViewById(R.id.btnCaptureViolation);
        sh = findViewById(R.id.btnComplaintHistory);
        logout = findViewById(R.id.btnLogout);
        db = FirebaseFirestore.getInstance();

        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        helloUser.setTypeface(face);
        userScore.setTypeface(face);
        welcomeText.setTypeface(face);
        rc.setTypeface(face);
        sh.setTypeface(face);
        logout.setTypeface(face);


        findViewById(R.id.btnCaptureViolation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runtimePermissions();
            }
        });

        DocumentReference docRef = db.collection("USERDB").document(""+FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   if (task.isSuccessful()) {
                                                       DocumentSnapshot document = task.getResult();
                                                       if (document.exists()) {
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                           String UserName = document.getData().get("NAME").toString();
                                                           String UserScore = document.getData().get("TOTAL_SCORE").toString();
                                                           helloUser.setText("Hello "+UserName);
                                                           userScore.setText("Your Current Score :"+UserScore);
                                                       }
                                                   }
                                               }
                                           });

        findViewById(R.id.btnComplaintHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity2.this, ComplaintHistoryPage2.class);
                intent.putExtra("collectionName", "Complaints2");
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(ProfileActivity2.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });
    }


    private void runtimePermissions(){
        Toast.makeText(ProfileActivity2.this, "Grant All Permissions First!", Toast.LENGTH_SHORT).show();
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    startActivity(new Intent(getApplicationContext(),CaptureViolation.class));
                    finish();
                }
                else {
                    Toast.makeText(ProfileActivity2.this, "You Must Grant All Permissions For This!", Toast.LENGTH_LONG).show();
                }


            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();

    }



}
