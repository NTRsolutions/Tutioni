package com.example.khokan.tutorisbdservice;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView myRequestList;
    private DatabaseReference chatRequestRef,userRef,contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);

        mAuth =FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef.keepSynced(true);
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        chatRequestRef.keepSynced(true);
        myRequestList = requestFragmentView.findViewById(R.id.chat_request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserID), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model) {

                        final String list_user_id= getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())
                                {
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received"))
                                    {
                                        holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);
                                            userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {
                                                    if (dataSnapshot.hasChild("image"))
                                                    {
                                                        final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

//                                                        Picasso.get().load(requestProfileImage).into(holder.profileImage);

                                                        Picasso.get().load(requestProfileImage).networkPolicy(NetworkPolicy.OFFLINE)
                                                                .into(holder.profileImage, new Callback() {
                                                                    @Override
                                                                    public void onSuccess() {

                                                                    }

                                                                    @Override
                                                                    public void onError(Exception e) {
                                                                        Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                                    }
                                                                });
                                                    }

                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText("whant to connect with you!");

                                                    holder.itemView.setOnClickListener(new View.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(View v)
                                                        {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Accept",
                                                                            "Cancel"
                                                                    };

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle(requestUserName + " Chat Request");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                    if (i==0)
                                                                    {
                                                                        contactsRef.child(currentUserID).child(list_user_id).child("Contact")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    contactsRef.child(list_user_id).child(currentUserID).child("Contact")
                                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                chatRequestRef.child(currentUserID).child(list_user_id)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful())
                                                                                                                {
                                                                                                                    chatRequestRef.child(list_user_id).child(currentUserID)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful())
                                                                                                                                    {
                                                                                                                                        Toast.makeText(getContext(), "New Contact is added!", Toast.LENGTH_SHORT).show();
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    if (i==1)
                                                                    {
                                                                        chatRequestRef.child(currentUserID).child(list_user_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            chatRequestRef.child(list_user_id).child(currentUserID)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful())
                                                                                                            {

                                                                                                                Toast.makeText(getContext(), "You Canceled The Request", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });

                                                            builder.show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                    }else{

                                        /*holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.INVISIBLE);
                                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);
                                        holder.userName.setVisibility(View.INVISIBLE);
                                        holder.userStatus.setVisibility(View.INVISIBLE);
                                        holder.profileImage.setVisibility(View.INVISIBLE);
                                        */
                                        holder.displayProfileLayout.setVisibility(View.GONE);
                                    }



                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup,false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;
                    }
                };

        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class  RequestViewHolder extends  RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button accessButton, cancelButton;
        CardView displayProfileLayout;

        public RequestViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);

            accessButton = itemView.findViewById(R.id.request_accept_button);
            cancelButton = itemView.findViewById(R.id.request_cancel_button);
            displayProfileLayout = itemView.findViewById(R.id.display_profile_layout);

        }
    }
}
