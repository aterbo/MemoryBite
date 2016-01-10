package aterbo.MemoryBite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import aterbo.MemoryBite.objects.Photo;
import aterbo.MemoryBite.R;
import aterbo.MemoryBite.customviews.SquareImageView;

/**
 * Created by ATerbo on 9/19/15.
 */
public class PhotoGridAdapter extends BaseAdapter {
    Context context;
    ArrayList<Photo> photoList;

    public PhotoGridAdapter(ArrayList<Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_photo_grid_item, viewGroup, false);

            viewHolder = new ViewHolder();

            viewHolder.mealPicture = (SquareImageView) convertView.findViewById(R.id.grid_photo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Photo photo = photoList.get(position);

        //Image filepath with "file://" appended for UIL formatting
        String photoPath = "file://" + photo.getPhotoFilePath();

        //Use UIL with default options presented in GlobalState/Application:
        ImageLoader.getInstance().displayImage(photoPath, viewHolder.mealPicture);

        return convertView;
    }

    public Photo getPhoto(int position) {
        return photoList.get(position);
    }

    //ViewHolder for smooth scrolling
    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    //http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
    static class ViewHolder {
        SquareImageView mealPicture;
    }

}
