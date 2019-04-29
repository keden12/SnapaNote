package com.example.snapanote.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snapanote.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    ArrayList<String> titles,descriptions,urls,imgurls;
    Context parentContext;

    public MainAdapter(ArrayList<String> articleTitles, ArrayList<String> articleDescriptions, ArrayList<String> articleUrls, ArrayList<String> imageUrls)
    {
        titles = articleTitles;
        descriptions = articleDescriptions;
        urls = articleUrls;
        imgurls = imageUrls;
    }
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles,parent,false);
        parentContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, final int position) {
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position));
        Glide.with(parentContext).load(imgurls.get(position)).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position)));
                parentContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView recycler;
        public TextView title,description;
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            description = itemView.findViewById(R.id.articleDescription);
            image = itemView.findViewById(R.id.articleImage);
            recycler = itemView.findViewById(R.id.articleRecyclerView);

        }
    }
}
