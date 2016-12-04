package gholzrib.arctouchchallenge.core.utils;

/**
 * Created by Gunther Ribak on 08/01/2015.
 * For more information contact me
 * through guntherhr@gmail.com
 */

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog extends ProgressDialog {

    private static final String MESSAGE_LOADING = "Loading";

    private Context context;
    private boolean isCancelable;

    public LoadingDialog(Context context, int theme, boolean isCancelable) {
        super(context, theme);
        this.context = context;
        this.isCancelable = isCancelable;
        setupDialog();
    }

    public LoadingDialog(Context context, boolean isCancelable) {
        super(context);
        this.context = context;
        this.isCancelable = isCancelable;
        setupDialog();
    }

    private void setupDialog() {
        this.setMessage(MESSAGE_LOADING);
        this.setIndeterminate(true);
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(isCancelable);
    }

}