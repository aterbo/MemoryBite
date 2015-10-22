package aterbo.MemoryBite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by ATerbo on 9/19/15.
 */
public class MealListAdaptor extends BaseAdapter {
    List<Meal> mealList;
    Context context;

    public MealListAdaptor(List<Meal> mealList, Context context) {
        this.mealList = mealList;
        this.context = context;
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
            convertView = inflater.inflate(R.layout.meal_list_item_layout, viewGroup, false);

            viewHolder = new ViewHolder();

            viewHolder.restaurantName = (TextView) convertView.findViewById(R.id.restaurant_name_list);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location_list);
            viewHolder.dinedWith = (TextView) convertView.findViewById(R.id.dined_with_list);
            viewHolder.mealDate = (TextView) convertView.findViewById(R.id.date_list);
            viewHolder.mealIdNumber = (TextView) convertView.findViewById(R.id.id_number_list);
            viewHolder.mealPicture = (SquareImageView) convertView.findViewById(R.id.meal_picture_list);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Meal meal = mealList.get(position);

        //
        contentTest(meal.getRestaurantName(), viewHolder.restaurantName,false);
        contentTest(meal.getLocation(), viewHolder.location,false);
        contentTest(meal.getDinedWith(), viewHolder.dinedWith,true);
        contentTest(meal.getRestaurantName(), viewHolder.restaurantName,false);
        contentTest(meal.getDateMealEaten(), viewHolder.mealDate,false);

        viewHolder.mealIdNumber.setText(Long.toString(meal.getMealIdNumber()));

        if (meal.getPrimaryPhoto() != null && !meal.getPrimaryPhoto().isEmpty()) {
            //Image filepath with "file://" appended for UIL formatting
            photoPath = "file://" + meal.getPrimaryPhoto();
        } else { photoPath = ""; }

        ImageLoader.getInstance().displayImage(photoPath, viewHolder.mealPicture);

        return convertView;
    }

    private void contentTest(String string, TextView textView, boolean withTest) {
        if (!string.isEmpty()) {
            if(withTest){string = "With " + string;}
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
        TextView location;
        TextView dinedWith;
        TextView mealDate;
        TextView mealIdNumber;
        SquareImageView mealPicture;
    }
}