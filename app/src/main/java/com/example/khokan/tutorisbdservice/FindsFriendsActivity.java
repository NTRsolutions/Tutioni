package com.example.khokan.tutorisbdservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindsFriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView findndFriendsRecylerList;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);
        setContentView(R.layout.activity_finds_friends);
        findndFriendsRecylerList = findViewById(R.id.find_friends_recycler_list);
        findndFriendsRecylerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.find_friends_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userRef.child(currentUser.getUid()).child("online").setValue(true);

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRef,Contacts.class)
                .build();

        super.onStart();
        FirebaseRecyclerAdapter<Contacts,findfriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, findfriendViewHolder>(options) {
                    @NonNull
                    @Override
                    public findfriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        return new findfriendViewHolder(view);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull final findfriendViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText("("+model.getProfession()+")");
                        holder.private_tutor.setText("Private Tutor : " +model.getPrivate_tutors());
//                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
                        Picasso.get().load(model.getImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image)
                                .into(holder.profileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                    }
                                });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();
                                Intent profileIntent = new Intent(FindsFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id",visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }
                };

        findndFriendsRecylerList.setAdapter(adapter);
        adapter.startListening();
    }

//    view holder class
    public static class findfriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus, private_tutor;
        CircleImageView profileImage;
        public findfriendViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            private_tutor = itemView.findViewById(R.id.user_profile_private_tutor);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

}
