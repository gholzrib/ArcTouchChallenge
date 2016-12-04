package gholzrib.arctouchchallenge.core.utils;

/**
 * Created by Gunther Ribak on 30/11/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class Constants {

    public static final String BASE_URL = "https://api.themoviedb.org/3";
    public static final String BASE_URL_IMAGES = "https://image.tmdb.org/t/p/";

    public static final String PARAMETER_API_KEY = "api_key";
    public static final String PARAMETER_LANGUAGE = "language";
    public static final String PARAMETER_PAGE = "page";

    public static final String RESPONSE_STATUS_CODE = "status_code";
    public static final String RESPONSE_STATUS_MESSAGE = "status_message";
    public static final String RESPONSE_PAGE = "page";
    public static final String RESPONSE_TOTAL_PAGES = "total_pages";
    public static final String RESPONSE_RESULTS = "results";
    public static final String RESPONSE_GENRES = "genres";
    public static final String RESPONSE_GENRE_ID = "id";
    public static final String RESPONSE_GENRE_NAME = "name";
    public static final String RESPONSE_TMDB_CONFIGURATION_IMAGES = "images";
    public static final String RESPONSE_TMDB_CONFIGURATION_CHANGE_KEYS = "change_keys";

    public static final String API_KEY_VALUE = "1f54bd990f1cdfb230adb312546d765d";

    public static final String LANGUAGE_ENGLISH_US = "en-US";

    public static final String REQUEST_PATH_MOVIE = "movie";
    public static final String REQUEST_PATH_UPCOMING = "upcoming";
    public static final String REQUEST_PATH_GENRE = "genre";
    public static final String REQUEST_PATH_LIST = "list";
    public static final String REQUEST_PATH_CONFIGURATION = "configuration";

    public static final int RESPONSE_CODE_OK = 200;
    public static final int RESPONSE_CODE_UNAUTHORIZED = 401;
    public static final int RESPONSE_CODE_NOT_FOUND = 404;

    public static final String ERROR_REQUEST_UNSUCCESSFUL = "We were unable to complete your request, please try again later";

}
