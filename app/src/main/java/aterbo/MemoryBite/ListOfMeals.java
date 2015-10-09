package aterbo.MemoryBite;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;
import java.util.Random;


public class ListOfMeals extends ActionBarActivity {

    private DBHelper db;
    private SimpleCursorAdapter dataAdapter;
    private List<Meal> mealList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_meals);

        //Set random header image based on files in drawable folder and value array
        final TypedArray imgs = getResources().obtainTypedArray(R.array.headerimages);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        final int resID = imgs.getResourceId(rndInt, 0);
        ((ImageView) findViewById(R.id.header_photo)).setImageResource(resID);

        displayListView();
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
