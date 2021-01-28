package com.example.digitrafficinspectorv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnResetPassword,homeBtn;
    private FirebaseAuth mAuth;
    private TextView heading,heading2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtEmail = (EditText) findViewById(R.id.edt_reset_email);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        homeBtn = (Button) findViewById(R.id.homeBtn);
        mAuth = FirebaseAuth.getInstance();
        heading = (TextView) findViewById(R.id.heading);
        heading2 = (TextView) findViewById(R.id.heading2);
        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        heading.setTypeface(face);
        heading2.setTypeface(face);
        edtEmail.setTypeface(face);
        btnResetPassword.setTypeface(face);



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}