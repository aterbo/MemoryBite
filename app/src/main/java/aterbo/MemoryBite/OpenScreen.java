package aterbo.MemoryBite;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class OpenScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_screen);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushanscript.ttf");
        Button button = (Button) findViewById(R.id.add_new_meal_button);
        button.setTypeface(tf);
        button = (Button) findViewById(R.id.edit_last_meal_button);
        button.setTypeface(tf);
        button = (Button) findViewById(R.id.view_journal_button);
        button.setTypeface(tf);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_screen, menu);
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
                super.onResume();
                startActivity(newMealIntent);
                return true;
            case R.id.edit_last_entry_menu:
                editLastMeal();
                return true;
            case R.id.view_journal_menu:
                Intent viewJournalIntent = new Intent(this, ListOfMeals.class);
                startActivity(viewJournalIntent);
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

    //onClick of New Meal button.
    // 1. Create intent
    // 2. startActivity
    public void startNewMeal(View view) {
        Intent intent = new Intent(this, InputMeal.class);
        startActivity(intent);
    }

    //onClick of Edit Last Meal button
    // 1. get count of all meals
    //2. Call edit on that database item.
    public void editLastMealClick(View view) {
        editLastMeal();
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


    //onClick of List Button
    public void goToViewList(View view) {
        Intent intent = new Intent(this, ListOfMeals.class);
        startActivity(intent);
    }

    //onClick of Help button
    public void helpButtonClick(View view) {
        DialogFragment newFragment = HelpDialog.newInstance();
        newFragment.show(getFragmentManager(), "help");
    }

    //onClick of Comments button
    public void commentsButtonClick(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Memory Bite");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
