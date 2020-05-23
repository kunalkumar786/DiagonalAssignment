package me.maxdev.popularmoviesapp.ui.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.model.MovieData;
import me.maxdev.popularmoviesapp.util.CursorRecyclerViewAdapter;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;

public class MoviesAdapter extends CursorRecyclerViewAdapter<MovieGridItemViewHolder> {

    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w780";
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private ArrayList<MovieData> movie_list;
    public MoviesAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }
    public MoviesAdapter(Context context, Cursor cursor,ArrayList<MovieData> movie_list) {
        super(cursor);
        this.context = context;
        this.movie_list=movie_list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(MovieGridItemViewHolder viewHolder, Cursor cursor) {

    }

    @Override
    @SuppressLint("PrivateResource")
    public void onBindViewHolder(MovieGridItemViewHolder viewHolder, int cursor) {
        //if (cursor != null) {
            MovieData movie = movie_list.get(cursor);
            viewHolder.moviePoster.setContentDescription(movie.getCategory_name());
            String movies_name=movie.getContent_poster_image();

            String uri="@drawable/";

        int imageResource;

        Drawable res;
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

            }

        //}

    }
public void loadMovie(Drawable movie_name, MovieGridItemViewHolder viewHolder){
    Glide.with(context)
            .load(movie_name)
            .placeholder(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .crossFade()
            .into(viewHolder.moviePoster);
}
    @Override
    public MovieGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie, parent, false);
        return new MovieGridItemViewHolder(itemView, onItemClickListener);
    }

    @Nullable
    public Movie getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return null;
        }
        if (position < 0 || position > cursor.getCount()) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < position; i++) {
            cursor.moveToNext();
        }
        return Movie.fromCursor(cursor);
    }

}
