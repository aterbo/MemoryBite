package aterbo.MemoryBite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the meal class that stores all of the data for a given meal.
 * Use this to make a journal entry. Data in this class will be pulled
 * from the input screen and stored in an SQLite database. As necessary,
 * it will be read from the database to either view in the journal or
 * be fed back into the input screen to edit an existing entry.
 */
public class Meal implements Parcelable {

    private long mealIdNumber;
    private String restaurantName;
    private String location;
    private String dateMealEaten;
    private String cuisineType;
    private String appetizersNotes;
    private String mainCoursesNotes;
    private String dessertsNotes;
    private String drinksNotes;
    private String generalNotes;
    private String dinedWith;
    private String atmosphere;
    private String price;
    private String primaryPhoto;

    //Empty constructor
    public Meal() {
    }

    public String getPrimaryPhoto() {
        return primaryPhoto;
    }

    public void setPrimaryPhoto(String primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    //Setters and Getters

    public long getMealIdNumber() {
        return mealIdNumber;
    }

    public void setMealIdNumber(long mealIdNumber) {
        this.mealIdNumber = mealIdNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getDateMealEaten() {
        return dateMealEaten;
    }

    public void setDateMealEaten(String dateMealEaten) {
        this.dateMealEaten = dateMealEaten;
    }

    public String getAppetizersNotes() {
        return appetizersNotes;
    }

    public void setAppetizersNotes(String appetizersNotes) {
        this.appetizersNotes = appetizersNotes;
    }

    public String getMainCoursesNotes() {
        return mainCoursesNotes;
    }

    public void setMainCoursesNotes(String mainCoursesNotes) {
        this.mainCoursesNotes = mainCoursesNotes;
    }

    public String getDessertsNotes() {
        return dessertsNotes;
    }

    public void setDessertsNotes(String dessertsNotes) {
        this.dessertsNotes = dessertsNotes;
    }

    public String getDrinksNotes() {
        return drinksNotes;
    }

    public void setDrinksNotes(String drinksNotes) {
        this.drinksNotes = drinksNotes;
    }

    public String getGeneralNotes() {
        return generalNotes;
    }

    public void setGeneralNotes(String generalNotes) {
        this.generalNotes = generalNotes;
    }

    public String getDinedWith() {
        return dinedWith;
    }

    public void setDinedWith(String dinedWith) {
        this.dinedWith = dinedWith;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isDataFilledOut() {
        String testString = restaurantName + location + cuisineType + dinedWith + appetizersNotes
                + mainCoursesNotes + dessertsNotes + drinksNotes + generalNotes;

        if (testString.length() >= 1) {
            return true;
        }

        return false;
    }


    public String getEmailShareSubjectString(Context context) {
        //create subject
        String shareSubject = context.getResources().getString(R.string.share_subject) + " " +
                restaurantName + " " + context.getResources().getString(R.string.on) + " "
                + dateMealEaten;
        return shareSubject;

    }

    public String getEmailShareBodyString(Context context) {
        String shareBody = "";

        //Go through and test all meal details in separate function. If they exist, append to string.
        shareBody = shareBody + checkStringAttachTitle(
                restaurantName, context.getResources().getString(R.string.restaurant_name));
        shareBody = shareBody + checkStringAttachTitle(
                dateMealEaten, context.getResources().getString(R.string.date));
        shareBody = shareBody + checkStringAttachTitle(
                location, context.getResources().getString(R.string.location));
        shareBody = shareBody + checkStringAttachTitle(
                cuisineType, context.getResources().getString(R.string.cuisine_type));
        shareBody = shareBody + checkStringAttachTitle(
                dinedWith, context.getResources().getString(R.string.dined_with));
        shareBody = shareBody + checkStringAttachTitle(
                appetizersNotes, context.getResources().getString(R.string.appetizers));
        shareBody = shareBody + checkStringAttachTitle(
                mainCoursesNotes, context.getResources().getString(R.string.main_courses));
        shareBody = shareBody + checkStringAttachTitle(
                dessertsNotes, context.getResources().getString(R.string.desserts));
        shareBody = shareBody + checkStringAttachTitle(
                drinksNotes, context.getResources().getString(R.string.drinks));
        shareBody = shareBody + checkStringAttachTitle(
                generalNotes, context.getResources().getString(R.string.notes));
        shareBody = shareBody + checkStringAttachTitle(
                atmosphere, context.getResources().getString(R.string.atmosphere));
        shareBody = shareBody + checkStringAttachTitle(
                price, context.getResources().getString(R.string.price));

        //add closing lines
        shareBody = shareBody + "\n\n\n" +
                context.getResources().getText(R.string.share_body_closing);

        return shareBody;
    }

    private String checkStringAttachTitle(String mealDetail, String detailTitle) {
        if (mealDetail != null && !mealDetail.isEmpty()) {
            return detailTitle + ": " + mealDetail + "\n";
        } else return "";
    }

    //Parceling info!!
    protected Meal(Parcel in) {
        mealIdNumber = in.readLong();
        restaurantName = in.readString();
        location = in.readString();
        dateMealEaten = in.readString();
        cuisineType = in.readString();
        appetizersNotes = in.readString();
        mainCoursesNotes = in.readString();
        dessertsNotes = in.readString();
        drinksNotes = in.readString();
        generalNotes = in.readString();
        dinedWith = in.readString();
        atmosphere = in.readString();
        price = in.readString();
        primaryPhoto = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mealIdNumber);
        dest.writeString(restaurantName);
        dest.writeString(location);
        dest.writeString(dateMealEaten);
        dest.writeString(cuisineType);
        dest.writeString(appetizersNotes);
        dest.writeString(mainCoursesNotes);
        dest.writeString(dessertsNotes);
        dest.writeString(drinksNotes);
        dest.writeString(generalNotes);
        dest.writeString(dinedWith);
        dest.writeString(atmosphere);
        dest.writeString(price);
        dest.writeString(primaryPhoto);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Meal> CREATOR = new Parcelable.Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };
}
