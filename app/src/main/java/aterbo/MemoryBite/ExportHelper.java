package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
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
        int headerPhotoHeight = 134;
        int headerPhotoWidth = 234;

        //Make PDF file
        try {
            pdfFile.createNewFile();
            OutputStream out = new FileOutputStream(pdfFile);

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
        int pageNumber = position + 1;

        while(position < mealCount){
            //Inflate layout to view. Attach to container as root.
            view = inflater.inflate(R.layout.layout_pdf_meal_template, container, true);

            meal = mealList.get(position);
            photos = db.getAllPhotosForMealList(meal.getMealIdNumber());

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

            //check for photos and populate if so
            if (!photos.isEmpty()) {
                String photoFilePath = photos.get(0).getPhotoFilePath();
                ImageView headerPhoto = (ImageView) container.findViewById(R.id.header_photo);
                headerPhoto.setImageBitmap(decodeSampledBitmapFromResource(photoFilePath, headerPhotoWidth, headerPhotoHeight));
            }

            TextView pageNumberView = ((TextView) container.findViewById(R.id.page_number));
            pageNumberView.setText(context.getResources().getString(R.string.page) + pageNumber);

            //Method call to make and write page
//            document = makePageAndWriteToCanvas(document, container, position + 1);

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

            //Get bottom edge of text box
            int y = container.findViewById(R.id.textBlock).getBottom();
            Log.i("MyActivity", "Bottom edge of textBlock " + y);

            if(!photos.isEmpty()){
                int neededYSpace = 450;

                //If meal has 1-2 or 5-6 photos, measure space below text and put up to two photos there.
                if(y > neededYSpace && (photos.size() < 3 || photos.size()< 5)){

                    container.findViewById(R.id.photoBlock).setVisibility(View.VISIBLE);
                    ImageView photoView;

                    int availableXSpace = 550;
                    int availableYSpace = 792-100-y;

                    if(photos.size() > 1){
                        availableXSpace = availableXSpace/2;
                        //Set Photo 2, if needed
                        photoView = (ImageView) container.findViewById(R.id.photo_2);
                        photoView.setVisibility(View.VISIBLE);
                        photoView.getLayoutParams().height = availableYSpace;
                        photoView.getLayoutParams().width = availableXSpace;
                        photoView.setImageBitmap(decodeSampledBitmapFromResource(
                                photos.get(1).getPhotoFilePath(), availableXSpace, availableYSpace));
                    }

                    photoView = (ImageView) container.findViewById(R.id.photo_1);
                    photoView.setVisibility(View.VISIBLE);
                    photoView.getLayoutParams().height = availableYSpace;
                    photoView.getLayoutParams().width = availableXSpace;
                    photoView.setImageBitmap(decodeSampledBitmapFromResource(
                            photos.get(0).getPhotoFilePath(), availableXSpace, availableYSpace));

                    container.measure(measureWidth, measuredHeight);
                    container.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                }
            }



            container.draw(canvas);
            document.finishPage(page);

            //Clean up to prevent duplicate data in following meals
            container = new LinearLayout(context);
            position++;

            //Write page to file
            try {
                document.writeTo(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        document.close();
        out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send back to AsyncTask to share output file.
        return pdfFile;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {
        //Check orientation of photo
        int orientation = getOrientation(path);


        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return rotateBitmap(BitmapFactory.decodeFile(path, options), orientation);
    }

    public static int getOrientation(String photoPath){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if(bitmap != bmRotated){
            bitmap.recycle();
            bitmap = null;
            }
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //Takes a filled out and inflated view and makes a PDF page to cram it into
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
