package com.example.videocall.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videocall.R;
import com.example.videocall.network.ApiClient;
import com.example.videocall.network.ApiService;
import com.example.videocall.utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingActivity extends AppCompatActivity {

    TextView textname,textemail;
    ImageView imageAcceptInvitation, imageRejectInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);

        textname = findViewById(R.id.textname);
        textemail = findViewById(R.id.textemail);

        //String name = getIntent().getStringExtra(Constants.KEY_NAME);
        textname.setText(getIntent().getStringExtra(Constants.KEY_NAME));
        textemail.setText(getIntent().getStringExtra(Constants.KEY_EMAIL));

        imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
        imageRejectInvitation = findViewById(R.id.imageRejectInvitation);

        imageAcceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
            }
        });

        imageRejectInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_REJECTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
            }
        });


    }

    private void sendInvitationResponse(String type, String receiverToken){
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);

        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){

                        try {

                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions conferenceOptions =
                                    new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverURL)
                                    .setWelcomePageEnabled(false)
                                    .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                                    .build();
                            JitsiMeetActivity.launch(IncomingActivity.this, conferenceOptions);
                            finish();

                        } catch (Exception exception){
                            Toast.makeText(IncomingActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } else{
                        Toast.makeText(IncomingActivity.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else{
                    Toast.makeText(IncomingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t){
                Toast.makeText(IncomingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null){
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)){
                    Toast.makeText(context, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}