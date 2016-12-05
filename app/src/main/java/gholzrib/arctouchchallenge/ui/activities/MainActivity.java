package gholzrib.arctouchchallenge.ui.activities;

import android.app.Dialog;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import gholzrib.arctouchchallenge.R;
import gholzrib.arctouchchallenge.core.handlers.DateAndTimeHandler;
import gholzrib.arctouchchallenge.core.listeners.RequestsListener;
import gholzrib.arctouchchallenge.core.models.Movie;
import gholzrib.arctouchchallenge.core.models.TMDBConfiguration;
import gholzrib.arctouchchallenge.core.requests.GenresRequest;
import gholzrib.arctouchchallenge.core.requests.MoviesRequest;
import gholzrib.arctouchchallenge.core.requests.SearchRequest;
import gholzrib.arctouchchallenge.core.requests.TMDBConfigurationRequest;
import gholzrib.arctouchchallenge.core.utils.CheckConnection;
import gholzrib.arctouchchallenge.core.utils.Constants;
import gholzrib.arctouchchallenge.core.utils.EndlessRecyclerViewScrollListener;
import gholzrib.arctouchchallenge.core.utils.LoadingDialog;
import gholzrib.arctouchchallenge.core.utils.PreferencesManager;
import gholzrib.arctouchchallenge.ui.adapters.MoviesAdapter;

/**
 * Created by Gunther Ribak on 29/11/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        RequestsListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_OPERATION_GENRES = 0;
    private static final int REQUEST_OPERATION_UPCOMING_MOVIES = 1;
    private static final int REQUEST_OPERATION_TMDB_CONFIGURATION = 2;
    private static final int REQUEST_OPERATION_SEARCH = 3;

    private static final String MESSAGE_LOADING_CONFIGURATION = "Loading configurations";
    private static final String MESSAGE_LOADING_GENRES = "Loading genres";
    private static final String MESSAGE_LOADING_UPCOMING_MOVIES = "Loading upcoming movies";
    private static final String MESSAGE_LOADING_SEARCH_RESULTS = "Loading search results";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

    boolean hasNextPage = true;
    boolean isSearching = false;

    private int nextPage = 1;
    private int currentOperation = -1;
    private String searchQuery = "";

    MoviesAdapter mAdapter;

    RecyclerView mRvMovies;
    LinearLayout mLnrWarningNoInternet;
    LinearLayout mLnrWarningNoData;

    EditText mEdtQuery;

    HashMap<Integer, String> mGenres = new HashMap<>();

    DateAndTimeHandler mDateAndTimeHandler;
    LoadingDialog mLoadingDialog;
    EndlessRecyclerViewScrollListener mEndlessScrollListener;
    Dialog mSearchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_label_main_activity);


        mDateAndTimeHandler = new DateAndTimeHandler(dateFormat);
        mLoadingDialog = new LoadingDialog(this, false);

        mRvMovies = (RecyclerView) findViewById(R.id.act_main_rv_movies);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRvMovies.setLayoutManager(mLinearLayoutManager);
        mRvMovies.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int margin = getResources().getDimensionPixelSize(R.dimen.spacing_tiny);
                outRect.set(0, 0, 0, margin);
            }
        });
        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (hasNextPage) {
                    Log.i(TAG, "Page: " + page);
                    if (isSearching) {
                        attemptToExecuteRequest(new String[]{searchQuery, String.valueOf(page)}, REQUEST_OPERATION_SEARCH);
                    } else {
                        attemptToExecuteRequest(new String[] {String.valueOf(page)}, REQUEST_OPERATION_UPCOMING_MOVIES);
                    }
                }
            }
        };
        mRvMovies.addOnScrollListener(mEndlessScrollListener);

        mAdapter = new MoviesAdapter(this, new ArrayList<Movie>());
        mRvMovies.setAdapter(mAdapter);

        mLnrWarningNoInternet = (LinearLayout) findViewById(R.id.cnt_no_internet_lnr_warning);
        mLnrWarningNoData = (LinearLayout) findViewById(R.id.cnt_no_data_lnr_warning);

        findViewById(R.id.act_main_btn_try_again).setOnClickListener(this);

        attemptToExecuteRequest(null, REQUEST_OPERATION_TMDB_CONFIGURATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mSearchDialog = new Dialog(this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);

                mEdtQuery = (EditText) dialogView.findViewById(R.id.dlg_search_edt_query);
                dialogView.findViewById(R.id.dlg_search_btn_search).setOnClickListener(this);
                dialogView.findViewById(R.id.dlg_search_btn_cancel).setOnClickListener(this);

                mSearchDialog.setContentView(dialogView);
                mSearchDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_main_btn_try_again:
                attemptToExecuteRequest(null, REQUEST_OPERATION_TMDB_CONFIGURATION);
                break;
            case R.id.dlg_search_btn_search:
                mEdtQuery.setError(null);
                searchQuery = mEdtQuery.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    isSearching = true;
                    nextPage = 1;
                    mEndlessScrollListener.resetState();
                    mAdapter.removeAllItems();
                    mSearchDialog.dismiss();
                    attemptToExecuteRequest(new String[]{searchQuery, String.valueOf(nextPage)}, REQUEST_OPERATION_SEARCH);
                } else {
                    isSearching = false;
                    nextPage = 1;
                    mEndlessScrollListener.resetState();
                    mAdapter.removeAllItems();
                    mSearchDialog.dismiss();
                    attemptToExecuteRequest(new String[] {String.valueOf(nextPage)}, REQUEST_OPERATION_UPCOMING_MOVIES);
                }
                break;
            case R.id.dlg_search_btn_cancel:
                mSearchDialog.dismiss();
                break;
        }
    }

    @Override
    public void onPreExecute() {
        if (mLoadingDialog != null && currentOperation != -1) {
            switch (currentOperation) {
                case REQUEST_OPERATION_TMDB_CONFIGURATION:
                    mLoadingDialog.setMessage(MESSAGE_LOADING_CONFIGURATION);
                    break;
                case REQUEST_OPERATION_GENRES:
                    mLoadingDialog.setMessage(MESSAGE_LOADING_GENRES);
                    break;
                case REQUEST_OPERATION_UPCOMING_MOVIES:
                    mLoadingDialog.setMessage(MESSAGE_LOADING_UPCOMING_MOVIES);
                    break;
                case REQUEST_OPERATION_SEARCH:
                    mLoadingDialog.setMessage(MESSAGE_LOADING_SEARCH_RESULTS);
                    break;
            }
            if (!mLoadingDialog.isShowing() && nextPage == 1) {
                mLoadingDialog.show();
            }
        }
    }

    @Override
    public void onRequestEnds(int operation, boolean isSuccess, int code, String parsedData) {
        if (isSuccess) {
            try {
                JSONObject jsonObject = new JSONObject(parsedData);
                switch (code) {
                    case Constants.RESPONSE_CODE_OK:
                        handleSuccessfulRequestResults(operation, jsonObject);
                        break;
                    case Constants.RESPONSE_CODE_UNAUTHORIZED:
                    case Constants.RESPONSE_CODE_NOT_FOUND:
                        String statusMessage = null;
                        int statusCode = 0;
                        if (!jsonObject.isNull(Constants.RESPONSE_STATUS_MESSAGE))
                            statusMessage = jsonObject.getString(Constants.RESPONSE_STATUS_MESSAGE);
                        if (!jsonObject.isNull(Constants.RESPONSE_STATUS_CODE))
                            statusCode = jsonObject.getInt(Constants.RESPONSE_STATUS_CODE);
                        if (statusCode != 0) statusMessage = statusMessage + "(Code: " + statusCode + ")";
                        if (statusMessage != null) Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        break;
                }
            } catch (JSONException e) {
                Log.i(TAG, "JSONException: " + e.getMessage());
            }
        } else {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            Toast.makeText(this, Constants.ERROR_REQUEST_UNSUCCESSFUL, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Changes the visibility of the No Internet Warning and RecyclerView according with the
     * internet connection state
     *
     * @param hasConnection
     */
    private void setWarningAndListVisibilities(boolean hasConnection) {
        if (hasConnection) {
            mRvMovies.setVisibility(View.VISIBLE);
            mLnrWarningNoInternet.setVisibility(View.GONE);
        } else {
            mRvMovies.setVisibility(View.GONE);
            mLnrWarningNoInternet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Attempt to execute a request.
     *
     * @param additionalParams
     * @param operation
     */
    private void attemptToExecuteRequest(String[] additionalParams, int operation) {
        boolean hasConnection = CheckConnection.hasInternetConnection(this, false);
        setWarningAndListVisibilities(hasConnection);
        if (!hasConnection) {
            return;
        }
        currentOperation = operation;
        switch (operation) {
            case REQUEST_OPERATION_TMDB_CONFIGURATION:
                if (PreferencesManager.containsTmdbConfigurationLastUpdateKey(this)) {
                    long days = mDateAndTimeHandler.getTimeDifferenceInDays(PreferencesManager.getTmdbConfigurationLastUpdateKey(this));
                    Log.i(TAG, "Difference in days: " + days);
                    if (days >= 7) { //Update the configuration every week
                        TMDBConfigurationRequest tmdbConfigurationRequest = new TMDBConfigurationRequest(this, operation);
                        tmdbConfigurationRequest.doRequest(null, null);
                    } else {
                        attemptToExecuteRequest(null, REQUEST_OPERATION_GENRES);
                    }
                } else {
                    TMDBConfigurationRequest tmdbConfigurationRequest = new TMDBConfigurationRequest(this, operation);
                    tmdbConfigurationRequest.doRequest(null, null);
                }
                break;
            case REQUEST_OPERATION_GENRES:
                GenresRequest genresRequest = new GenresRequest(this, operation);
                genresRequest.doRequest(null, null);
                break;
            case REQUEST_OPERATION_UPCOMING_MOVIES:
                MoviesRequest moviesRequest = new MoviesRequest(this, operation);
                moviesRequest.doRequest(additionalParams, null);
                break;
            case REQUEST_OPERATION_SEARCH:
                SearchRequest searchRequest = new SearchRequest(this, operation);
                searchRequest.doRequest(additionalParams, null);
                break;
        }
    }

    /**
     * If the request was successful, handle the result according with the operation
     *
     * @param operation
     * @param jsonObject
     * @throws JSONException
     */
    private void handleSuccessfulRequestResults(int operation, JSONObject jsonObject) {
        switch (operation) {
            case REQUEST_OPERATION_TMDB_CONFIGURATION:
                if (!jsonObject.isNull(Constants.RESPONSE_TMDB_CONFIGURATION_IMAGES)
                        && !jsonObject.isNull(Constants.RESPONSE_TMDB_CONFIGURATION_CHANGE_KEYS)) {
                    Gson gson = new Gson();
                    TMDBConfiguration tmdbConfiguration = gson.fromJson(jsonObject.toString(), TMDBConfiguration.class);
                    PreferencesManager.setTmdbConfiguration(this, tmdbConfiguration);
                    PreferencesManager.setTmdbConfigurationLastUpdateKey(this, mDateAndTimeHandler.getCurrentDate());
                }
                attemptToExecuteRequest(null, REQUEST_OPERATION_GENRES);
                break;
            case REQUEST_OPERATION_GENRES:
                if (!jsonObject.isNull(Constants.RESPONSE_GENRES)) {
                    mGenres = new HashMap<>();
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constants.RESPONSE_GENRES);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonGenre = jsonArray.getJSONObject(i);
                            if (!jsonGenre.isNull(Constants.RESPONSE_GENRE_ID) && !jsonGenre.isNull(Constants.RESPONSE_GENRE_NAME)) {
                                mGenres.put(
                                        jsonGenre.getInt(Constants.RESPONSE_GENRE_ID),
                                        jsonGenre.getString(Constants.RESPONSE_GENRE_NAME)
                                );
                            }
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, "JSONException: " + e.getMessage());
                    }
                }
                attemptToExecuteRequest(new String[] {String.valueOf(nextPage)}, REQUEST_OPERATION_UPCOMING_MOVIES);
                break;
            case REQUEST_OPERATION_UPCOMING_MOVIES:
            case REQUEST_OPERATION_SEARCH:
                try {
                    if (!jsonObject.isNull(Constants.RESPONSE_PAGE)
                            && !jsonObject.isNull(Constants.RESPONSE_TOTAL_PAGES)
                            && jsonObject.getInt(Constants.RESPONSE_PAGE) < jsonObject.getInt(Constants.RESPONSE_TOTAL_PAGES)) {
                        hasNextPage = true;
                        nextPage = jsonObject.getInt(Constants.RESPONSE_PAGE) + 1;
                    } else {
                        hasNextPage = false;
                    }
                    if (!jsonObject.isNull(Constants.RESPONSE_RESULTS)) {
                        Gson gson = new Gson();
                        ArrayList<Movie> movies = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray(Constants.RESPONSE_RESULTS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonMovie = jsonArray.getJSONObject(i);
                            Movie movie = gson.fromJson(jsonMovie.toString(), Movie.class);
                            if (isSearching && !movie.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                                continue;
                            }
                            for (int j = 0; j < movie.getGenre_ids().size(); j++) {
                                String genre = mGenres.get(movie.getGenre_ids().get(j));
                                if (genre != null) {
                                    Movie.MovieGenre movieGenre = new Movie.MovieGenre();
                                    movieGenre.setId(movie.getGenre_ids().get(j));
                                    movieGenre.setName(genre);
                                    movie.getGenres().add(movieGenre);
                                }
                            }
                            movies.add(movie);
                        }
                        mAdapter.update(movies);
                    }
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "JSONException: " + e.getMessage());
                }
                break;
        }
    }
}
