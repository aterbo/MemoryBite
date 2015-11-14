package aterbo.MemoryBite;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by ATerbo on 11/4/15.
 */
public class FullScreenImageAdapter extends PagerAdapter {
    private Activity activity;
    private ArrayList<Photo> photoList;
    private LayoutInflater inflater;

    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  ArrayList<Photo> photoList) {
        this.activity = activity;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return this.photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        Button btnClose;
        final Button btnShowCaption;
        final TextView caption;
        int buttonTransparancy = 180;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        btnClose.getBackground().setAlpha(buttonTransparancy);

        Photo photo = photoList.get(position);

        //Image filepath with "file://" appended for UIL formatting
        String photoPath = "file://" + photo.getPhotoFilePath();

        //Use UIL with default options presented in GlobalState/Application:
        ImageLoader.getInstance().displayImage(photoPath, imgDisplay);

        //Test if caption present. If so, build caption display
        if (photo.getPhotoCaption() != null && !photo.getPhotoCaption().isEmpty()){
            btnShowCaption = (Button) viewLayout.findViewById(R.id.btnShowCaption);
            btnShowCaption.setVisibility(View.VISIBLE);
            btnShowCaption.getBackground().setAlpha(buttonTransparancy);
            btnShowCaption.setText("...");

            caption = (TextView) viewLayout.findViewById(R.id.caption);
            caption.setText(photo.getPhotoCaption());
            caption.setMovementMethod(new ScrollingMovementMethod());
            caption.getBackground().setAlpha(buttonTransparancy);

            // Caption button click event
            btnShowCaption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnShowCaption.getText() == "...") {
                        caption.setVisibility(View.VISIBLE);
                        btnShowCaption.setText(R.string.close_caption);
                    } else {
                        caption.setVisibility(View.GONE);
                        btnShowCaption.setText("...");
                    }

                }
            });


        }

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
