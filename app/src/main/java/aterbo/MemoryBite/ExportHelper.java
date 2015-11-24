package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by ATerbo on 11/24/15.
 */
public class ExportHelper {

    private Context context;

    //Constructor code
    public ExportHelper(Context context){
        this.context = context;
    }

    public void chooseExportType (Context context){
        CharSequence sortOptions[] = new CharSequence[] {
                context.getResources().getString(R.string.toCSV),
                context.getResources().getString(R.string.cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.export_format));
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
        Toast.makeText(context, "EXPORT BABY!", Toast.LENGTH_SHORT).show();
    }

}
