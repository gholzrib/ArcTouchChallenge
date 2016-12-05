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

public class SearchRequest extends RequestsHandler {

    public SearchRequest(RequestsListener requestsListener, int operation) {
        super(requestsListener, operation);
    }

    @Override
    public void doRequest(String[] additionalParams, File picture) {
        String[] paths = new String[] { Constants.REQUEST_PATH_SEARCH,
                Constants.REQUEST_PATH_MOVIE };

        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAMETER_API_KEY, Constants.API_KEY_VALUE);
        params.put(Constants.PARAMETER_LANGUAGE, Constants.LANGUAGE_ENGLISH_US);
        params.put(Constants.PARAMETER_QUERY, additionalParams[0]);
        params.put(Constants.PARAMETER_PAGE, additionalParams[1]);

        doRequestByUsingGet(createUrl(Constants.BASE_URL, paths, params));
    }
}
