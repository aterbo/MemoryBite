package aterbo.MemoryBite.activities;

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
import android.text.InputType;
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
import java.util.Locale;

import aterbo.MemoryBite.AboutDialog;
import aterbo.MemoryBite.DBHelper;
import aterbo.MemoryBite.customviews.FullHeightGridView;
import aterbo.MemoryBite.objects.Meal;
import aterbo.MemoryBite.objects.Photo;
import aterbo.MemoryBite.adapters.PhotoGridAdapter;
import aterbo.MemoryBite.R;


public class InputMealActivity extends ActionBarActivity {
    private DBHelper db = new DBHelper(this);
    private Meal meal;
    private ArrayList<Photo> photos;
    private int mealIdNumber = -1;
    static final String STATE_MEAL_ID = "mealIdNumber";
    static final String STATE_MEAL = "meal";
    static final String STATE_PHOTO_LIST = "photoList";
    static final String STATE_PHOTO_PATH = "photoPath";
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_meal);
        setButtonFont(R.id.save_button);

        setInputToWordWrapWithNext(R.id.appetizers_input);
        setInputToWordWrapWithNext(R.id.main_courses_input);
        setInputToWordWrapWithNext(R.id.desserts_input);
        setInputToWordWrapWithNext(R.id.drinks_input);
        setInputToWordWrapWithNext(R.id.notes_input);

        setMealDatePicker(findViewById(R.id.meal_date));

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mealIdNumber = savedInstanceState.getInt(STATE_MEAL_ID);
            meal = savedInstanceState.getParcelable(STATE_MEAL);
            photos = savedInstanceState.getParcelableArrayList(STATE_PHOTO_LIST);
            photoPath = savedInstanceState.getString(STATE_PHOTO_PATH);

            setMealToUI();
        } else { //continue create if not from bundle

            //Check for intent and if one present load meal. If no intent, new meal
            Intent intent = this.getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mealIdNumber = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
                DBHelper db = new DBHelper(this);
                meal = db.readMeal(mealIdNumber);

                //Get all photos and check for photos exist
                photos = (ArrayList) db.getAllPhotosForMealList(mealIdNumber);

                setMealToUI();

            } else { //no intent, make no meal
                meal = new Meal();
                photos = new ArrayList<>();
                EditText dateBox = (EditText) findViewById(R.id.meal_date);
                dateBox.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            }
        }
    }

    private boolean hasPhotos(){
        return !photos.isEmpty();
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
                new DatePickerDialog(InputMealActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void setButtonFont(int resourceId){
        Typeface tf = Typeface.createFromAsset(getAssets(),
                getResources().getString(R.string.default_font_file));
        Button button = (Button) findViewById(resourceId);
        button.setTypeface(tf);
    }

    //http://stackoverflow.com/questions/5014219/multiline-edittext-with-done-softinput-action-label-on-2-3
    private void setInputToWordWrapWithNext(int viewResourceId) {
        EditText editText = (EditText) findViewById(viewResourceId);
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(Integer.MAX_VALUE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_input_meal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_journal_menu:
                Intent viewJournalIntent = new Intent(this, ListOfMealsActivity.class);
                startActivity(viewJournalIntent);
                return true;
            case R.id.about_menu:
                DialogFragment newFragment = AboutDialog.newInstance();
                newFragment.show(getFragmentManager(), "about");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

        setAtmosphereRadioButtons();
        setPriceRadioButtons();

        if(hasPhotos()) {
            setPhotosToGridView();
        }
    }

    private void setAtmosphereRadioButtons(){
        String atmosphere = meal.getAtmosphere();

        if (atmosphere != null && !atmosphere.isEmpty()) {
            if (atmosphere.equals(getString(R.string.atmosphere_1))) {
                ((RadioButton) findViewById(R.id.atmosphere_1)).setChecked(true);
            } else if (atmosphere.equals(getString(R.string.atmosphere_2))) {
                ((RadioButton) findViewById(R.id.atmosphere_2)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_3))) {
                ((RadioButton) findViewById(R.id.atmosphere_3)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_4))) {
                ((RadioButton) findViewById(R.id.atmosphere_4)).setChecked(true);
            } else if (meal.getAtmosphere().equals(getString(R.string.atmosphere_5))) {
                ((RadioButton) findViewById(R.id.atmosphere_5)).setChecked(true);
            }
        }
    }

    private void setPriceRadioButtons() {
        String price = meal.getPrice();

        if (price != null && !price.isEmpty()) {
            if (price.equals(getString(R.string.price_1))) {
                ((RadioButton) findViewById(R.id.price_1)).setChecked(true);
            } else if (price.equals(getString(R.string.price_2))) {
                ((RadioButton) findViewById(R.id.price_2)).setChecked(true);
            } else if (price.equals(getString(R.string.price_3))) {
                ((RadioButton) findViewById(R.id.price_3)).setChecked(true);
            } else if (price.equals(getString(R.string.price_4))) {
                ((RadioButton) findViewById(R.id.price_4)).setChecked(true);
            } else if (price.equals(getString(R.string.price_5))) {
                ((RadioButton) findViewById(R.id.price_5)).setChecked(true);
            }
        }
    }

    //Set photos to grid view
    private void setPhotosToGridView() {
        final PhotoGridAdapter photoGridAdapter = new PhotoGridAdapter(photos, this);
        final FullHeightGridView photoGrid = (FullHeightGridView) findViewById(R.id.meal_input_photo_grid);
        photoGrid.setAdapter(photoGridAdapter);

        findViewById(R.id.photoGroup).setVisibility(View.VISIBLE);

        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                           final int position, long arg3) {

                CharSequence photoOptions[] = new CharSequence[] {
                        getResources().getString(R.string.add_caption),
                        getResources().getString(R.string.set_as_primary),
                        getResources().getString(R.string.remove),
                        getResources().getString(R.string.cancel)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(InputMealActivity.this);
                builder.setTitle(getResources().getString(R.string.photo_options));
                builder.setIcon(R.drawable.mbicon);
                builder.setItems(photoOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                addCaptionToPhoto(position);
                                break;
                            case 1:
                                setPhotoAsPrimary(position);
                                photoGridAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                photos.remove(position);
                                showToastFromStringResource(R.string.photo_removed);
                                photoGridAdapter.notifyDataSetChanged();
                                break;
                            case 4:
                                break;
                        }
                    }
                });
            builder.show();
            }
        });
    }

    private void addCaptionToPhoto(final int position){
        AlertDialog.Builder addCaptionDialog = new AlertDialog.Builder(InputMealActivity.this);
        addCaptionDialog.setTitle(getResources().getString(R.string.add_caption));

        // Set up the input
        final EditText input = new EditText(InputMealActivity.this);

        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        addCaptionDialog.setView(input);

        String existingCaption = photos.get(position).getPhotoCaption();
        input.setText(existingCaption);

        // Set up the buttons
        addCaptionDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCaption = input.getText().toString();
                photos.get(position).setPhotoCaption(newCaption);
            }
        });
        addCaptionDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        addCaptionDialog.show();
    }

    private void setPhotoAsPrimary(final int position){
        if (position != 0) {
            Photo holderPhoto = photos.get(position);
            photos.remove(position);
            photos.add(0, holderPhoto);
            showToastFromStringResource(R.string.photo_set_as_primary);
        } else {
            showToastFromStringResource(R.string.photo_already_primary);
        }
    }

    private void setInputToMeal() {
        meal.setRestaurantName(getStringFromViewId(R.id.restaurant_name_input));
        meal.setDateMealEaten(getStringFromViewId(R.id.meal_date));
        meal.setLocation(getStringFromViewId(R.id.location_input));
        meal.setCuisineType(getStringFromViewId(R.id.cuisine_type_input));
        meal.setAppetizersNotes(getStringFromViewId(R.id.appetizers_input));
        meal.setMainCoursesNotes(getStringFromViewId(R.id.main_courses_input));
        meal.setDessertsNotes(getStringFromViewId(R.id.desserts_input));
        meal.setDrinksNotes(getStringFromViewId(R.id.drinks_input));
        meal.setGeneralNotes(getStringFromViewId(R.id.notes_input));
        meal.setDinedWith(getStringFromViewId(R.id.dined_with_input));

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

    private String getStringFromViewId(int editTextViewId){
        EditText inputText = (EditText) findViewById(editTextViewId);
        return inputText.getText().toString();
    }

    private int numberOfPhotos() {
        if (photos != null) {
            return photos.size();
        } else {
            return 0;
        }
    }

    public void saveMeal(View view) {
        setInputToMeal();

        if (isMealEntryComplete()) {
            setPrimaryPhotoToMeal();

            if (isNewMeal()){
                mealIdNumber = db.createMeal(meal);
                meal.setMealIdNumber(mealIdNumber);
            } else {
                meal.setMealIdNumber(mealIdNumber);
                db.updateMeal(meal);
                //In order to ensure proper update, delete all existing photos for
                //meal number and replace with new photos
                db.deleteAllPhotosFromMeal(mealIdNumber);
            }

            //save photos once meal is saved and mealIdNumber is confirmed
            savePhotos();

            showToastFromStringResource(R.string.saved_meal_message);
            openMealDetailActivity();
            finish();
        } else {
            showToastFromStringResource(R.string.no_data_no_save_message);
        }
    }

    private boolean isMealEntryComplete(){
        return (meal.isDataFilledOut() || numberOfPhotos() > 0);
    }

    private void setPrimaryPhotoToMeal(){
        if (numberOfPhotos() > 0) {
            meal.setPrimaryPhoto(photos.get(0).getPhotoFilePath());
        } else {
            meal.setPrimaryPhoto("");
        }
    }

    private boolean isNewMeal(){
        return (mealIdNumber == -1); //if mealIdNumber == -1 means that this is a new meal
    }

    private void savePhotos() {
        if (numberOfPhotos() > 0) {
            for (Photo photo : photos) {
                photo.setAssociatedMealIdNumber(mealIdNumber);
                db.createPhoto(photo);
            }
        }
    }

    private void openMealDetailActivity(){
        Intent intent = new Intent(this, MealDetailsActivity.class)
                .putExtra(Intent.EXTRA_TEXT, mealIdNumber);
        startActivity(intent);
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
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    //Test if number of photos is under limit
    private boolean canAddMorePhotos() {
        if (numberOfPhotos() < getResources().getInteger(R.integer.max_number_of_photos)) {
            return true;
        } else {
            showToastFromStringResource(R.string.cant_add_photos);
            return false;
        }
    }

    public void takeNewPhoto(View view){
        if(canAddMorePhotos()){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File cameraPicFile = null;
                try {
                    cameraPicFile = createImageFile();
                    photoPath = cameraPicFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                    cameraPicFile = null;
                    photoPath = null;
                }
                if (cameraPicFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPicFile));
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                } else {
                    showToastFromStringResource(R.string.error_taking_photo);
                    photoPath = null;

                }
            }
        }
    }

    public void choosePhotoFromGallery(View view){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
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
                    Environment.DIRECTORY_PICTURES), imageFileName);
            return imageF;
        }
    }

    //Handling return from camera or gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                File f = new File(photoPath);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_PHOTO_PATH, photoPath);
        savedInstanceState.putInt(STATE_MEAL_ID, mealIdNumber);
        savedInstanceState.putParcelable(STATE_MEAL, meal);
        savedInstanceState.putParcelableArrayList(STATE_PHOTO_LIST, photos);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void showToastFromStringResource(int stringResourceId) {
        Toast.makeText(this, getResources().getString(stringResourceId), Toast.LENGTH_LONG).show();
    }
}