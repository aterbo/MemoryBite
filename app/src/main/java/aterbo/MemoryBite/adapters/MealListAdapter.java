package aterbo.MemoryBite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import aterbo.MemoryBite.objects.Meal;
import aterbo.MemoryBite.R;
import aterbo.MemoryBite.customviews.SquareImageView;

/**
 * Created by ATerbo on 9/19/15.
 */
public class MealListAdapter extends BaseAdapter implements Filterable{
    private List<Meal> mealList;
    private Context context;
    private Filter mealFilter;
    private List<Meal> originalMealList;
    private Boolean isCompressed;

    public MealListAdapter(List<Meal> mealList, Context context, Boolean isCompressed) {
        this.mealList = mealList;
        this.context = context;
        this.originalMealList = mealList;
        this.isCompressed = isCompressed;
    }

    @Override
    public int getCount() {
        return mealList.size();
    }

    @Override
    public Object getItem(int position) {
        return mealList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(isCompressed){
                convertView = inflater.inflate(R.layout.layout_meal_list_item_compressed, viewGroup, false);
            } else {
                convertView = inflater.inflate(R.layout.layout_meal_list_item, viewGroup, false);
            }

            viewHolder = setNewViewHolder(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Meal meal = mealList.get(position);

        populateViewWithMealText(viewHolder, meal);
        setViewImageWithPrimaryImage(viewHolder, meal);

        return convertView;
    }

    private ViewHolder setNewViewHolder(ViewHolder viewHolder, View convertView){
        viewHolder.restaurantName = (TextView) convertView.findViewById(R.id.restaurant_name_list);
        viewHolder.date = (TextView) convertView.findViewById(R.id.date_list);
        viewHolder.location = (TextView) convertView.findViewById(R.id.location_list);
        viewHolder.dinedWith = (TextView) convertView.findViewById(R.id.dined_with_list);
        viewHolder.mealIdNumber = (TextView) convertView.findViewById(R.id.id_number_list);
        viewHolder.mealPicture = (SquareImageView) convertView.findViewById(R.id.meal_picture_list);
        return viewHolder;
    }

    private void populateViewWithMealText(ViewHolder viewHolder, Meal meal){
        contentTest(meal.getRestaurantName(), viewHolder.restaurantName);
        contentTest(meal.getDateMealEaten(), viewHolder.date);
        contentTest(meal.getLocation(), viewHolder.location);
        contentTest(meal.getDinedWith(), viewHolder.dinedWith);
        viewHolder.mealIdNumber.setText(Long.toString(meal.getMealIdNumber()));
    }

    private void setViewImageWithPrimaryImage(ViewHolder viewHolder, Meal meal){
        String photoPath = "";

        if (hasPrimaryPhoto(meal)){
            photoPath = "file://" + meal.getPrimaryPhoto();
        }
        ImageLoader.getInstance().displayImage(photoPath, viewHolder.mealPicture);
    }

    private boolean hasPrimaryPhoto(Meal meal){
        return (meal.getPrimaryPhoto() != null && !meal.getPrimaryPhoto().isEmpty());
    }

    public void resetData() {
        mealList = originalMealList;
    }

    private void contentTest(String string, TextView textView) {
        if (!string.isEmpty()) {
            textView.setText(string);
        } else{ textView.setText(""); }
    }

    public Meal getMeal(int position) {
        return mealList.get(position);
    }

    //ViewHolder for smooth scrolling
    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    //http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
    static class ViewHolder {
        TextView restaurantName;
        TextView date;
        TextView location;
        TextView dinedWith;
        TextView mealIdNumber;
        SquareImageView mealPicture;
    }


    //Below is to set up filtering
    //https://github.com/survivingwithandroid/Surviving-with-android/blob/master/ListView_Filter_Tutorial/src/com/survivingwithandroid/listview/SimpleList/MainActivity.java
    //http://www.survivingwithandroid.com/2013/01/android-listview-filterable.html
    @Override
    public Filter getFilter(){
        if(mealFilter == null){
            mealFilter = new MealFilter();
        }
        return mealFilter;
    }

    private class MealFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence searchString){
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (searchString == null || searchString.length() == 0) {
                // No filter implemented we return all the list
                results.values = mealList;
                results.count = mealList.size();
            }
            else {
                // We perform filtering operation
                List<Meal> nMealList = new ArrayList<>();

                //Counters so that I can insert meals in the correct place by category
                int numRestResults = 0;
                int numLocation = 0;
                int numDinedWithResults = 0;
                int numCuisineType = 0;
                int numDishResults = 0;

                for (Meal meal : mealList) {
                    //Test if search string in restaurant name and put in first category
                    if (meal.getRestaurantName().toUpperCase().contains(
                            searchString.toString().toUpperCase())) {
                        nMealList.add(numRestResults, meal);
                        numRestResults++;
                    }

                    //If search string in Location, second category
                    else if (meal.getLocation().toUpperCase().contains(
                            searchString.toString().toUpperCase())) {
                        nMealList.add(numRestResults + numLocation, meal);
                        numDinedWithResults++;
                    }

                    //If search string in Dined with, third category
                    else if (meal.getDinedWith().toUpperCase().contains(
                                    searchString.toString().toUpperCase())) {
                        nMealList.add(numRestResults + numLocation + numDinedWithResults, meal);
                        numDinedWithResults++;
                    }

                    //If search string in Cuisine Type, 4th category
                    else if (meal.getCuisineType().toUpperCase().contains(
                                    searchString.toString().toUpperCase())) {
                        nMealList.add(numRestResults + numLocation + numDinedWithResults + numCuisineType, meal);
                        numCuisineType++;
                    }

                    //If search string in Dish type entries, last category
                    else if (meal.getAppetizersNotes().toUpperCase().contains(
                                    searchString.toString().toUpperCase()) ||
                                    meal.getMainCoursesNotes().toUpperCase().contains(
                                    searchString.toString().toUpperCase()) ||
                                    meal.getDessertsNotes().toUpperCase().contains(
                                    searchString.toString().toUpperCase()) ||
                                    meal.getDrinksNotes().toUpperCase().contains(
                                    searchString.toString().toUpperCase()) ) {
                        nMealList.add(meal);
                        numDishResults++;
                    }
                }

                results.values = nMealList;
                results.count = nMealList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            // Now we have to inform the adapter about the new list filtered
            //AGT - Also check if the results are zero and if so blank the list
            if (results.count == 0) {
                resetData();
                mealList = (List<Meal>) results.values;
            } else {
                mealList = (List<Meal>) results.values;
                notifyDataSetChanged();
            }
        }
    }

}