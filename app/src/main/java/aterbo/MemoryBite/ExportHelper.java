package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ATerbo on 11/24/15.
 */
public class ExportHelper {

    private Context context;
    private String shareType;
    private final int CSV_FORMAT = 1;
    private final int PDF_FORMAT = 2;
    private String PRINT_SERVICE = "PrintService";

    //Constructor code
    public ExportHelper(Context context){
        this.context = context;
    }

    public void chooseExportType (){
        CharSequence exportOptions[] = new CharSequence[] {
                context.getResources().getString(R.string.toCSV),
                context.getResources().getString(R.string.toPDF),
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
                        shareType = "application/pdf";
                        new AsyncTaskHandler().execute(PDF_FORMAT);
                        break;
                    case 2:
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

        String currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());

        File file = new File(exportDir, context.getResources().getString(R.string.export_file_name) + currentDate
                + fileExtension);

        return file;
    }


    private File pdfExporter(File pdfFile) {

        //Pull data from Meal Database
        List<Meal> mealList;
        ArrayList<Photo> photos;
        Meal meal;
        int position = 0;

        //Sets print options
        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("res1", PRINT_SERVICE, 300, 300)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();

        //create PDF Document
        PdfDocument document = new PrintedPdfDocument(context, printAttrs);

        //Pull data from database
        DBHelper db = new DBHelper(context);
        mealList = db.getAllMealsSortedList(DBContract.MealDBTable.COLUMN_DATE, true);
        int mealCount = mealList.size();

        //Views and containers needed to inflate layout
        LinearLayout container = new LinearLayout(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = new View(context);

        while(position < mealCount){
            //Inflate layout to view. Attach to container as root.
            view = inflater.inflate( R.layout.layout_pdf_meal_template, container, true);

            meal = mealList.get(position);
            //photos = db.getAllPhotosForMealList(meal.getMealIdNumber());

            checkViewAndDisplay(meal.getRestaurantName(), R.id.restaurant_name_details, 0, container);
            checkViewAndDisplay(meal.getDateMealEaten(), R.id.date_details, 0, container);
            checkViewAndDisplay(meal.getDinedWith(), R.id.dined_with_details, 0, container);
            checkViewAndDisplay(meal.getLocation(), R.id.location_details, 0, container);
            checkViewAndDisplay(meal.getCuisineType(), R.id.cuisine_type_details, 0, container);
            checkViewAndDisplay(meal.getAppetizersNotes(), R.id.appetizers_details, R.id.appetizers_title, container);
            checkViewAndDisplay(meal.getMainCoursesNotes(), R.id.main_course_details, R.id.main_course_title, container);
            checkViewAndDisplay(meal.getDessertsNotes(), R.id.dessert_details, R.id.dessert_title, container);
            checkViewAndDisplay(meal.getDrinksNotes(), R.id.drinks_details, R.id.drinks_title, container);
            checkViewAndDisplay(meal.getGeneralNotes(), R.id.general_notes_details, R.id.general_notes_title, container);
            checkViewAndDisplay(meal.getAtmosphere(), R.id.atmosphere_details, 0, container);
            checkViewAndDisplay(meal.getPrice(), R.id.price_details, 0, container);

            TextView pageNumber = ((TextView) container.findViewById(R.id.page_number));
            pageNumber.setText("Pg. " + (position+1));

            //Method call to make and write page
            document = makePageAndWriteToCanvas(document, container, position + 1);

            //Clean up to prevent duplicate data in following meals
            container = new LinearLayout(context);
            position++;
        }

        //write PDF to file
        try {
            pdfFile.createNewFile();
            OutputStream out = new FileOutputStream(pdfFile);
            document.writeTo(out);
            document.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send back to AsyncTask to share output file.
        return pdfFile;
    }

    private PdfDocument makePageAndWriteToCanvas(PdfDocument document, ViewGroup container, int pageNumber){
        //Set page dimensions. Can eventually be set for A4 also
        int pageWidth = 612; //8.5" * 72
        int pageHeight = 792; //11" * 72

        //create page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // draw view on the page
        int measureWidth = View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY);
        container.measure(measureWidth, measuredHeight);
        container.layout(0, 0, canvas.getWidth(), canvas.getHeight());

        container.draw(canvas);
        document.finishPage(page);

        return document;
    }

    //checks if meal details string is empty. If not, populates textView and sets to visible
    private void checkViewAndDisplay(String text, int viewInt, int titleViewInt, View container) {
        if (text != null && !text.isEmpty()) {
            TextView textView = ((TextView) container.findViewById(viewInt));
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
            if (titleViewInt != 0) {
                (container.findViewById(titleViewInt)).setVisibility(View.VISIBLE);
            }
        }
    }

    // Exporting via AsyncTask
    private class AsyncTaskHandler extends AsyncTask<Integer, Void, File> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Integer... params) {
            DBHelper db = new DBHelper(context);
            switch (params[0]) {
                case CSV_FORMAT:
                    File csvFile = makeExportFile(".csv");
                    db.exportAsCSV(csvFile);
                    return csvFile;
                case PDF_FORMAT:
                    File pdfFile = makeExportFile(".pdf");
                    pdfFile = pdfExporter(pdfFile);

                    return pdfFile;
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
