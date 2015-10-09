package aterbo.MemoryBite;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;


public class MealDetails extends ActionBarActivity {

    private int mealIdNumber;
    private Meal meal;
    private List<Photo> photos;
    private boolean hasPhotos;

    //Trying to set ShareActionProvider. Issues with ActionBarActivity
    //private ShareActionProvider mShareActionProvider;
    //also add to .xml:
    //android:actionProviderClass="android.widget.ShareActionProvider"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meal_details);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mealIdNumber = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
        }

        DBHelper db = new DBHelper(this);
        meal = db.readMeal(mealIdNumber);
        //check for photos and populate if so
        photos = db.getAllPhotosForMealList(mealIdNumber);
        if (photos.isEmpty()) {
            hasPhotos = false;
        } else { hasPhotos = true; }

        populateLayoutWMeal();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meal_details, menu);

        /*
        //set SharingActionProvider http://android-developers.blogspot.com/2012/02/share-with-intents.html
        // Get the menu item.
        MenuItem menuItem = menu.findItem(R.id.share_meal_menu);
        // Get the provider and hold onto it to set/change the share intent.
        //MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);
        mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        mShareActionProvider.setShareIntent(shareMealIntent());
        */

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.share_meal_menu:
                shareMeal();
                return true;
            case R.id.edit_meal_menu:
                editMeal();
                return true;
            case R.id.delete_meal_menu:
                new DeleteMealDialog().show(getFragmentManager(), "MyDialog");
                return true;
            case R.id.add_new_meal_menu:
                Intent newMealIntent = new Intent(this, InputMeal.class);
                startActivity(newMealIntent);
                return true;
            case R.id.edit_last_entry_menu:
                editLastMeal();
                return true;
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

    private void populateLayoutWMeal() {
        checkViewAndDisplay(meal.getRestaurantName(), R.id.restaurant_name_details, 0);
        checkViewAndDisplay(meal.getDateMealEaten(), R.id.date_details, 0);
        checkViewAndDisplay(meal.getDinedWith(), R.id.dined_with_details, 0);
        checkViewAndDisplay(meal.getLocation(), R.id.location_details, 0);
        checkViewAndDisplay(meal.getCuisineType(), R.id.cuisine_type_details, 0);
        checkViewAndDisplay(meal.getAppetizersNotes(), R.id.appetizers_details, R.id.appetizers_title);
        checkViewAndDisplay(meal.getMainCoursesNotes(), R.id.main_course_details, R.id.main_course_title);
        checkViewAndDisplay(meal.getDessertsNotes(), R.id.dessert_details, R.id.dessert_title);
        checkViewAndDisplay(meal.getDrinksNotes(), R.id.drinks_details, R.id.drinks_title);
        checkViewAndDisplay(meal.getGeneralNotes(), R.id.general_notes_details, R.id.general_notes_title);
        checkViewAndDisplay(meal.getAtmosphere(), R.id.atmosphere_details, 0);
        checkViewAndDisplay(meal.getPrice(), R.id.price_details, 0);


        //Set Title Picture and photo grid if meal has photos
        if (hasPhotos) {
            //show layout box
            findViewById(R.id.photoGroup).setVisibility(View.VISIBLE);

            //Display title using UIL
            ImageLoader.getInstance().displayImage("file://" + photos.get(0).getPhotoFilePath(),
                    (ImageView) findViewById(R.id.header_photo));
            ((ImageView) findViewById(R.id.header_photo)).setVisibility(View.VISIBLE);

            //Set photo grid
            setPhotosToGridView();
        }
    }

    //Set photos to grid view
    private void setPhotosToGridView() {
        final PhotoGridAdaptor photoGridAdaptor = new PhotoGridAdaptor(photos, this);
        final FullHeightGridView photoGrid = (FullHeightGridView) findViewById(R.id.meal_details_photo_grid);
        photoGrid.setAdapter(photoGridAdaptor);

        //Set click listener and open selected photo via Intent on click
        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> clickListener, View view, int position, long id) {
                Photo chosenPhoto = photoGridAdaptor.getPhoto(position);
                String photoFilePath = chosenPhoto.getPhotoFilePath();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(photoFilePath)), "image/*");
                startActivity(intent);
            }
        });
    }

    //checks if meal details string is empty. If not, populates textView and sets to visible
    private void checkViewAndDisplay(String text, int viewInt, int titleViewInt) {
        if (text != null && !text.isEmpty()) {
            TextView textView = ((TextView) findViewById(viewInt));
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
            if (titleViewInt != 0) {
                ((TextView) findViewById(titleViewInt)).setVisibility(View.VISIBLE);
            }
        }
    }

    private void editMeal() {
        Intent intent = new Intent(getApplicationContext(), InputMeal.class)
                .putExtra(Intent.EXTRA_TEXT, mealIdNumber);
        startActivity(intent);
    }


    //share this meal!
    private void shareMeal() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        if (photos.get(0) != null) {
            String path = photos.get(0).getPhotoFilePath();

            //Set type of share to image and add URI
            sharingIntent.setType("image/*");
            Uri imgUri = Uri.fromFile(new File(path));//Absolute Path of image
            sharingIntent.putExtra(Intent.EXTRA_STREAM, imgUri);//Uri of image
        } else {
            sharingIntent.setType("text/plain");
        }

        //create subject
        String shareSubject = meal.getEmailShareSubjectString(this);
        //create body
        String shareBody = meal.getEmailShareBodyString(this);

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.send_to_chooser)));

    }

    public void doPositiveClick() {
        DBHelper db = new DBHelper(this);
        //delete all assocaited photos (should be able to do this via SQLite Foreign key, but it's
        //easier and safer to do it separately
        db.deleteAllPhotosFromMeal(mealIdNumber);
        db.deleteMeal(meal);
        Intent intent = new Intent(this, ListOfMeals.class);
        startActivity(intent);
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
            //doPositiveClick is if user clicks delete
            alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((MealDetails) getActivity()).doPositiveClick();
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

    public void editLastMeal(){
        DBHelper db = new DBHelper(this);
        int maxId = db.getMaxMealId();

        //test if 0 is returned, showing no meals entered. if so, toast!!
        if (maxId == 0){
            Toast.makeText(this, getResources().getString(R.string.no_meals), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), InputMeal.class)
                    .putExtra(Intent.EXTRA_TEXT, maxId);
            startActivity(intent);
        }
    }
}
