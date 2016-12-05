package gholzrib.arctouchchallenge.core.requests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import gholzrib.arctouchchallenge.core.handlers.RequestsHandler;
import gholzrib.arctouchchallenge.core.listeners.RequestsListener;
import gholzrib.arctouchchallenge.core.utils.Constants;

/**
 * Created by Gunther Ribak on 30/11/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class GenresRequest extends RequestsHandler {

    public GenresRequest(RequestsListener requestsListener, int operation) {
        super(requestsListener, operation);
    }

    @Override
    public void doRequest(String[] additionalParams, File picture) {
        String[] paths = new String[] { Constants.REQUEST_PATH_GENRE,
                Constants.REQUEST_PATH_MOVIE,
                Constants.REQUEST_PATH_LIST };

        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAMETER_API_KEY, Constants.API_KEY_VALUE);
        params.put(Constants.PARAMETER_LANGUAGE, Constants.LANGUAGE_ENGLISH_US);

        doRequestByUsingGet(createUrl(Constants.BASE_URL, paths, params));
    }
}
