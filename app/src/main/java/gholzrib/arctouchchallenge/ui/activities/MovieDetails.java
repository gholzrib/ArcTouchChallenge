package gholzrib.arctouchchallenge.ui.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
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
import java.util.Locale;

import gholzrib.arctouchchallenge.R;
import gholzrib.arctouchchallenge.core.models.Movie;
import gholzrib.arctouchchallenge.core.models.TMDBConfiguration;
import gholzrib.arctouchchallenge.core.utils.CheckConnection;
import gholzrib.arctouchchallenge.core.utils.Constants;
import gholzrib.arctouchchallenge.core.utils.PreferencesManager;

import static gholzrib.arctouchchallenge.core.models.TMDBConfiguration.IMAGE_TYPE_BACKDROP;
import static gholzrib.arctouchchallenge.core.models.TMDBConfiguration.IMAGE_TYPE_POSTER;

public class MovieDetails extends AppCompatActivity implements View.OnClickListener {

    private SimpleDateFormat appSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
    private SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

    private int LNR_GENRES_WIDTH_LIMIT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView mBtnClose = (ImageView) findViewById(R.id.act_movie_details_btn_close);
        RelativeLayout mRltPosterContainer = (RelativeLayout) findViewById(R.id.act_movie_details_rlt_poster_container);
        ImageView mIvPoster = (ImageView) findViewById(R.id.act_movie_details_iv_poster);
        final ProgressBar mPrgLoading = (ProgressBar) findViewById(R.id.act_movie_details_prg_loading);
        TextView mTvTitle = (TextView) findViewById(R.id.act_movie_details_tv_title);
        TextView mTvReleaseDate = (TextView) findViewById(R.id.act_movie_details_tv_release_date);
        TextView mTvOverview = (TextView) findViewById(R.id.act_movie_details_tv_overview);
        final TextView mTvGenres = (TextView) findViewById(R.id.act_movie_details_tv_genres);
        final LinearLayout mLnrGenres = (LinearLayout) findViewById(R.id.act_movie_details_lnr_genres);

        mBtnClose.setOnClickListener(this);

        String posterSizePath = null;
        String backdropSizePath = null;

        TMDBConfiguration tmdbConfiguration = PreferencesManager.getTmdbConfiguration(this);
        if (tmdbConfiguration != null && tmdbConfiguration.getImages() != null) {
            int imageWidth = getWindowManager().getDefaultDisplay().getWidth() / 3;
            posterSizePath = tmdbConfiguration.getImageSizeUrlPath(IMAGE_TYPE_POSTER, imageWidth);
            backdropSizePath = tmdbConfiguration.getImageSizeUrlPath(IMAGE_TYPE_BACKDROP, imageWidth);
            //Log.i(TAG, "Poster path: " + posterSizePath + " Backdrop path: " + backdropSizePath);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Constants.EXTRA_MOVIE_DETAILS)) {
            final Movie movie = (Movie) extras.getSerializable(Constants.EXTRA_MOVIE_DETAILS);
            if (movie != null) {
                String posterUrl = null;
                if (movie.getPoster_path() != null && posterSizePath != null) {
                    posterUrl = Constants.BASE_URL_IMAGES + posterSizePath + movie.getPoster_path();
                } else if (movie.getBackdrop_path() != null && backdropSizePath != null) {
                    posterUrl = Constants.BASE_URL_IMAGES + backdropSizePath + movie.getBackdrop_path();
                }
                if (posterUrl != null && CheckConnection.hasInternetConnection(this, false)) {
                    mRltPosterContainer.setVisibility(View.VISIBLE);
                    //Log.d(TAG, "Poster URL: " + posterUrl);
                    Picasso.with(this)
                            .load(posterUrl)
                            .placeholder(R.drawable.placeholder_generic)
                            .into(mIvPoster, new Callback() {
                                @Override
                                public void onSuccess() {
                                   mPrgLoading.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    mPrgLoading.setVisibility(View.GONE);
                                }
                            });
                } else {
                    mRltPosterContainer.setVisibility(View.GONE);
                }
                if (movie.getTitle() != null) mTvTitle.setText(movie.getTitle());
                if (movie.getRelease_date() != null) {
                    try {
                        String date = appSdf.format(apiSdf.parse(movie.getRelease_date()));
                        mTvReleaseDate.setText(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (movie.getOverview() != null) mTvOverview.setText(movie.getOverview());
                mLnrGenres.removeAllViews();
                mLnrGenres.post(new Runnable() {
                    @Override
                    public void run() {
                        int margin = getResources().getDimensionPixelSize(R.dimen.spacing_tiny);
                        if (mLnrGenres.getWidth() > 0) {
                            LNR_GENRES_WIDTH_LIMIT = mLnrGenres.getWidth();
                        }

                        LinearLayout mLnrGenresRow = createGenresRowLayout();

                        if (movie.getGenres().size() == 0) {
                            mTvGenres.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < movie.getGenres().size(); i++) {
                            Movie.MovieGenre genre = movie.getGenres().get(i);
                            TextView mTvGenre = new TextView(MovieDetails.this);
                            LinearLayout.LayoutParams genreParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            genreParams.setMargins(0, 0, margin, 0);
                            mTvGenre.setLayoutParams(genreParams);
                            mTvGenre.setText(genre.getName());
                            if (i % 2 == 0) {
                                mTvGenre.setTextColor(ContextCompat.getColor(MovieDetails.this, R.color.text_genre_even));
                            } else {
                                mTvGenre.setTextColor(ContextCompat.getColor(MovieDetails.this, R.color.text_genre_odd));
                            }
                            mTvGenre.setAllCaps(true);
                            mTvGenre.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                            //Log.i(TAG, "mLnrGenresRow: " + mLnrGenresRow.getMeasuredWidth() + " mTvGenre: " + mTvGenre.getMeasuredWidth() + " margin: " + margin + " widthLimit: " + LNR_GENRES_WIDTH_LIMIT);

                            if (mLnrGenresRow.getMeasuredWidth() + mTvGenre.getMeasuredWidth() + margin > LNR_GENRES_WIDTH_LIMIT) {
                                mLnrGenres.addView(mLnrGenresRow);
                                mLnrGenresRow = createGenresRowLayout();
                            }

                            mLnrGenresRow.addView(mTvGenre);
                            mLnrGenresRow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        }

                        mLnrGenres.addView(mLnrGenresRow);
                    }
                });
            }
        } else {
            finish();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_movie_details_btn_close:
                finish();
                break;
        }
    }

    /**
     * Creates a new Linear Layout to be used as a row of genres
     * @return
     */
    private LinearLayout createGenresRowLayout() {
        LinearLayout mLnrGenresRow = new LinearLayout(this);
        mLnrGenresRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mLnrGenresRow.setOrientation(LinearLayout.HORIZONTAL);
        mLnrGenresRow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return mLnrGenresRow;
    }
}
