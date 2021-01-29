package com.example.videocall.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.fonts.FontFamily;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.videocall.R;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore database;

    EditText txtname, txtemail, txtpwd;
    Button btnsignup, btnlogin, btncall;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        txtname = findViewById(R.id.txtname);
        txtemail = findViewById(R.id.txtemail);
        txtpwd = findViewById(R.id.txtpwd);

        btnsignup = findViewById(R.id.btnsignup);
        btnlogin = findViewById(R.id.btnlogin);
        btncall = findViewById(R.id.btncall);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_NAME, txtname.getText().toString());
                user.put(Constants.KEY_EMAIL, txtemail.getText().toString());
                user.put(Constants.KEY_PASSWORD, txtpwd.getText().toString());

                database.collection(Constants.KEY_COLLECTION_USERS)
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                                preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                                preferenceManager.putString(Constants.KEY_NAME,txtname.getText().toString());
                                preferenceManager.putString(Constants.KEY_EMAIL,txtemail.getText().toString());
                                preferenceManager.putString(Constants.KEY_PASSWORD,txtpwd.getText().toString());

                                Toast.makeText(MainActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();

                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });



                //old code
                /*String name,email,pwd;
                name = txtname.getText().toString();
                email = txtemail.getText().toString();
                pwd = txtpwd.getText().toString();

                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPass(pwd);

                auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            database.collection("Users")
                                    .document().set(user);

                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.collection(Constants.KEY_COLLECTION_USERS)
                        .whereEqualTo(Constants.KEY_EMAIL,txtemail.getText().toString())
                        .whereEqualTo(Constants.KEY_PASSWORD,txtpwd.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                    preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                                    preferenceManager.putString(Constants.KEY_EMAIL,documentSnapshot.getString(Constants.KEY_EMAIL));

                                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this, "Login OK!",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Login failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                //old code
                /*String email,pwd;
                email = txtemail.getText().toString();
                pwd = txtpwd.getText().toString();

                auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "login success", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
            }
        });

        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });




    }

}