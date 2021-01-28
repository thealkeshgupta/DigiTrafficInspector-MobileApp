package com.example.digitrafficinspectorv0;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class scanDocument extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView,mTextView2;
    CameraSource mCameraSource;
    Button submitbtn,homeBtn;
    String pic1="",pic2="",pic3="",pic4="",turn="",dl="",ip="",rc="",fa="",la="",ba="",ra="",vn="";


    private static final String TAG = "scanDocument";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_document);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);
        mTextView2 = findViewById(R.id.text_view2);
        submitbtn = findViewById(R.id.submit_btn);
        homeBtn = findViewById(R.id.homeBtn);
        pic1 = getIntent().getStringExtra("pic1");
        pic2 = getIntent().getStringExtra("pic2");
        pic3 = getIntent().getStringExtra("pic3");
        pic4 = getIntent().getStringExtra("pic4");
        dl = getIntent().getStringExtra("dl");
        ip = getIntent().getStringExtra("ip");
        rc = getIntent().getStringExtra("rc");
        fa = getIntent().getStringExtra("fa");
        la = getIntent().getStringExtra("la");
        ba = getIntent().getStringExtra("ba");
        ra = getIntent().getStringExtra("ra");
        vn = getIntent().getStringExtra("vn");
        turn = getIntent().getStringExtra("turn");
        startCameraSource();
        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        mTextView.setTypeface(face);
        mTextView2.setTypeface(face);
        submitbtn.setTypeface(face);
        homeBtn.setTypeface(face);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),mTextView.getText(),Toast.LENGTH_LONG).show();
                if(turn.equals("5"))
                {
                    dl = mTextView.getText().toString();
                }
                if(turn.equals("6"))
                {
                    ip = mTextView.getText().toString();
                }
                if(turn.equals("7"))
                {
                    rc = mTextView.getText().toString();
                }
                Intent intent = new Intent(scanDocument.this, registerComplaintPage.class);
                intent.putExtra("pic1",pic1);
                intent.putExtra("pic2",pic2);
                intent.putExtra("pic3",pic3);
                intent.putExtra("pic4",pic4);
                intent.putExtra("dl",dl);
                intent.putExtra("ip",ip);
                intent.putExtra("rc",rc);
                intent.putExtra("fa", fa);
                intent.putExtra("la", la);
                intent.putExtra("ba", ba);
                intent.putExtra("ra", ra);
                intent.putExtra("vn", vn);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();


            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(scanDocument.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}