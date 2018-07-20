package misfit.testing.trackme.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import misfit.testing.trackme.R;

public class DialogUtil {
    public static void showConfirmDialog(Context context, String header,
                                         String message, String positiveLabel,
                                         DialogInterface.OnClickListener positiveAction, String negativeLabel, DialogInterface.OnClickListener negativeAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        if (header != null && header.length() != 0)
            builder.setTitle(header);


        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveLabel, positiveAction)
                .setNegativeButton(negativeLabel, negativeAction)
                .show();
    }
    public static void showConfirmDialog(Context context,
                                         String message,
                                         DialogInterface.OnClickListener positiveAction, DialogInterface.OnClickListener negativeAction) {
        showConfirmDialog(context, "", message, "OK", positiveAction, "Cancel", negativeAction);
    }
    public static void showInformDialog(Context context, String title, String message, String okLabel, DialogInterface.OnClickListener okListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        if (title != null && title.length() != 0)
            builder.setTitle(title);


        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(okLabel, okListener)
                .show();
    }
    public static void showInformDialog(Context context, String message, DialogInterface.OnClickListener okListener){
        showInformDialog(context, "", message, "OK", okListener);
    }
}
