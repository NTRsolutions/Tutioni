package com.example.khokan.tutorisbdservice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by USER on 10/7/2018.
 */

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.ViewHolder> {
    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();


    public void update(String name, String url)
    {
        items.add(name);
        urls.add(url);
        notifyDataSetChanged();
    }
    public MyBooksAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // For create views for recycler view
        View view = LayoutInflater.from(context).inflate(R.layout.books_list_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // initialise the elements of individual items;
        holder.books_name.setText("Book Name: "+items.get(position)+".pdf");

    }

    @Override
    public int getItemCount() {
        //return the number of items
        return items.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder
    {
        TextView books_name, total_books;

        public ViewHolder(View itemView) {
            super(itemView);

            books_name = itemView.findViewById(R.id.books_name_cardview);
            itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                int position = recyclerView.getChildLayoutPosition(view);
                                                Intent intent = new Intent();
                                                intent.setType(Intent.ACTION_VIEW);// we are going to view something;
                                                intent.setData(Uri.parse(urls.get(position)));
                                                context.startActivity(intent);
                                            }
                                        }
            );
        }
    }
}
