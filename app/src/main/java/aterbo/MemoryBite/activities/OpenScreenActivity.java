package aterbo.MemoryBite.activities;

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

import aterbo.MemoryBite.AboutDialog;
import aterbo.MemoryBite.DBHelper;
import aterbo.MemoryBite.ExportHelper;
import aterbo.MemoryBite.HelpDialog;
import aterbo.MemoryBite.R;

public class OpenScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_screen);

        setButtonFont(R.id.add_new_meal_button);
        setButtonFont(R.id.edit_last_meal_button);
        setButtonFont(R.id.view_journal_button);

        setAdView(R.id.adView);
    }

    private void setButtonFont(int resourceId){
        Typeface tf = Typeface.createFromAsset(getAssets(),
                getResources().getString(R.string.default_font_file));
        Button button = (Button) findViewById(resourceId);
        button.setTypeface(tf);
    }

    private void setAdView(int resourceId){
        AdView mAdView = (AdView) findViewById(resourceId);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_open_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void addNewMeal(View view) {
        Intent intent = new Intent(this, InputMealActivity.class);
        startActivity(intent);
    }

    public void editLastMeal(View view) {
        int maxMealId = getMaxMealId();

        if (maxMealId > 0) {
            editMealFromMealId(maxMealId);
        } else {
            showToastFromStringResource(R.string.no_meals);
        }
    }
    
    private int getMaxMealId(){
        DBHelper db = new DBHelper(this);
        return db.getMaxMealId();
    }

    private void editMealFromMealId(int mealId){
        Intent intent = new Intent(getApplicationContext(), InputMealActivity.class)
                .putExtra(Intent.EXTRA_TEXT, mealId);
        startActivity(intent);
    }

    public void goToViewList(View view) {
        Intent intent = new Intent(this, ListOfMealsActivity.class);
        startActivity(intent);
    }

    public void showHelpDialog(View view) {
        DialogFragment newFragment = HelpDialog.newInstance();
        newFragment.show(getFragmentManager(), "help");
    }

    public void sendFeedbackEmail(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Memory Bite");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            showToastFromStringResource(R.string.no_email_clients);
        }
    }

    private void showToastFromStringResource(int stringResourceId) {
        Toast.makeText(this, getResources().getString(stringResourceId), Toast.LENGTH_LONG).show();
    }
}
