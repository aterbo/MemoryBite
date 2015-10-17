package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class InputMeal extends ActionBarActivity {
    private DBHelper db = new DBHelper(this);
    private Meal meal;
    private List<Photo> photos;
    private boolean hasPhotos;
    private int mealIdNumber = -1;

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);

        //Setting appetizer, mains, desserts, drinks, and notes to have multiline input on display
        //but to also have Next button
        setNextWithWordWrap((EditText) findViewById(R.id.appetizers_input));
        setNextWithWordWrap((EditText) findViewById(R.id.main_courses_input));
        setNextWithWordWrap((EditText) findViewById(R.id.desserts_input));
        setNextWithWordWrap((EditText) findViewById(R.id.drinks_input));
        setNextWithWordWrap((EditText) findViewById(R.id.notes_input));

        //Set button font to fancy type
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushanscript.ttf");
        Button button = (Button) findViewById(R.id.save_button);
        button.setTypeface(tf);
        button = (Button) findViewById(R.id.photo_button);
        button.setTypeface(tf);

        //Check for intent and if one present load meal. If no intent, new meal
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mealIdNumber = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
            DBHelper db = new DBHelper(this);
            meal = db.readMeal(mealIdNumber);

            //Get all photos and check for photos exist
            photos = db.getAllPhotosForMealList(mealIdNumber);
            if (photos.isEmpty()) {
                hasPhotos = false;
            } else {
                hasPhotos = true;
            }

            setMealToUI();

        } else {
            meal = new Meal();
            photos = new ArrayList<>();
            EditText dateBox = (EditText) findViewById(R.id.meal_date);
            dateBox.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        }

        setMealDatePicker(findViewById(R.id.meal_date));
    }

    private void setMealDatePicker(final View edittext) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                TextView edTextView = (TextView) findViewById(R.id.meal_date);
                edTextView.setText(sdf.format(myCalendar.getTime()));
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(InputMeal.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    //http://stackoverflow.com/questions/5014219/multiline-edittext-with-done-softinput-action-label-on-2-3
    //Setting the larger EditText boxes to display wordwrapping while showing a next key
    private void setNextWithWordWrap(EditText editText) {
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(Integer.MAX_VALUE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_meal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.view_journal_menu:
                Intent viewJournalIntent = new Intent(this, ListOfMeals.class);
                startActivity(viewJournalIntent);
                return true;
            case R.id.about_menu:
                DialogFragment newFragment = AboutDialog.newInstance();
                newFragment.show(getFragmentManager(), "about");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Sets pre-existing meal data to UI views (if in edit existing meal mode)
    private void setMealToUI() {
        ((TextView) findViewById(R.id.restaurant_name_input)).setText(meal.getRestaurantName());
        ((TextView) findViewById(R.id.meal_date)).setText(meal.getDateMealEaten());
        ((TextView) findViewById(R.id.location_input)).setText(meal.getLocation());
        ((TextView) findViewById(R.id.cuisine_type_input)).setText(meal.getCuisineType());
        ((TextView) findViewById(R.id.appetizers_input)).setText(meal.getAppetizersNotes());
        ((TextView) findViewById(R.id.main_courses_input)).setText(meal.getMainCoursesNotes());
        ((TextView) findViewById(R.id.desserts_input)).setText(meal.getDessertsNotes());
        ((TextView) findViewById(R.id.drinks_input)).setText(meal.getDrinksNotes());
        ((TextView) findViewById(R.id.notes_input)).setText(meal.getGeneralNotes());
        ((TextView) findViewById(R.id.dined_with_input)).setText(meal.getDinedWith());


        //set radio buttons
        //Atmosphere
        if (meal.getAtmosphere() != null && !meal.getAtmosphere().isEmpty()) {
            if (meal.getAtmosphere().equals(getString(R.string.atmosphere_1))) {
                ((RadioButton) findViewById(R.id.atmosphere_1)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_2))) {
                ((RadioButton) findViewById(R.id.atmosphere_2)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_3))) {
                ((RadioButton) findViewById(R.id.atmosphere_3)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_4))) {
                ((RadioButton) findViewById(R.id.atmosphere_4)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_5))) {
                ((RadioButton) findViewById(R.id.atmosphere_5)).setChecked(true);
            }
        }
        //set radio buttons
        //Price
        if (meal.getPrice() != null && !meal.getPrice().isEmpty()) {
            if (meal.getPrice().equals(getString(R.string.price_1))) {
                ((RadioButton) findViewById(R.id.price_1)).setChecked(true);
            } else if (meal.getPrice().equals(getString(R.string.price_2))) {
                ((RadioButton) findViewById(R.id.price_2)).setChecked(true);
            } else if (meal.getPrice().equals(getString(R.string.price_3))) {
                ((RadioButton) findViewById(R.id.price_3)).setChecked(true);
            } else if (meal.getPrice().equals(getString(R.string.price_4))) {
                ((RadioButton) findViewById(R.id.price_4)).setChecked(true);
            } else if (meal.getPrice().equals(getString(R.string.price_5))) {
                ((RadioButton) findViewById(R.id.price_5)).setChecked(true);
            }
        }

        //Set Photos to Grid View, if present
        if (hasPhotos) {
            setPhotosToGridView();
        }
    }

    //Set photos to grid view
    private void setPhotosToGridView() {
        final PhotoGridAdaptor photoGridAdaptor = new PhotoGridAdaptor(photos, this);
        final FullHeightGridView photoGrid = (FullHeightGridView) findViewById(R.id.meal_input_photo_grid);
        photoGrid.setAdapter(photoGridAdaptor);

        findViewById(R.id.photoGroup).setVisibility(View.VISIBLE);

        photoGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                photos.remove(position);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.photo_removed),
                        Toast.LENGTH_SHORT).show();
                photoGridAdaptor.notifyDataSetChanged();
                return true;
            }
        });

    }

    //Set entered data to meal (pulls data from UI views)
    private void setInputToMeal() {
        EditText inputText = (EditText) findViewById(R.id.restaurant_name_input);
        meal.setRestaurantName(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.meal_date);
        meal.setDateMealEaten(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.location_input);
        meal.setLocation(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.cuisine_type_input);
        meal.setCuisineType(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.appetizers_input);
        meal.setAppetizersNotes(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.main_courses_input);
        meal.setMainCoursesNotes(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.desserts_input);
        meal.setDessertsNotes(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.drinks_input);
        meal.setDrinksNotes(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.notes_input);
        meal.setGeneralNotes(inputText.getText().toString());

        inputText = (EditText) findViewById(R.id.dined_with_input);
        meal.setDinedWith(inputText.getText().toString());

        //Get Atmosphere radio result
        RadioButton inputRadioButton;
        RadioGroup inputRadioGroup = (RadioGroup) findViewById(R.id.atmosphere_radio);
        //test if checked and if so set Atmosphere
        if (inputRadioGroup.getCheckedRadioButtonId() != -1) {
            inputRadioButton = (RadioButton) findViewById(inputRadioGroup.getCheckedRadioButtonId());
            meal.setAtmosphere(inputRadioButton.getText().toString());
        }

        //Get price radio result
        inputRadioGroup = (RadioGroup) findViewById(R.id.price_radio);
        //test if checked and if so set Price
        if (inputRadioGroup.getCheckedRadioButtonId() != -1) {
            inputRadioButton = (RadioButton) findViewById(inputRadioGroup.getCheckedRadioButtonId());
            meal.setPrice(inputRadioButton.getText().toString());
        }

    }

    private int numberOfPhotos() {
        if (photos != null) {
            return photos.size();
        } else {
            return 0;
        }
    }


    //Save button clicked
    public void saveMeal(View view) {

        //Copy input from layout and assign to meal
        setInputToMeal();

        //check if there is data to save
        if (meal.isDataFilledOut() || numberOfPhotos() > 0) {

            //Save photo 1 to primary photo in meal prior to updating meal DB
            //if no photos, ensure to clear any existing primary photo if was deleted during upgrayedd
            if (numberOfPhotos() > 0) {
                meal.setPrimaryPhoto(photos.get(0).getPhotoFilePath());
            } else {
                meal.setPrimaryPhoto("");
            }

            //if mealIdNumber == -1 means that this is a new meal
            if (mealIdNumber == -1) {
                mealIdNumber = db.createMeal(meal);
                meal.setMealIdNumber(mealIdNumber);
            } else {
                //If mealIdNumber is NOT -1, then that means a meal exists and the meal is updated
                //to the existing ID number
                meal.setMealIdNumber(mealIdNumber);
                db.updateMeal(meal);
                //In order to ensure proper update, delete all existing photos for
                //meal number and replace with new photos
                db.deleteAllPhotosFromMeal(mealIdNumber);
            }

            //save photos once meal is saved and mealIdNumber is confirmed
            savePhotos();

            Toast.makeText(this, getResources().getText(R.string.saved_meal_message), Toast.LENGTH_LONG).show();

            //start Details view w/ new or updated meal
            Intent intent = new Intent(getApplicationContext(), MealDetails.class)
                    .putExtra(Intent.EXTRA_TEXT, mealIdNumber);
            startActivity(intent);
        } else { //Make a toast if the IDIOT USER tries saving a meal without entering anything

            Toast.makeText(this, getResources().getText(R.string.no_data_no_save_message), Toast.LENGTH_LONG).show();

        }
    }

    //save photos via enhanced for loop through photo list
    private void savePhotos() {
        if (numberOfPhotos() > 0) {
            for (Photo photo : photos) {
                photo.setAssociatedMealIdNumber(mealIdNumber);
                db.createPhoto(photo);
            }
        }
    }

    //Photo button clicked (choose from gallery
    public void addPictureButtonClick(View view) {

        if (numberOfPhotos() < getResources().getInteger(R.integer.max_number_of_photos)) {
            selectImage();
        } else {
            Toast.makeText(this, getResources().getString(R.string.cant_add_photos), Toast.LENGTH_LONG).show();
        }


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private static int REQUEST_CAMERA = 1;
    private static int SELECT_FILE = 2;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    File f;

                    try {
                        f = createImageFile();
                        mCurrentPhotoPath = f.getAbsolutePath();
                        photoPath = f.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath = null;
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        //Check if MemoryBite subfolder exists in gallery and create if needed
        // Get the directory for the user's public pictures directory.
        boolean success = true;
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MemoryBite/");
        if (!folder.exists()) {
            success = folder.mkdirs();
            Log.e("Album Creation", "Directory not created");
        }
        if (success) { //if folder creation works or folder exists return MemoryBite folder
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
            File imageF = new File(folder, imageFileName);
            return imageF;
        } else { //else return non-MemoryBite folder
            // Do something else on failure
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
            File imageF = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES),imageFileName);
            return imageF;
        }
    }


    /* Photo album for this application */
    private String getAlbumName() {
        return "MemoryBite";
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImageUri,
                        filePathColumn, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                // String picturePath contains the path of selected Image
                photoPath = cursor.getString(columnIndex);
                cursor.close();

            }
            //Create photo object
            Photo newPhoto = new Photo(photoPath);
            //Add returned photo to end of photos list
            photos.add(newPhoto);
            //Redraw gridview
            setPhotosToGridView();
        }
    }
}