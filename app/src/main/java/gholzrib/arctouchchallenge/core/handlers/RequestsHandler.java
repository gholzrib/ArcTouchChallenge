package gholzrib.arctouchchallenge.core.handlers;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

import gholzrib.arctouchchallenge.ArcTouchChallengeApplication;
import gholzrib.arctouchchallenge.core.listeners.RequestsListener;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class RequestsHandler {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    protected WeakReference<RequestsListener> mApiListener = null;
    protected WeakReference<Call> mCurrentCall;
    public int mCurrentOperation;

    public RequestsHandler(RequestsListener requestsListener, int operation) {
        mApiListener = new WeakReference<>(requestsListener);
        mCurrentOperation = operation;

        if (mApiListener.get() == null) {
            throw new IllegalArgumentException("RequestsHandlerListener is required. You mustn't use requestsHandlerListener parameter as null");
        }
    }

    protected String createUrl(String baseUrl, String[] paths, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        if (paths != null) {
            for (String path : paths) {
                builder.appendPath(path);
            }
        }
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                builder.appendQueryParameter(param.getKey(), param.getValue());
            }
        }
        return builder.build().toString();
    }

    public abstract void doRequest(String additionalParams, File picture);

    protected void doRequestByUsingGet(final String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mCurrentCall = new WeakReference<>(ArcTouchChallengeApplication.getClientInstance().newCall(request));
        ApiTask apiTask = new ApiTask();
        apiTask.execute(mCurrentCall.get());
    }

    protected void doRequestByUsingPost(final String url, final String json) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mCurrentCall = new WeakReference<>(ArcTouchChallengeApplication.getClientInstance().newCall(request));
        ApiTask apiTask = new ApiTask();
        apiTask.execute(mCurrentCall.get());
    }

    protected void doRequestByUsingPost(final String url, final String json, final File picture) {

    }

    public void cancelRequest() {
        if (mCurrentCall.get() != null && !mCurrentCall.get().isExecuted()) {
            mCurrentCall.get().cancel();
        }
    }

    private class ApiTask extends AsyncTask<Call, Integer, RequestResponse> {

        @Override
        protected void onPreExecute() {
            mApiListener.get().onPreExecute();
        }

        @Override
        protected RequestResponse doInBackground(Call... params) {
            Call mCall = params[0];
            if (mCall != null) {
                try {
                    Response response =  mCall.execute();
                    RequestResponse requestResponse = new RequestResponse();
                    requestResponse.setSuccessful(response.isSuccessful());
                    requestResponse.setCode(response.code());
                    requestResponse.setBody(response.body().string());
                    return  requestResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RequestResponse requestResponse) {
            if (requestResponse != null) {
                mApiListener.get().onRequestEnds(mCurrentOperation,
                        requestResponse.isSuccessful(),
                        requestResponse.getCode(),
                        requestResponse.getBody());
            }
        }
    }

    /**
     * Model created to use the request response into the main thread
     */
    private class RequestResponse {

        boolean isSuccessful;
        int code;
        String body;

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public void setSuccessful(boolean successful) {
            isSuccessful = successful;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}