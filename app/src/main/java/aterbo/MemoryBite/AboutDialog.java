package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ATerbo on 9/1/15.
 */
public class AboutDialog extends DialogFragment {

    public static AboutDialog newInstance() {
        AboutDialog frag = new AboutDialog();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String dialogText = getResources().getText(R.string.app_name) + "\n" +
                getResources().getText(R.string.version_number) + "\n" +
                getResources().getText(R.string.release_date) + "\n\n\n" +
                getResources().getText(R.string.dialog_about_body);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about)
                .setCancelable(false)
                .setMessage(dialogText)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}