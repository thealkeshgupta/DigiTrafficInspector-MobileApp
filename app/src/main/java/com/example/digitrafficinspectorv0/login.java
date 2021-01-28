package com.example.digitrafficinspectorv0;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {



    public EditText loginEmailId, logInpasswd;
    Button btnLogIn, btnForgotPassword;
    SignInButton btnSignIn;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    TextView tv1,tv2,tv3;
    FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener authStateListener;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.loginEmail);
        logInpasswd = findViewById(R.id.loginpaswd);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnSignIn = findViewById(R.id.signInGoogle);
        tv1 = findViewById(R.id.textViewTitle);
        tv2 = findViewById(R.id.textView);
        tv3 = findViewById(R.id.textView2);
        db = FirebaseFirestore.getInstance();

        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        tv1.setTypeface(face);
        tv2.setTypeface(face);
        tv3.setTypeface(face);
        loginEmailId.setTypeface(face);
        logInpasswd.setTypeface(face);
        btnForgotPassword.setTypeface(face);
        btnLogIn.setTypeface(face);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        final LoadingDialogue loadingDialogue = new LoadingDialogue(login.this);
        loadingDialogue.startLoadingDialogue();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    Toast.makeText(login.this, "This User is : " + FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

                    DocumentReference docRef = db.collection("USERDB").document("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    String UserType = document.getData().get("USER_TYPE").toString();
                                    loadingDialogue.dismissDialogue();
                                    if (UserType.equals("Public")) {
                                        startActivity(new Intent(login.this, ProfileActivity2.class));
                                        finish();
                                    } else if (UserType.equals("Traffic_Inspector")) {
                                        startActivity(new Intent(login.this, ProfileActivity.class));
                                        finish();
                                    } else if (UserType.equals("Main_Authority")) {
                                        Toast.makeText(login.this, "User Not Authorised", Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    loadingDialogue.dismissDialogue();
                                    Toast.makeText(login.this, "Login to continue", Toast.LENGTH_SHORT).show();
                                }
                            } else {
//                                            Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });


                } else {
                    loadingDialogue.dismissDialogue();
                    Toast.makeText(login.this, "Login to continue", Toast.LENGTH_SHORT).show();
                }
            }
        };


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));

            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = loginEmailId.getText().toString();
                String userPaswd = logInpasswd.getText().toString();
                if (userEmail.isEmpty()) {
                    loginEmailId.setError("Provide your Email first!");
                    loginEmailId.requestFocus();
                } else if (userPaswd.isEmpty()) {
                    logInpasswd.setError("Enter Password!");
                    logInpasswd.requestFocus();
                } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                    Toast.makeText(login.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                    loadingDialogue.startLoadingDialogue();
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(login.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Not sucessfull", Toast.LENGTH_SHORT).show();
                            } else {

                                DocumentReference docRef = db.collection("USERDB").document("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                String UserType = document.getData().get("USER_TYPE").toString();

                                                loadingDialogue.dismissDialogue();
                                                if (UserType.equals("Public")) {
                                                    startActivity(new Intent(login.this, ProfileActivity2.class));
                                                } else if (UserType.equals("Traffic_Inspector")) {
                                                    startActivity(new Intent(login.this, ProfileActivity.class));
                                                } else if (UserType.equals("Main_Authority")) {
                                                    Toast.makeText(login.this, "User Not Authorised", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(login.this, login.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                    startActivity(intent);
                                                }
                                            } else {
                                                loadingDialogue.dismissDialogue();
//                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            loadingDialogue.dismissDialogue();
//                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount acc = completeTask.getResult(ApiException.class);
            Toast.makeText(login.this, "Signed In Successfully!", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);

        } catch (ApiException e) {
            Toast.makeText(login.this, "Sign In Unsuccessful!", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    DocumentReference docRef = db.collection("USERDB").document("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    String UserType = document.getData().get("USER_TYPE").toString();
                                    if (UserType.equals("Public")) {
                                        Toast.makeText(login.this, "Loggin in to your Account", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(login.this, ProfileActivity2.class));

                                        finish();
                                    } else if (UserType.equals("Traffic_Inspector")) {
                                        startActivity(new Intent(login.this, ProfileActivity.class));
                                    } else if (UserType.equals("Main_Authority")) {
                                        Toast.makeText(login.this, "User Not Authorised", Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    Toast.makeText(login.this, "Creating Account", Toast.LENGTH_SHORT).show();
                                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    Map<String, Object> newCase = new HashMap<>();
                                    newCase.put("USERID", currentuser);
                                    newCase.put("USER_TYPE", "Public");
                                    newCase.put("NAME", account.getDisplayName());
                                    newCase.put("TOTAL_SCORE", "0");
                                    newCase.put("PROFILE_PIC", "" + account.getPhotoUrl());
                                    db.collection("USERDB").document(currentuser).set(newCase)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(login.this, "Registration Successful",
                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(login.this, ProfileActivity2.class));

                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(login.this, "ERROR" + e.toString(),
                                                            Toast.LENGTH_SHORT).show();
                                                    Log.d("TAG", e.toString());
                                                }
                                            });
                                }


                            } else {
                                Toast.makeText(login.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });
    }
}