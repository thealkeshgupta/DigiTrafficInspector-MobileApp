package com.example.digitrafficinspectorv0;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView helloUser,userScore,welcomeText;
    Button rc,sh,logout;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        helloUser = findViewById(R.id.textView);
        userScore = findViewById(R.id.textView3);
        welcomeText = findViewById(R.id.textView2);
        rc = findViewById(R.id.registerComplaint);
        sh = findViewById(R.id.showHistory);
        logout = findViewById(R.id.buttonLogout);
        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        helloUser.setTypeface(face);
        userScore.setTypeface(face);
        welcomeText.setTypeface(face);
        rc.setTypeface(face);
        sh.setTypeface(face);
        logout.setTypeface(face);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("USERDB").document(""+FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String UserName = document.getData().get("NAME").toString();
                        helloUser.setText("Hello "+UserName);
                        String UserScore = document.getData().get("TOTAL_SCORE").toString();
                        userScore.setText("Your Current Score : "+UserScore);
                    }
                }
            }
        });

        findViewById(R.id.registerComplaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runtimePermissions();
            }
        });



        findViewById(R.id.showHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ComplaintHistoryPage2.class);
                intent.putExtra("collectionName", "Complaints");
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(ProfileActivity.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });


    }


    private void runtimePermissions(){
        Toast.makeText(ProfileActivity.this, "Grant All Permissions First!", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(ProfileActivity.this, registerComplaintPage.class);
                    intent.putExtra("pic1","null.png");
                    intent.putExtra("pic2","null.png");
                    intent.putExtra("pic3","null.png");
                    intent.putExtra("pic4","null.png");
                    intent.putExtra("dl","null");
                    intent.putExtra("ip","null");
                    intent.putExtra("rc","null");
                    intent.putExtra("fa", "no");
                    intent.putExtra("la", "no");
                    intent.putExtra("ba", "no");
                    intent.putExtra("ra", "no");
                    intent.putExtra("vn", "");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(ProfileActivity.this, "You Must Grant All Permissions For This!", Toast.LENGTH_LONG).show();
                }


            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();

    }
}