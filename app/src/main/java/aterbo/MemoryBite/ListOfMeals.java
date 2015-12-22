package aterbo.MemoryBite;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;
import java.util.Random;


public class ListOfMeals extends ActionBarActivity {

    private List<Meal> mealList;
    MealListAdaptor mealListAdaptor;
    private int resID;
    static final String STATE_RESID = "imageResID";
    static final String STATE_SORT_SETTING = "sortSetting";
    private String sortColumn;
    static final String STATE_SORT_ORDER = "sortOrder";
    private Boolean isAscending;
    private ListView mealsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_meals);

        //address saved instance state to deal with screen rotation
        if (savedInstanceState != null) {
            //get INT from saved state
            resID = savedInstanceState.getInt(STATE_RESID);
            sortColumn = savedInstanceState.getString(STATE_SORT_SETTING);
            isAscending = savedInstanceState.getBoolean(STATE_SORT_ORDER);
        } else {
            //Set random header image based on files in drawable folder and value array
            final TypedArray imgs = getResources().obtainTypedArray(R.array.headerimages);
            final Random rand = new Random();
            final int rndInt = rand.nextInt(imgs.length());
            resID = imgs.getResourceId(rndInt, 0);
            sortColumn = DBContract.MealDBTable.COLUMN_DATE;
            isAscending = false;
            displayListView();

        }
        ((ImageView) findViewById(R.id.header_photo)).setImageResource(resID);

        //AD MONEY AD MONEY
        if(findViewById(R.id.adView)!=null) {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onResume() {
        displayListView();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_of_meals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.search_meals_menu:
                if(findViewById(R.id.search_meals_box).getVisibility() == View.GONE) {
                    findViewById(R.id.search_meals_box).setVisibility(View.VISIBLE);
                    findViewById(R.id.header_photo).setVisibility(View.GONE);
                } else{
                    findViewById(R.id.search_meals_box).setVisibility(View.GONE);
                    findViewById(R.id.header_photo).setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.sort_meals_menu:
                sortMeals();
                return true;
            case R.id.add_new_meal_menu:
                Intent newMealIntent = new Intent(this, InputMeal.class);
                startActivity(newMealIntent);
                return true;
            case R.id.edit_last_entry_menu:
                editLastMeal();
                return true;
            case R.id.export_menu:
                ExportHelper exportHelper = new ExportHelper(this);
                exportHelper.chooseExportType();
                return true;
            case R.id.about_menu:
                DialogFragment newFragment = AboutDialog.newInstance();
                newFragment.show(getFragmentManager(), "about");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Saving the random header image so that it doesn't change on screen rotation
    //Also save current sort setting, just in case
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the resID from the random image
        savedInstanceState.putInt(STATE_RESID, resID);
        savedInstanceState.putString(STATE_SORT_SETTING, sortColumn);
        savedInstanceState.putBoolean(STATE_SORT_ORDER, isAscending);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Pulls list from database for display
    private void displayListView() {
        DBHelper db = new DBHelper(this);
        mealList = db.getAllMealsSortedList(sortColumn, isAscending);
        mealListAdaptor = new MealListAdaptor(mealList, this);
        mealsListView = (ListView) findViewById(R.id.meal_list_view);
        mealsListView.setAdapter(mealListAdaptor);

        mealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> clickListener, View view, int position, long id) {
                int mealId = (int) mealListAdaptor.getMeal(position).getMealIdNumber();
                Intent intent = new Intent(getApplicationContext(), MealDetails.class)
                        .putExtra(Intent.EXTRA_TEXT, mealId);
                startActivity(intent);
            }
        });

        //Below is to set up filtering
        //https://github.com/survivingwithandroid/Surviving-with-android/blob/master/ListView_Filter_Tutorial/src/com/survivingwithandroid/listview/SimpleList/MainActivity.java
        //http://www.survivingwithandroid.com/2013/01/android-listview-filterable.html
        // we register for the contextmneu
        registerForContextMenu(mealsListView);

        // TextFilter
        mealsListView.setTextFilterEnabled(true);
        EditText searchInputBox = (EditText) findViewById(R.id.search_meals_box);

        searchInputBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "] - Start [" + start + "] - Before [" + before + "] - Count [" + count + "]");
                if (count < before) {
                    // We're deleting char so we need to reset the adapter data
                    mealListAdaptor.resetData();
                }

                mealListAdaptor.getFilter().filter(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void editLastMeal() {
        DBHelper db = new DBHelper(this);
        int maxId = db.getMaxMealId();

        //test if 0 is returned, showing no meals entered. if so, toast!!
        if (maxId == 0) {
            Toast.makeText(this, getResources().getString(R.string.no_meals), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), InputMeal.class)
                    .putExtra(Intent.EXTRA_TEXT, maxId);
            startActivity(intent);
        }
    }

    public void sortMeals() {

        CharSequence sortOptions[] = new CharSequence[] {
                getResources().getString(R.string.new_to_old),
                getResources().getString(R.string.old_to_new),
                getResources().getString(R.string.restaurant_a_to_z),
                getResources().getString(R.string.restaurant_z_to_a),
                getResources().getString(R.string.location_a_to_z),
                getResources().getString(R.string.location_z_to_a)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.sort_meals));
        builder.setIcon(R.drawable.mbicon);
        builder.setItems(sortOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sortColumn = DBContract.MealDBTable.COLUMN_DATE;
                        isAscending = false;
                        displayListView();
                        break;
                    case 1:
                        sortColumn = DBContract.MealDBTable.COLUMN_DATE;
                        isAscending = true;
                        displayListView();
                        break;
                    case 2:
                        sortColumn = DBContract.MealDBTable.COLUMN_RESTAURANT_NAME;
                        isAscending = true;
                        displayListView();
                        break;
                    case 3:
                        sortColumn = DBContract.MealDBTable.COLUMN_RESTAURANT_NAME;
                        isAscending = false;
                        displayListView();
                        break;
                    case 4:
                        sortColumn = DBContract.MealDBTable.COLUMN_LOCATION;
                        isAscending = true;
                        displayListView();
                        break;
                    case 5:
                        sortColumn = DBContract.MealDBTable.COLUMN_LOCATION;
                        isAscending = false;
                        displayListView();
                        break;
                }
            }
        });
            builder.show();
    }
}
