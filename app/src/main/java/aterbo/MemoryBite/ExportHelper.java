package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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

import aterbo.MemoryBite.objects.Meal;
import aterbo.MemoryBite.objects.Photo;

/**
 * Created by ATerbo on 11/24/15.
 */
public class ExportHelper {

    private final int CSV_FORMAT = 1;
    private final int PDF_FORMAT = 2;
    private Context context;
    private String shareType;
    private String PRINT_SERVICE = "PrintService";
    //Progress Dialog 
    private ProgressDialog dialog;

    public ExportHelper(Context context){
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    private static int calculateInSampleSize(
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

    private static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
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

    private static int getOrientation(String photoPath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
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
            if (bitmap != bmRotated) {
                bitmap.recycle();
                bitmap = null;
            }
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public void chooseExportType() {
        CharSequence exportOptions[] = new CharSequence[]{
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

    private File makeExportFile(String fileExtension) {
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
        int headerPhotoHeight = 144;
        int headerPhotoWidth = 216;
        //Set page dimensions. Can eventually be set for A4 also
        int pageWidth = 612; //8.5" * 72
        int pageHeight = 792; //11" * 72
        int maxYSpaceToFitPhotos = 450; // Maximum y position before there is not enough space for photos

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

            while (position < mealCount) {
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
                    headerPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    headerPhoto.setImageBitmap(decodeSampledBitmapFromResource(photoFilePath, headerPhotoWidth, headerPhotoHeight));
                }

                TextView pageNumberView = ((TextView) container.findViewById(R.id.page_number));
                pageNumberView.setText(context.getResources().getString(R.string.page) + pageNumber);

                //create page
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                // draw view on the page
                int measureWidth = View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY);
                int measuredHeight = View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY);
                container.measure(measureWidth, measuredHeight);
                container.layout(0, 0, canvas.getWidth(), canvas.getHeight());

                if (!photos.isEmpty()) {

                    //Get bottom edge of text box
                    int y = container.findViewById(R.id.textBlock).getBottom();
                    Log.i("MyActivity", "Bottom edge of textBlock " + y);

                    //If meal has 1-2 or 5-6 photos, measure space below text and put up to two photos there.
                    if (y <= maxYSpaceToFitPhotos) {
                        container.findViewById(R.id.photoBlock).setVisibility(View.VISIBLE);

                        int availableXSpace = pageWidth - 54;
                        int availableYSpace = pageHeight - 100 - y;

                        switch (photos.size()) {
                            case 1: //If one photo set to first page
                                setPhotoToView(photos.get(0).getPhotoFilePath(), availableXSpace,
                                        availableYSpace, (ImageView) container.findViewById(R.id.photo_1));
                                break;
                            case 2: //If 2, 5, or 6 photos set both to first page
                            case 5:
                            case 6:
                                availableXSpace = availableXSpace / 2;
                                setPhotoToView(photos.get(0).getPhotoFilePath(), availableXSpace,
                                        availableYSpace, (ImageView) container.findViewById(R.id.photo_1));
                                //Set Photo 2, if needed
                                setPhotoToView(photos.get(1).getPhotoFilePath(), availableXSpace,
                                        availableYSpace, (ImageView) container.findViewById(R.id.photo_2));
                                break;
                            case 3: //If 3 or 4 photo set only one to first page
                            case 4:
                                setPhotoToView(photos.get(0).getPhotoFilePath(), availableXSpace,
                                        availableYSpace, (ImageView) container.findViewById(R.id.photo_1));
                                break;
                        }
                        container.measure(measureWidth, measuredHeight);
                        container.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                    }

                    //Test if a second photo page is needed
                    if (y > maxYSpaceToFitPhotos || photos.size() >= 3) {
                        //Finish first page, write to file, and start second
                        container.draw(canvas);
                        document.finishPage(page);
                        //Write page to file
                        try {
                            document.writeTo(out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Clean up to prevent duplicate data in following meals
                        container = new LinearLayout(context);
                        //Increase page number
                        pageNumber++;

                        //Inflate Photo Page layout
                        //Inflate layout to view. Attach to container as root.
                        view = inflater.inflate(R.layout.layout_pdf_photo_page_template, container, true);

                        pageNumberView = ((TextView) container.findViewById(R.id.page_number));
                        pageNumberView.setText(context.getResources().getString(R.string.page) + pageNumber);

                        int photoWidth = pageWidth - 64;
                        int photoHeight = pageHeight - 110;
                        LinearLayout column1 = (LinearLayout) container.findViewById(R.id.column_1);
                        LinearLayout column2 = (LinearLayout) container.findViewById(R.id.column_2);

                        //Test if any photos were placed on first page and update container appropriately
                        if(y > maxYSpaceToFitPhotos){ //No photos on first page (leftover space too small)
                            switch (photos.size()) {
                                case 1:
                                    setColumnWidth(column1, column2, pageWidth - 54, false);
                                    setPhotoToView(photos.get(0).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    break;
                                case 2:
                                    photoHeight = photoHeight / 2;
                                    setColumnWidth(column1, column2, pageWidth - 54, false);

                                    setPhotoToView(photos.get(0).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    break;
                                case 3:
                                    photoHeight = photoHeight / 3;
                                    setColumnWidth(column1, column2, pageWidth - 54, false);

                                    setPhotoToView(photos.get(0).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_3));
                                    break;
                                case 4:
                                    photoHeight = photoHeight / 2;
                                    photoWidth = photoWidth / 2;
                                    setColumnWidth(column1, column2, (pageWidth - 54) / 2, true);

                                    setPhotoToView(photos.get(0).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_4));
                                    setPhotoToView(photos.get(3).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_5));
                                    break;
                                case 5:
                                    photoHeight = photoHeight / 3;
                                    photoWidth = photoWidth / 2;
                                    setColumnWidth(column1, column2, (pageWidth - 54) / 2, true);

                                    setPhotoToView(photos.get(0).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_3));
                                    setPhotoToView(photos.get(3).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_4));
                                    setPhotoToView(photos.get(4).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_5));
                                    break;
                                case 6:
                                    photoHeight = photoHeight / 3;
                                    photoWidth = photoWidth / 2;
                                    setColumnWidth(column1, column2, (pageWidth - 54) / 2, true);

                                    setPhotoToView(photos.get(0).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_3));
                                    setPhotoToView(photos.get(3).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_4));
                                    setPhotoToView(photos.get(4).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_5));
                                    setPhotoToView(photos.get(5).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_6));
                                    break;
                            }
                        } else { //Photos placed on first page
                            switch (photos.size()){
                                case 3:
                                    photoHeight = photoHeight / 2;
                                    setColumnWidth(column1, column2, pageWidth - 54, false);
                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    break;
                                case 4:
                                    photoHeight = photoHeight / 3;
                                    setColumnWidth(column1, column2, (pageWidth - 54), false);

                                    setPhotoToView(photos.get(1).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(3).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_3));
                                    break;
                                case 5:
                                    photoHeight = photoHeight / 3;
                                    setColumnWidth(column1, column2, (pageWidth - 54), false);

                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(3).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(4).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_3));
                                    break;
                                case 6:
                                    photoHeight = photoHeight / 2;
                                    photoWidth = photoWidth / 2;
                                    setColumnWidth(column1, column2, (pageWidth - 54) / 2, true);

                                    setPhotoToView(photos.get(2).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_1));
                                    setPhotoToView(photos.get(3).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_2));
                                    setPhotoToView(photos.get(4).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_4));
                                    setPhotoToView(photos.get(5).getPhotoFilePath(), photoWidth,
                                            photoHeight, (ImageView) container.findViewById(R.id.photo_5));
                                    break;
                            }
                        }

                        //create page
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();

                        // draw view on the page
                        measureWidth = View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY);
                        measuredHeight = View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY);
                        container.measure(measureWidth, measuredHeight);
                        container.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                    }
                }


                container.draw(canvas);
                document.finishPage(page);

                //Clean up to prevent duplicate data in following meals
                container = new LinearLayout(context);
                position++;
                //Increase page number
                pageNumber++;

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

    private void setColumnWidth(LinearLayout column1, LinearLayout column2, int width, boolean areTwo) {
        column1.getLayoutParams().width = width;
        if (areTwo) {
            column2.setVisibility(View.VISIBLE);
            column2.getLayoutParams().width = width;
        }
    }

    private void setPhotoToView(String photoPath, int width, int height, ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        imageView.getLayoutParams().width = width;
        imageView.getLayoutParams().height = height;
        imageView.setImageBitmap(decodeSampledBitmapFromResource(photoPath, width, height));
    }

    //Takes a filled out and inflated view and makes a PDF page to cram it into
    private PdfDocument makePageAndWriteToCanvas(PdfDocument document, ViewGroup container, int pageNumber) {
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
            dialog.setMessage(context.getResources().getString(R.string.busy_exporting_message));
            dialog.show();
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

            if(dialog.isShowing()){
                dialog.dismiss();
            }

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
