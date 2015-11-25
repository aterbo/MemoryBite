package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ATerbo on 11/24/15.
 */
public class ExportHelper {

    private Context context;

    //Constructor code
    public ExportHelper(Context context){
        this.context = context;
    }

    public void chooseExportType (){
        CharSequence sortOptions[] = new CharSequence[] {
                context.getResources().getString(R.string.toCSV),
                context.getResources().getString(R.string.cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.export_format));
        builder.setIcon(R.drawable.mbicon);
        builder.setItems(sortOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        exportToCSV();
                        break;
                    case 1:
                        break;
                }
            }
        });
        builder.show();
    }

    private void exportToCSV(){
        DBHelper db = new DBHelper(context);
        File exportedData = db.exportAsCSV(makeExportFile(".csv"));

        Toast.makeText(context, "Data saved in the Documents folder",  Toast.LENGTH_SHORT).show();

        shareOutputFile(exportedData);
    }

    private File makeExportFile(String fileExtension){
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) +
                context.getResources().getString(R.string.export_folder), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
            }

        File file = new File(exportDir, context.getResources().getString(R.string.export_file_name)
                + fileExtension);

        return file;
    }


    private void shareOutputFile(File file){

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Intent shareOuputIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareOuputIntent.setType("image/jpeg");
        shareOuputIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                context.getResources().getString(R.string.export_subject));
        shareOuputIntent.putExtra(Intent.EXTRA_TEXT,
                context.getResources().getString(R.string.export_text) + " " + currentDate + ".");
        shareOuputIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
        context.startActivity(Intent.createChooser(shareOuputIntent,
                context.getResources().getString(R.string.export_share_message)));

    }

}
