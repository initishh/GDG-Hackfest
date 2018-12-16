package com.example.initish.offlinedownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.myViewHolder> {

    Context context;
    List<Bitmap> list=new ArrayList<>();

    public NotesAdapter(Context context, List<Bitmap> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotesAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.img, viewGroup, false);

        return new NotesAdapter.myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        myViewHolder.imageView.setImageBitmap(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imageView);
        }
    }

}
