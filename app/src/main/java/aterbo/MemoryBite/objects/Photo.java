package aterbo.MemoryBite.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ATerbo on 9/16/15.
 */
public class Photo implements Parcelable{

    private long photoIdNumber;
    private long associatedMealIdNumber;
    private String photoFilePath;
    private String photoCaption;
    private String photoCourse;
    private String photoNotes;
    private int photoIsPrimary;

    //Empty constructor
    public Photo() {
    }

    //Empty constructor
    public Photo(String photoFilePath) {
        this.photoFilePath = photoFilePath;
    }

    // Getter and setter methods
    public long getPhotoIdNumber() {
        return photoIdNumber;
    }

    public void setPhotoIdNumber(long photoIdNumber) {
        this.photoIdNumber = photoIdNumber;
    }

    public long getAssociatedMealIdNumber() {
        return associatedMealIdNumber;
    }

    public void setAssociatedMealIdNumber(long associatedMealIdNumber) {
        this.associatedMealIdNumber = associatedMealIdNumber;
    }

    public String getPhotoFilePath() {
        return photoFilePath;
    }

    public void setPhotoFilePath(String photoFilePath) {
        this.photoFilePath = photoFilePath;
    }

    public String getPhotoCaption() {
        return photoCaption;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    public String getPhotoCourse() {
        return photoCourse;
    }

    public void setPhotoCourse(String photoCourse) {
        this.photoCourse = photoCourse;
    }

    public String getPhotoNotes() {
        return photoNotes;
    }

    public void setPhotoNotes(String photoNotes) {
        this.photoNotes = photoNotes;
    }

    public int getPhotoIsPrimary() {
        return photoIsPrimary;
    }

    public void setPhotoIsPrimary(int photoIsPrimary) {
        this.photoIsPrimary = photoIsPrimary;
    }

    // Parcelling part
    protected Photo(Parcel in) {
        photoIdNumber = in.readLong();
        associatedMealIdNumber = in.readLong();
        photoFilePath = in.readString();
        photoCaption = in.readString();
        photoCourse = in.readString();
        photoNotes = in.readString();
        photoIsPrimary = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(photoIdNumber);
        dest.writeLong(associatedMealIdNumber);
        dest.writeString(photoFilePath);
        dest.writeString(photoCaption);
        dest.writeString(photoCourse);
        dest.writeString(photoNotes);
        dest.writeInt(photoIsPrimary);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };



}
