package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

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

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "MemoryBiteCSVExport.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM Table",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }


    }

}
