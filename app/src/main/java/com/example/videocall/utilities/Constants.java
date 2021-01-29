package com.example.videocall.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "Users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "IsSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static HashMap<String, String> getRemoteMessageHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAA-CB1HHE:APA91bG8GpbpgKw_aDh-eUpSq1HMCONcNebOpj0A6AU18zhy4-JAEtwZmETtvzibXvbT6vnwALjLyYo4C5RG3KC2wdQTZY2b-VOwfGCejhqbV6X2tYEAUJc8gVW208HQP9BLvlRkD3jc"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }
}
