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

public class TMDBConfigurationRequest extends RequestsHandler {

    public TMDBConfigurationRequest(RequestsListener requestsListener, int operation) {
        super(requestsListener, operation);
    }

    @Override
    public void doRequest(String additionalParams, File picture) {
        String[] paths = new String[] { Constants.REQUEST_PATH_CONFIGURATION };

        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAMETER_API_KEY, Constants.API_KEY_VALUE);

        doRequestByUsingGet(createUrl(Constants.BASE_URL, paths, params));
    }
}
