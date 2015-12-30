package aterbo.MemoryBite;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

/**
 * Created by ATerbo on 9/19/15.
 */
public class MealListAdaptor extends BaseAdapter implements Filterable{
    private List<Meal> mealList;
    private Context context;
    private Filter mealFilter;
    private List<Meal> originalMealList;
    private Boolean isCompressed;

    public MealListAdaptor(List<Meal> mealList, Context context) {
        this.mealList = mealList;
        this.context = context;
        this.originalMealList = mealList;
        isCompressed = true;
    }

    public MealListAdaptor(List<Meal> mealList, Context context, Boolean isCompressed) {
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
        ViewHolder viewHolder;
        String photoPath;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(isCompressed){
                convertView = inflater.inflate(R.layout.meal_list_item_layout_compressed, viewGroup, false);
            } else {
                convertView = inflater.inflate(R.layout.meal_list_item_layout, viewGroup, false);
            }

            viewHolder = new ViewHolder();

            viewHolder.restaurantName = (TextView) convertView.findViewById(R.id.restaurant_name_list);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date_list);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location_list);
            viewHolder.dinedWith = (TextView) convertView.findViewById(R.id.dined_with_list);
            viewHolder.mealIdNumber = (TextView) convertView.findViewById(R.id.id_number_list);
            viewHolder.mealPicture = (SquareImageView) convertView.findViewById(R.id.meal_picture_list);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Meal meal = mealList.get(position);

        //
        contentTest(meal.getRestaurantName(), viewHolder.restaurantName);
        contentTest(meal.getDateMealEaten(), viewHolder.date);
        contentTest(meal.getLocation(), viewHolder.location);
        contentTest(meal.getDinedWith(), viewHolder.dinedWith);
        viewHolder.mealIdNumber.setText(Long.toString(meal.getMealIdNumber()));

        if (meal.getPrimaryPhoto() != null && !meal.getPrimaryPhoto().isEmpty()) {
            //Image filepath with "file://" appended for UIL formatting
            photoPath = "file://" + meal.getPrimaryPhoto();
        } else { photoPath = ""; }

        ImageLoader.getInstance().displayImage(photoPath, viewHolder.mealPicture);

        return convertView;
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

                for (Meal meal : mealList) {
                    if (meal.getRestaurantName().toUpperCase().startsWith(
                            searchString.toString().toUpperCase())) {
                        nMealList.add(meal);
                    } else if (meal.getRestaurantName().toUpperCase().contains(
                            searchString.toString().toUpperCase())) {
                        nMealList.add(meal);
                    }
                    if (meal.getDinedWith().toUpperCase().startsWith(
                            searchString.toString().toUpperCase())) {
                        nMealList.add(meal);
                    } else if (meal.getDinedWith().toUpperCase().contains(
                            searchString.toString().toUpperCase())) {
                        nMealList.add(meal);
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
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                mealList = (List<Meal>) results.values;
                notifyDataSetChanged();
            }
        }
    }

}