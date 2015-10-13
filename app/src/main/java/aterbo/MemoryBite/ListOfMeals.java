package aterbo.MemoryBite;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;
import java.util.Random;


public class ListOfMeals extends ActionBarActivity {

    private DBHelper db;
    private SimpleCursorAdapter dataAdapter;
    private List<Meal> mealList;
    private int resID;
    static final String STATE_RESID = "imageResID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_meals);

        //address saved instance state to deal with screen rotation
        if (savedInstanceState != null) {
            //get INT from saved state
            resID = savedInstanceState.getInt(STATE_RESID);
        } else {
            //Set random header image based on files in drawable folder and value array
            final TypedArray imgs = getResources().obtainTypedArray(R.array.headerimages);
            final Random rand = new Random();
            final int rndInt = rand.nextInt(imgs.length());
            resID = imgs.getResourceId(rndInt, 0);
            displayListView();

        }
        ((ImageView) findViewById(R.id.header_photo)).setImageResource(resID);


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
            case R.id.add_new_meal_menu:
                Intent newMealIntent = new Intent(this, InputMeal.class);
                startActivity(newMealIntent);
                return true;
            case R.id.edit_last_entry_menu:
                editLastMeal();
                return true;
            case R.id.about_menu:
                DialogFragment newFragment = AboutDialog.newInstance();
                newFragment.show(getFragmentManager(), "about");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the resID from the random image
        savedInstanceState.putInt(STATE_RESID, resID);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayListView() {
        DBHelper db = new DBHelper(this);
        mealList = db.getAllMealsList();
        final MealListAdaptor mealListAdaptor = new MealListAdaptor(mealList, this);
        final ListView mealsList = (ListView) findViewById(R.id.meal_list_view);
        mealsList.setAdapter(mealListAdaptor);

        mealsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> clickListener, View view, int position, long id) {
                Meal chosenMeal = mealListAdaptor.getMeal(position);
                int mealId = (int) chosenMeal.getMealIdNumber();
                Intent intent = new Intent(getApplicationContext(), MealDetails.class)
                        .putExtra(Intent.EXTRA_TEXT, mealId);
                startActivity(intent);
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

}
