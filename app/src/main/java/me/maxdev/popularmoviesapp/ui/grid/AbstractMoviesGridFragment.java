package me.maxdev.popularmoviesapp.ui.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.adapter.MyListAdapter;
import me.maxdev.popularmoviesapp.model.MovieData;
import me.maxdev.popularmoviesapp.ui.ItemOffsetDecoration;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;
import me.maxdev.popularmoviesapp.util.OnItemSelectedListener;

public abstract class AbstractMoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
private String Tag="AbstractMoviesGridFragment";
    private static final int LOADER_ID = 0;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movies_grid)
    RecyclerView recyclerView;
    @BindView(R.id.view_no_movies)
    RelativeLayout noMoviesView;

    private MoviesAdapter adapter;
    private MyListAdapter myListAdapter;

    private OnItemSelectedListener onItemSelectedListener;
    private GridLayoutManager gridLayoutManager;

    public AbstractMoviesGridFragment() {
        // Required empty public constructor
    }

    @NonNull
    protected abstract Uri getContentUri();

    protected abstract void onCursorLoaded(Cursor data);

    protected abstract void onRefreshAction();

    protected abstract void onMoviesGridInitialisationFinished();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            onItemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new IllegalStateException("The activity must implement " +
                    OnItemSelectedListener.class.getSimpleName() + " interface.");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        initSwipeRefreshLayout();
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        initMoviesGrid();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGridLayout();
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    protected void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, AbstractMoviesGridFragment.this);
    }

    protected void updateGridLayout() {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noMoviesView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMoviesView.setVisibility(View.GONE);
        }
    }

    protected void initMoviesGrid() {
        View view = getActivity().getWindow().getDecorView();
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            reinitializeGrid(7);

        } else {
            reinitializeGrid(3);
        }

        onMoviesGridInitialisationFinished();

    }


public void reinitializeGrid(int column){
    myListAdapter=new MyListAdapter(getContext(), parseJsonData());
    recyclerView.setAdapter(myListAdapter);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    //int columns = getResources().getInteger(R.integer.movies_columns_3);
    gridLayoutManager = new GridLayoutManager(getActivity(), column);
    recyclerView.setLayoutManager(gridLayoutManager);
}
    public ArrayList<MovieData> parseJsonData(){
    ArrayList<MovieData> movieData=new ArrayList<>();
        try{
            JSONObject jsonObject=new JSONObject(loadFirstFromAsset());
          JSONObject  res=jsonObject.getJSONObject("page");
          JSONObject res_val= res.getJSONObject("content-items");
            JSONArray cntnt=res_val.getJSONArray("content");
            if(cntnt.length()>0){
                for(int i=0;i<cntnt.length();i++){
                    MovieData movieData1=new MovieData();
                    movieData1.setCategory_name(res.getString("title"));
                    JSONObject data=cntnt.getJSONObject(i);
                    movieData1.setContent_name(data.getString("name"));
                    movieData1.setContent_poster_image(data.getString("poster-image"));
                    movieData.add(movieData1);
                }
            }


        }catch(Exception e){
            e.printStackTrace();

        }
return movieData;
    }



    public String loadFirstFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("CONTENTLISTINGPAGE-PAGE1.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }




    @SuppressLint("PrivateResource")
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_material_dark,
                R.color.accent_material_light);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                getContentUri(),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        onCursorLoaded(data);
        updateGridLayout();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
        updateGridLayout();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        onItemSelectedListener.onItemSelected(adapter.getItem(position));
    }

    @Override
    public void onRefresh() {
        onRefreshAction();
    }

    public MoviesAdapter getAdapter() {
        return adapter;
    }

    public GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }
}
