package com.example.videocall.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videocall.R;
import com.example.videocall.listener.UserListener;
import com.example.videocall.models.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<Users> users;
    private UserListener usersListener;

    public UsersAdapter(List<Users> users, UserListener usersListener) {
        this.users = users;
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView textView,tvusername,tvemail;
        ImageView imageAudioMeeting,imageVideoMeeting;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            tvusername = itemView.findViewById(R.id.tvusername);
            tvemail = itemView.findViewById(R.id.tvemail);
            imageAudioMeeting = itemView.findViewById(R.id.imageAudioMeeting);
            imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);
        }

        void setUserData (Users user){
            textView.setText(user.name.substring(0,1));
            tvusername.setText(user.name);
            tvemail.setText(user.email);
            imageAudioMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    usersListener.initiateAudioMeeting(user);
                }
            });
            imageVideoMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    usersListener.initiateVideoMeeting(user);
                }
            });
        }
    }
}
