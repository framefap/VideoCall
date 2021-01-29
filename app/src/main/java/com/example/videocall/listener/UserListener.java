package com.example.videocall.listener;

import com.example.videocall.models.Users;

public interface UserListener {

    void initiateVideoMeeting(Users user);

    void initiateAudioMeeting(Users user);
}
