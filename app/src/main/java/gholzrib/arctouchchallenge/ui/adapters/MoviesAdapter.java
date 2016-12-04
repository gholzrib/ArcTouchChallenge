package gholzrib.arctouchchallenge.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gholzrib.arctouchchallenge.R;
import gholzrib.arctouchchallenge.core.models.Movie;
import gholzrib.arctouchchallenge.core.models.TMDBConfiguration;
import gholzrib.arctouchchallenge.core.utils.CheckConnection;
import gholzrib.arctouchchallenge.core.utils.Constants;
import gholzrib.arctouchchallenge.core.utils.PreferencesManager;
import gholzrib.arctouchchallenge.ui.activities.MovieDetails;

import static gholzrib.arctouchchallenge.core.models.TMDBConfiguration.IMAGE_TYPE_BACKDROP;
import static gholzrib.arctouchchallenge.core.models.TMDBConfiguration.IMAGE_TYPE_POSTER;

/**
 * Created by Gunther Ribak on 29/11/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private int LNR_GENRES_WIDTH_LIMIT = 0;

    private String posterSizePath = null;
    private String backdropSizePath = null;

    Context mContext;
    ArrayList<Movie> mUpcomingMoviesList;

    private int imageDimen;

    private SimpleDateFormat appSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
    private SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

    public MoviesAdapter(Context mContext, ArrayList<Movie> mUpcomingMoviesList) {
        this.mContext = mContext;
        this.mUpcomingMoviesList = mUpcomingMoviesList;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_movies, parent, false);
        imageDimen = (int) (parent.getWidth() * 0.5);
        TMDBConfiguration tmdbConfiguration = PreferencesManager.getTmdbConfiguration(mContext);
        if (tmdbConfiguration != null && tmdbConfiguration.getImages() != null) {
            int imageWidth = parent.getWidth() / 3;
            posterSizePath = tmdbConfiguration.getImageSizeUrlPath(IMAGE_TYPE_POSTER, imageWidth);
            backdropSizePath = tmdbConfiguration.getImageSizeUrlPath(IMAGE_TYPE_BACKDROP, imageWidth);
            //Log.i(TAG, "Poster path: " + posterSizePath + " Backdrop path: " + backdropSizePath);
        }
        return new MovieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, int position) {
        final Movie movie = mUpcomingMoviesList.get(position);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.mRltPosterContainer.getLayoutParams();
        params.height = imageDimen;
        holder.mRltPosterContainer.setLayoutParams(params);

        String posterUrl = null;
        if (movie.getPoster_path() != null) {
            posterUrl = Constants.BASE_URL_IMAGES + posterSizePath + movie.getPoster_path();
        } else if (movie.getBackdrop_path() != null) {
            posterUrl = Constants.BASE_URL_IMAGES + backdropSizePath + movie.getBackdrop_path();
        }

        if (posterUrl != null && CheckConnection.hasInternetConnection(mContext, false)) {
            //Log.d(TAG, "Poster URL: " + posterUrl);
            Picasso.with(mContext)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_generic)
                    .into(holder.mIvPoster, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mPrgLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.mPrgLoading.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.mPrgLoading.setVisibility(View.GONE);
            holder.mIvPoster.setImageResource(R.drawable.placeholder_generic);
        }

        holder.mTvTitle.setText(movie.getTitle());

        try {
            String date = appSdf.format(apiSdf.parse(movie.getRelease_date()));
            holder.mTvReleaseDate.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.mLnrGenres.removeAllViews();
        holder.mLnrGenres.post(new Runnable() {
            @Override
            public void run() {
                int margin = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_tiny);
                if (holder.mLnrGenres.getWidth() > 0) {
                    LNR_GENRES_WIDTH_LIMIT = holder.mLnrGenres.getWidth();
                }

                LinearLayout mLnrGenresRow = createGenresRowLayout();

                for (int i = 0; i < movie.getGenres().size(); i++) {
                    Movie.MovieGenre genre = movie.getGenres().get(i);
                    TextView mTvGenre = new TextView(mContext);
                    LinearLayout.LayoutParams genreParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    genreParams.setMargins(0, 0, margin, 0);
                    mTvGenre.setLayoutParams(genreParams);
                    mTvGenre.setText(genre.getName());
                    if (i % 2 == 0) {
                        mTvGenre.setTextColor(ContextCompat.getColor(mContext, R.color.text_genre_even));
                    } else {
                        mTvGenre.setTextColor(ContextCompat.getColor(mContext, R.color.text_genre_odd));
                    }
                    mTvGenre.setAllCaps(true);
                    mTvGenre.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                    //Log.i(TAG, "mLnrGenresRow: " + mLnrGenresRow.getMeasuredWidth() + " mTvGenre: " + mTvGenre.getMeasuredWidth() + " margin: " + margin + " widthLimit: " + LNR_GENRES_WIDTH_LIMIT);

                    if (mLnrGenresRow.getMeasuredWidth() + mTvGenre.getMeasuredWidth() + margin > LNR_GENRES_WIDTH_LIMIT) {
                        holder.mLnrGenres.addView(mLnrGenresRow);
                        mLnrGenresRow = createGenresRowLayout();
                    }

                    mLnrGenresRow.addView(mTvGenre);
                    mLnrGenresRow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                }

                holder.mLnrGenres.addView(mLnrGenresRow);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUpcomingMoviesList.size();
    }

    /**
     * Method used to update the Recycler information
     * @param mUpcomingMovies ArrayList to be loaded
     */
    public void update(ArrayList<Movie> mUpcomingMovies) {
        this.mUpcomingMoviesList.addAll(mUpcomingMovies);
        notifyDataSetChanged();
    }

    /**
     * Creates a new Linear Layout to be used as a row of genres
     * @return
     */
    private LinearLayout createGenresRowLayout() {
        LinearLayout mLnrGenresRow = new LinearLayout(mContext);
        mLnrGenresRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mLnrGenresRow.setOrientation(LinearLayout.HORIZONTAL);
        mLnrGenresRow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return mLnrGenresRow;
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout mLnrContainer;
        RelativeLayout mRltPosterContainer;
        ImageView mIvPoster;
        ProgressBar mPrgLoading;
        TextView mTvTitle;
        TextView mTvReleaseDate;
        LinearLayout mLnrGenres;

        public MovieHolder(View itemView) {
            super(itemView);

            mLnrContainer = (LinearLayout) itemView.findViewById(R.id.adp_movies_lnr_container);
            mRltPosterContainer = (RelativeLayout) itemView.findViewById(R.id.adp_movies_rlt_poster_container);
            mIvPoster = (ImageView) itemView.findViewById(R.id.adp_movies_iv_poster);
            mPrgLoading = (ProgressBar) itemView.findViewById(R.id.adp_movies_prg_loading);
            mTvTitle = (TextView) itemView.findViewById(R.id.adp_movies_tv_title);
            mTvReleaseDate = (TextView) itemView.findViewById(R.id.adp_movies_tv_release_date);
            mLnrGenres = (LinearLayout) itemView.findViewById(R.id.adp_movies_lnr_genres);

            mLnrContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.adp_movies_lnr_container:
                    Intent intent = new Intent(mContext, MovieDetails.class);
                    intent.putExtra(Constants.EXTRA_MOVIE_DETAILS, mUpcomingMoviesList.get(this.getAdapterPosition()));
                    mContext.startActivity(intent);
                    break;
            }
        }
    }

}
