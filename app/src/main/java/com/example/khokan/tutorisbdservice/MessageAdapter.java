package com.example.khokan.tutorisbdservice;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by USER on 10/3/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView sendMessageText, receiveMessageText;
        CircleImageView receiverProfileImage;

        MessageViewHolder(View itemView) {
            super(itemView);

            sendMessageText = itemView.findViewById(R.id.sender_message_text);
            receiveMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (fromMessageType.equals("text"))
        {
            messageViewHolder.receiveMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.sendMessageText.setVisibility(View.INVISIBLE);
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.sendMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.sendMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                messageViewHolder.sendMessageText.setText(messages.getMessage());
                messageViewHolder.sendMessageText.setTextColor(Color.BLACK);
            }else
            {

                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiveMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.receiveMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                messageViewHolder.receiveMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiveMessageText.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


}
