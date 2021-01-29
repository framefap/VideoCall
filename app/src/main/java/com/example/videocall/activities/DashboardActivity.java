package com.example.videocall.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.videocall.R;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    EditText txtcode;
    Button btnjoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtcode = findViewById(R.id.txtcode);
        btnjoin = findViewById(R.id.btnjoin);

        URL serverurl;

        try {
            serverurl = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverurl)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(txtcode.getText().toString())
                        .setWelcomePageEnabled(false)
                        .build();

                JitsiMeetActivity.launch(DashboardActivity.this,options);
            }
        });
    }
}