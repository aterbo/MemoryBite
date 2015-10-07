package aterbo.MemoryBite;

/**
 * Created by ATerbo on 9/16/15.
 */
public class Photo {

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
}
