package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ATerbo on 9/13/15.
 */
public class HelpDialog extends DialogFragment {

    public static HelpDialog newInstance() {
        HelpDialog frag = new HelpDialog();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String dialogText = getResources().getString(R.string.dialog_help_body);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_help_header)
                .setIcon(R.drawable.mbicon)
                .setCancelable(false)
                .setMessage(dialogText)
                .setPositiveButton(R.string.got_it,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
