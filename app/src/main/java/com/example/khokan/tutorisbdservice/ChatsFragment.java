package com.example.khokan.tutorisbdservice;


import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private View privateChatView;
    private RecyclerView chatList;
    private DatabaseReference chatRef,usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView = inflater.inflate(R.layout.fragment_chats, container, false);

        chatList = privateChatView.findViewById(R.id.chats_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        chatRef.keepSynced(true);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.keepSynced(true);
        return privateChatView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model)
                    {

                        final  String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};
                        usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if (dataSnapshot.hasChild("image"))
                                    {
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
//                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                        Picasso.get().load(retImage[0]).networkPolicy(NetworkPolicy.OFFLINE)
                                                .into(holder.profileImage, new Callback() {
                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                                    }
                                                });
                                    }

                                    final String reName = dataSnapshot.child("name").getValue().toString();
                                    final String reStatus = dataSnapshot.child("status").getValue().toString();
                                    final String reProfession = dataSnapshot.child("profession").getValue().toString();
                                    if (dataSnapshot.hasChild("online")) {
                                        Boolean onlineStatus = (Boolean) dataSnapshot.child("online").getValue();
                                        holder.onlineStatus(onlineStatus);
                                    }
                                    holder.userName.setText(reName);
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent  = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id",usersIDs);
                                            chatIntent.putExtra("visit_user_name",reName);
                                            chatIntent.putExtra("visit_image", retImage[0]);

                                            startActivity(chatIntent);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup, false);
                        return new ChatViewHolder(view);
                    }
                };

        chatList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        CircleImageView profileImage;
        TextView userStatus, userName;
        ImageView online_icon;

        public ChatViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
        public void onlineStatus(boolean online_status){
            online_icon = itemView.findViewById(R.id.user_online_status);
            if (online_status)
            {
                online_icon.setVisibility(View.VISIBLE);
                userStatus.setText("Online");
                userStatus.setTextColor(Color.GREEN);
            }else
            {
                online_icon.setVisibility(View.INVISIBLE);
                userStatus.setText("Offline");
                userStatus.setTextColor(Color.RED);
            }
        }
    }


}
