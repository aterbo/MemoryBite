package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ATerbo on 11/24/15.
 */
public class ExportHelper {

    private Context context;
    private String shareType;
    private final int CSV_FORMAT = 1;

    //Constructor code
    public ExportHelper(Context context){
        this.context = context;
    }

    public void chooseExportType (){
        CharSequence exportOptions[] = new CharSequence[] {
                context.getResources().getString(R.string.toCSV),
                context.getResources().getString(R.string.cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.export_format));
        builder.setIcon(R.drawable.mbicon);
        builder.setItems(exportOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareType = "text/csv";
                        new AsyncTaskHandler().execute(CSV_FORMAT);
                        break;
                    case 1:
                        break;
                }
            }
        });
        builder.show();
    }

    private File makeExportFile(String fileExtension){
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) +
                context.getResources().getString(R.string.export_folder), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
            }

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        File file = new File(exportDir, context.getResources().getString(R.string.export_file_name) + currentDate
                + fileExtension);

        return file;
    }

    // Exporting via AsyncTask
    private class AsyncTaskHandler extends AsyncTask<Integer, Void, File> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Integer... params) {
            switch (params[0]) {
                case CSV_FORMAT:
                    DBHelper db = new DBHelper(context);
                    File outputFile = makeExportFile(".csv");
                    db.exportAsCSV(outputFile);
                    return outputFile;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final File outputFile) {
            super.onPostExecute(outputFile);

            //Show dialog notifying where file has been saved.
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(context.getResources().getString(R.string.journal_exported));
            builder.setMessage(context.getResources().getString(R.string.journal_exported_message));

            builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    //make share intent if user clicks yes
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    Intent shareOuputIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareOuputIntent.setType(shareType);
                    shareOuputIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                            context.getResources().getString(R.string.export_subject));
                    shareOuputIntent.putExtra(Intent.EXTRA_TEXT,
                            context.getResources().getString(R.string.export_text) + " " + currentDate + ".");
                    shareOuputIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + outputFile));
                    context.startActivity(Intent.createChooser(shareOuputIntent,
                            context.getResources().getString(R.string.export_share_message)));
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
