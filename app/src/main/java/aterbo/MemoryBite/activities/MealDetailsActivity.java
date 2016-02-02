package aterbo.MemoryBite.activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import aterbo.MemoryBite.AboutDialog;
import aterbo.MemoryBite.DBHelper;
import aterbo.MemoryBite.customviews.FullHeightGridView;
import aterbo.MemoryBite.objects.Meal;
import aterbo.MemoryBite.objects.Photo;
import aterbo.MemoryBite.adapters.PhotoGridAdapter;
import aterbo.MemoryBite.R;


public class MealDetailsActivity extends ActionBarActivity {

    private int mealIdNumber;
    private Meal meal;
    private ArrayList<Photo> photos;
    private static int SHARE_GENERAL = 1;
    private static int SHARE_W_MEMORY_BITE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meal_details);

        setButtonFont(R.id.share_meal_button);
        setButtonFont(R.id.share_meal_w_memory_bite_button);

        getMealIdNumber();
        getMealAndPhotos();

        displayMealText();

        if(hasPhotos()){
            setTitlePhoto();
            setPhotosToGridView();
        }

        setAdView(R.id.adView);
    }

    private void setButtonFont(int resourceId){
        Typeface tf = Typeface.createFromAsset(getAssets(),
                getResources().getString(R.string.default_font_file));
        Button button = (Button) findViewById(resourceId);
        button.setTypeface(tf);
    }

    private void getMealIdNumber(){
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mealIdNumber = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
        } else {
            mealIdNumber = 0;
        }
    }

    private void getMealAndPhotos(){
        DBHelper db = new DBHelper(this);
        meal = db.readMeal(mealIdNumber);
        photos = db.getAllPhotosForMealList(mealIdNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meal_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_meal_menu:
                shareMeal(SHARE_GENERAL);
                return true;
            case R.id.share_meal_w_memory_bite_menu:
                shareMeal(SHARE_W_MEMORY_BITE);
                return true;
            case R.id.edit_meal_menu:
                editMeal(mealIdNumber);
                return true;
            case R.id.delete_meal_menu:
                new DeleteMealDialog().show(getFragmentManager(), "MyDialog");
                return true;
            case R.id.add_new_meal_menu:
                Intent newMealIntent = new Intent(this, InputMealActivity.class);
                startActivity(newMealIntent);
                return true;
            case R.id.edit_last_entry_menu:
                editLastMeal();
                return true;
            case R.id.view_journal_menu:
                openMealJournal();
                return true;
            case R.id.about_menu:
                DialogFragment newFragment = AboutDialog.newInstance();
                newFragment.show(getFragmentManager(), "about");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayMealText() {
        displayJournalItemInView(meal.getRestaurantName(), R.id.restaurant_name_details, 0);
        displayJournalItemInView(meal.getDateMealEaten(), R.id.date_details, 0);
        displayJournalItemInView(meal.getDinedWith(), R.id.dined_with_details, 0);
        displayJournalItemInView(meal.getLocation(), R.id.location_details, 0);
        displayJournalItemInView(meal.getCuisineType(), R.id.cuisine_type_details, 0);
        displayJournalItemInView(meal.getAppetizersNotes(), R.id.appetizers_details, R.id.appetizers_title);
        displayJournalItemInView(meal.getMainCoursesNotes(), R.id.main_course_details, R.id.main_course_title);
        displayJournalItemInView(meal.getDessertsNotes(), R.id.dessert_details, R.id.dessert_title);
        displayJournalItemInView(meal.getDrinksNotes(), R.id.drinks_details, R.id.drinks_title);
        displayJournalItemInView(meal.getGeneralNotes(), R.id.general_notes_details, R.id.general_notes_title);
        displayJournalItemInView(meal.getAtmosphere(), R.id.atmosphere_details, 0);
        displayJournalItemInView(meal.getPrice(), R.id.price_details, 0);
    }

    private void displayJournalItemInView(String text, int viewInt, int titleViewInt) {
        if (text != null && !text.isEmpty()) {
            TextView textView = ((TextView) findViewById(viewInt));
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
            if (titleViewInt != 0) {
                (findViewById(titleViewInt)).setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean hasPhotos(){
        return !photos.isEmpty();
    }

    private void setTitlePhoto(){
        ImageLoader.getInstance().displayImage("file://" + photos.get(0).getPhotoFilePath(),
                (ImageView) findViewById(R.id.header_photo));
        (findViewById(R.id.header_photo)).setVisibility(View.VISIBLE);
    }

    private void setPhotosToGridView() {
        findViewById(R.id.photoGroup).setVisibility(View.VISIBLE);

        final PhotoGridAdapter photoGridAdapter = new PhotoGridAdapter(photos, this);
        final FullHeightGridView photoGrid = (FullHeightGridView) findViewById(R.id.meal_details_photo_grid);
        photoGrid.setAdapter(photoGridAdapter);

        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> clickListener, View view, int position, long id) {
                Intent intent = new Intent(MealDetailsActivity.this, FullScreenViewActivity.class);
                intent.putExtra("id", position);
                intent.putParcelableArrayListExtra("photoList", photos);
                startActivity(intent);
            }
        });
    }

    private void setAdView(int resourceId){
        if(findViewById(resourceId)!=null){
            AdView mAdView = (AdView) findViewById(resourceId);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void editMeal(int mealIdToEdit) {
        Intent intent = new Intent(getApplicationContext(), InputMealActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, mealIdToEdit);
        startActivity(intent);
    }

    public void shareWithFriend(View view){
        shareMeal(SHARE_GENERAL);
    }

    public void shareWithMemoryBite(View view){
        shareMeal(SHARE_W_MEMORY_BITE);
    }

    private void shareMeal(int shareType) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        if(hasPhotos()){
            sharingIntent = setPhotoShare(sharingIntent);
        } else {
            sharingIntent = setTextShare(sharingIntent);
        }

        if (shareType == SHARE_W_MEMORY_BITE){
            String[] toAddresses = getResources().getStringArray(R.array.email_array);
            sharingIntent.putExtra(Intent.EXTRA_EMAIL, toAddresses);
        }

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                meal.getEmailShareSubjectString(this));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                meal.getEmailShareBodyString(this));

        startActivity(Intent.createChooser(sharingIntent,
                getResources().getText(R.string.send_to_chooser)));
    }

    public Intent setPhotoShare(Intent sharingIntent){
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, getPrimaryPhotoUri());//Uri of image
        return sharingIntent;
    }

    public Intent setTextShare(Intent sharingIntent){
        sharingIntent.setType("text/plain");
        return sharingIntent;
    }

    private Uri getPrimaryPhotoUri(){
        String path = photos.get(0).getPhotoFilePath();
        return Uri.fromFile(new File(path));
    }

    public void editLastMeal() {
        int maxMealId = getMaxMealId();
        if (areMealsPresent(maxMealId)){
            editMeal(maxMealId);
        } else {
            showToastFromStringResource(R.string.no_meals);
        }
    }

    private int getMaxMealId(){
        DBHelper db = new DBHelper(this);
        return db.getMaxMealId();
    }

    private boolean areMealsPresent(int maxMealId){
        return (maxMealId != 0);
    }

    private void showToastFromStringResource(int stringResourceId) {
        Toast.makeText(this, getResources().getString(stringResourceId), Toast.LENGTH_LONG).show();
    }

    public static class DeleteMealDialog extends android.app.DialogFragment {
        Context mContext;

        public DeleteMealDialog() {
            mContext = getActivity();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(R.string.delete);
            alertDialogBuilder.setMessage(R.string.dialog_delete_meal);
            //doConfirmedMealDelete is if user clicks delete
            alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((MealDetailsActivity) getActivity()).doConfirmedMealDelete();
                }
            });

            alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return alertDialogBuilder.create();
        }
    }

    public void doConfirmedMealDelete() {
        DBHelper db = new DBHelper(this);
        //delete all associated photos (should be able to do this via SQLite Foreign key, but it's
        //easier and safer to do it separately
        db.deleteAllPhotosFromMeal(mealIdNumber);
        db.deleteMeal(meal);
        openMealJournal();
    }

    //Override on Back so that app always goes from Meal Details to Meal Journal
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openMealJournal();
        finish();
    }

    private void openMealJournal(){
        Intent intent = new Intent(this, ListOfMealsActivity.class);
        startActivity(intent);
    }
}
