package aterbo.MemoryBite;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ATerbo on 9/19/15.
 */
public class PhotoGridAdaptor extends BaseAdapter {
    Context context;
    ArrayList<Photo> photoList;

    public PhotoGridAdaptor(ArrayList<Photo> photoList, Context context) {
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
            convertView = inflater.inflate(R.layout.photo_grid_item_layout, viewGroup, false);

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
