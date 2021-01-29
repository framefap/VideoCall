package com.example.videocall.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videocall.R;
import com.example.videocall.adapters.UsersAdapter;
import com.example.videocall.listener.UserListener;
import com.example.videocall.models.Users;
import com.example.videocall.utilities.Constants;
import com.example.videocall.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements UserListener {

    PreferenceManager preferenceManager;
    TextView txtusername,txtsignout;

    FirebaseFirestore database;
    DocumentReference documentReference;

    RecyclerView usersRecyclerView;
    List<Users> users;
    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        preferenceManager = new PreferenceManager(getApplicationContext());

        txtusername = findViewById(R.id.txtusername);
        txtsignout = findViewById(R.id.txtsignout);

        txtusername.setText("USER: " + preferenceManager.getString(Constants.KEY_NAME));

        txtsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signout();
            }
        });
        
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    sendFCMTokenToDatabase(task.getResult().getToken());
                }
            }
        });

        usersRecyclerView = findViewById(R.id.usersRecyclerView);

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users,this);
        usersRecyclerView.setAdapter(usersAdapter);

        getUsers();
    }

    private void getUsers(){
        database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null){
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                if(myUserId.equals(documentSnapshot.getId())){
                                    continue;
                                    //display the user list except for currently signed-in user
                                    //exclude meeting with yourself
                                }
                                Users user = new Users();
                                user.name = documentSnapshot.getString(Constants.KEY_NAME);
                                user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                                user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                users.add(user);

                            }
                            if (users.size()>0){
                                usersAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Toast.makeText(HomePageActivity.this, "BOO", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void sendFCMTokenToDatabase(String token){
        database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePageActivity.this, "Unable to send token " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void Signout(){
        database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        preferenceManager.clearPreferences();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePageActivity.this, "Sign out failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void initiateVideoMeeting(Users user) {
        if(user.token == null || user.token.trim().isEmpty()){
            Toast.makeText(
                    this,user.name + " is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,"Video meeting with " + user.name,
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(getApplicationContext(), OutgoingActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type","video");
            startActivity(intent);
        }

    }

    @Override
    public void initiateAudioMeeting(Users user) {
        if(user.token == null || user.token.trim().isEmpty()){
            Toast.makeText(
                    this,user.name + " is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,"Audio meeting with " + user.name,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}