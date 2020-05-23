package me.maxdev.popularmoviesapp.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.model.MovieData;
import me.maxdev.popularmoviesapp.ui.grid.MovieGridItemViewHolder;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private ArrayList<MovieData> listdata;
    private Context context;
    // RecyclerView recyclerView;
    public MyListAdapter(Context context, ArrayList<MovieData> listdata) {
        this.listdata = listdata;
       this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MovieData movie = listdata.get(position);
        Log.e("DATA_SIZE",""+listdata.size());
      //  holder.moviePoster.setContentDescription(movie.getCategory_name());
        String movies_name=movie.getContent_poster_image();
        viewHolder.tv_title.setText(movie.getContent_name());
        String uri="@drawable/";

        int imageResource;

        Drawable res;
        if(movie.getContent_poster_image().equals("poster1.jpg")){
            imageResource=context.getResources().getIdentifier(uri+"poster1",null,context.getPackageName());
            res=context.getResources().getDrawable(imageResource);
            viewHolder.image_poster.setImageDrawable(res);
        }
        if(movie.getContent_poster_image().equals("poster2.jpg")){
            imageResource=context.getResources().getIdentifier(uri+"poster2",null,context.getPackageName());
            res=context.getResources().getDrawable(imageResource);
            viewHolder.image_poster.setImageDrawable(res);
        }if(movie.getContent_poster_image().equals("poster3.jpg")){
            imageResource=context.getResources().getIdentifier(uri+"poster3",null,context.getPackageName());
            res=context.getResources().getDrawable(imageResource);
            viewHolder.image_poster.setImageDrawable(res);
        }
        if(movie.getContent_poster_image().equals("poster4.jpg")){
            imageResource=context.getResources().getIdentifier(uri+"poster4",null,context.getPackageName());
            res=context.getResources().getDrawable(imageResource);
            viewHolder.image_poster.setImageDrawable(res);
        }if(movie.getContent_poster_image().equals("poster5.jpg")){
            imageResource=context.getResources().getIdentifier(uri+"poster5",null,context.getPackageName());
            res=context.getResources().getDrawable(imageResource);
            viewHolder.image_poster.setImageDrawable(res);
        }
/*



        switch(movies_name){
            case "poster1.jpg":
                imageResource=context.getResources().getIdentifier(uri+"poster1",null,context.getPackageName());
                res=context.getResources().getDrawable(imageResource);
                loadMovie(res,viewHolder);
                break;
            case "poster2.jpg":
                imageResource=context.getResources().getIdentifier(uri+"poster2",null,context.getPackageName());
                res=context.getResources().getDrawable(imageResource);
                loadMovie(res,viewHolder);
                break;
            case "poster3.jpg":
                imageResource=context.getResources().getIdentifier(uri+"poster3",null,context.getPackageName());
                res=context.getResources().getDrawable(imageResource);
                loadMovie(res,viewHolder);
                break;
            case "poster4.jpg":
                imageResource=context.getResources().getIdentifier(uri+"poster4",null,context.getPackageName());
                res=context.getResources().getDrawable(imageResource);
                loadMovie(res,viewHolder);
                break;
            case "poster5.jpg":
                imageResource=context.getResources().getIdentifier(uri+"poster5",null,context.getPackageName());
                res=context.getResources().getDrawable(imageResource);
                loadMovie(res,viewHolder);
                break;

        }*/

    }
    public void loadMovie(Drawable movie_name, ViewHolder viewHolder){

        viewHolder.image_poster.setImageDrawable(movie_name);

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_poster;
        public TextView tv_title;
        public ViewHolder(View itemView) {
            super(itemView);
            this.image_poster = (ImageView) itemView.findViewById(R.id.image_poster);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
