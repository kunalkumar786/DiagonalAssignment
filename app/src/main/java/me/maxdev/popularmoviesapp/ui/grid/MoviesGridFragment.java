package me.maxdev.popularmoviesapp.ui.grid;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.Toast;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.maxdev.popularmoviesapp.PopularMoviesApp;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.adapter.MyListAdapter;
import me.maxdev.popularmoviesapp.api.DiscoverAndSearchResponse;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.data.MoviesService;
import me.maxdev.popularmoviesapp.data.SortHelper;
import me.maxdev.popularmoviesapp.model.MovieData;
import me.maxdev.popularmoviesapp.ui.EndlessRecyclerViewOnScrollListener;
import me.maxdev.popularmoviesapp.ui.SortingDialogFragment;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MoviesGridFragment extends AbstractMoviesGridFragment {

    private static final String LOG_TAG = "MoviesGridFragment";
    private static final int SEARCH_QUERY_DELAY_MILLIS = 400;
private String TAG="MoviesGridFragment";
    @Inject
    MoviesService moviesService;
    @Inject
    SortHelper sortHelper;

    @Inject
    TheMovieDbService theMovieDbService;



    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;
    private SearchView searchView;
    private ArrayList<MovieData>movies_list;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MoviesService.BROADCAST_UPDATE_FINISHED)) {
                if (!intent.getBooleanExtra(MoviesService.EXTRA_IS_SUCCESSFUL_UPDATED, true)) {
                    Snackbar.make(swipeRefreshLayout, R.string.error_failed_to_update_movies,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
                swipeRefreshLayout.setRefreshing(false);
                endlessRecyclerViewOnScrollListener.setLoading(false);
                updateGridLayout();
            } else if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)) {
                recyclerView.smoothScrollToPosition(0);
                restartLoader();
            }
        }
    };




    public static MoviesGridFragment create() {
        return new MoviesGridFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ((PopularMoviesApp) getActivity().getApplication()).getNetworkComponent().inject(this);

                Log.e("DATA_FROM_ASSET",""+loadFirstFromAsset());
        movies_list=parseJsonData();


    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MoviesService.BROADCAST_UPDATE_FINISHED);
        intentFilter.addAction(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
        if (endlessRecyclerViewOnScrollListener != null) {
            endlessRecyclerViewOnScrollListener.setLoading(moviesService.isLoading());
        }
        swipeRefreshLayout.setRefreshing(moviesService.isLoading());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movies_grid, menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        if (searchViewMenuItem != null) {
            searchView = (SearchView) searchViewMenuItem.getActionView();
            MenuItemCompat.setOnActionExpandListener(searchViewMenuItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            recyclerView.setAdapter(null);
                            initMoviesGrid();
                            restartLoader();
                            swipeRefreshLayout.setEnabled(true);
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }
                    });
            setupSearchView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_show_sort_by_dialog:
                showSortByDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @NonNull
    protected Uri getContentUri() {
        return sortHelper.getSortedMoviesUri();
    }

    @Override
    protected void onCursorLoaded(Cursor data) {
        try {

            getAdapter().changeCursor(data);
            if (data == null || data.getCount() == 0) {
                refreshMovies();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    @Override
    protected void onRefreshAction() {
        refreshMovies();
    }

    @Override
    protected void onMoviesGridInitialisationFinished() {
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(getGridLayoutManager()) {
            @Override
            public void onLoadMore() {
                swipeRefreshLayout.setRefreshing(true);
                moviesService.loadMoreMovies();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
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



    private void setupSearchView() {
        if (searchView == null) {
            Log.e(LOG_TAG, "SearchView is not initialized");
            return;
        }
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
             Log.d(TAG,query);
             return false;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
           // if (newText.length()>=3) {
                ArrayList<MovieData> movietempData = new ArrayList<>();
                for (int i = 0; i < movies_list.size(); i++) {
                    Log.e("category_NAME==>", movies_list.get(i).getContent_name());
                    if (movies_list.get(i).getContent_name().contains(newText)) {
                        movietempData.add(movies_list.get(i));
                    }
                }
                if (movietempData != null && movietempData.size() > 0) {
                    MyListAdapter myListAdapter = new MyListAdapter(getActivity(), movietempData);
                    recyclerView.setAdapter(myListAdapter);
                    updateGridLayout();
                }
            //}
             return true;
         }
     });

        searchView.setOnSearchClickListener(view -> {
            recyclerView.setAdapter(null);
            recyclerView.removeOnScrollListener(endlessRecyclerViewOnScrollListener);
            updateGridLayout();
            swipeRefreshLayout.setEnabled(false);
        });
    }

    @Override
    public String toString() {
        return "MoviesGridFragment{" +
                "TAG='" + TAG + '\'' +
                ", moviesService=" + moviesService +
                ", sortHelper=" + sortHelper +
                ", theMovieDbService=" + theMovieDbService +
                ", endlessRecyclerViewOnScrollListener=" + endlessRecyclerViewOnScrollListener +
                ", searchView=" + searchView +
                ", movies_list=" + movies_list +
                ", broadcastReceiver=" + broadcastReceiver +
                '}';
    }

    private void refreshMovies() {
        swipeRefreshLayout.setRefreshing(true);
        moviesService.refreshMovies();
    }

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortingDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), SortingDialogFragment.TAG);
    }
}
