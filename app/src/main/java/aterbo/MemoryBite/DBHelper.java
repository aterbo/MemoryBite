package aterbo.MemoryBite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "photoJournal";

    //Constructor code
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Enable foreign keys
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    //Create database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.MealDBTable.CREATE_MEAL_TABLE);
        db.execSQL(DBContract.PhotosDBTable.CREATE_PHOTO_TABLE);
    }

    //upgrade database on version change(currently drops table)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.MealDBTable.MEAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.PhotosDBTable.PHOTO_TABLE);
        this.onCreate(db);
    }

    //MEAL TABLE
    //CRUD - Create operations
    public int createMeal(Meal meal) {
        // get reference of the MealDB database
        long newId;
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted in meal table
        ContentValues values = new ContentValues();
        values.put(DBContract.MealDBTable.COLUMN_RESTAURANT_NAME, meal.getRestaurantName());
        values.put(DBContract.MealDBTable.COLUMN_LOCATION, meal.getLocation());
        values.put(DBContract.MealDBTable.COLUMN_DATE, meal.getDateMealEaten());
        values.put(DBContract.MealDBTable.COLUMN_CUISINE_TYPE, meal.getCuisineType());
        values.put(DBContract.MealDBTable.COLUMN_APPETIZERS, meal.getAppetizersNotes());
        values.put(DBContract.MealDBTable.COLUMN_MAIN_COURSES, meal.getMainCoursesNotes());
        values.put(DBContract.MealDBTable.COLUMN_DESSERTS, meal.getDessertsNotes());
        values.put(DBContract.MealDBTable.COLUMN_DRINKS, meal.getDrinksNotes());
        values.put(DBContract.MealDBTable.COLUMN_GENERAL_NOTES, meal.getGeneralNotes());
        values.put(DBContract.MealDBTable.COLUMN_DINED_WITH, meal.getDinedWith());
        values.put(DBContract.MealDBTable.COLUMN_ATMOSPHERE, meal.getAtmosphere());
        values.put(DBContract.MealDBTable.COLUMN_PRICE, meal.getPrice());
        values.put(DBContract.MealDBTable.COLUMN_PRIMARY_PHOTO, meal.getPrimaryPhoto());

        // insert meal
        newId = db.insert(DBContract.MealDBTable.MEAL_TABLE, null, values);

        // close database transaction
        db.close();

        return (int) newId;
    }

    //Read meal table
    public Meal readMeal(int id) {
        // get reference of the MealDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get meal query and create cursor
        Cursor cursor = db.query(DBContract.MealDBTable.MEAL_TABLE, // a. table
                DBContract.MEAL_COLUMNS, " _ID = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();

        Meal meal = new Meal();
        meal.setMealIdNumber(cursor.getInt(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable._ID)));
        meal.setRestaurantName(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_RESTAURANT_NAME)));
        meal.setLocation(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_LOCATION)));
        meal.setDateMealEaten(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DATE)));
        meal.setCuisineType(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_CUISINE_TYPE)));
        meal.setAppetizersNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_APPETIZERS)));
        meal.setMainCoursesNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_MAIN_COURSES)));
        meal.setDessertsNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DESSERTS)));
        meal.setDrinksNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DRINKS)));
        meal.setGeneralNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_GENERAL_NOTES)));
        meal.setDinedWith(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DINED_WITH)));
        meal.setAtmosphere(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_ATMOSPHERE)));
        meal.setPrice(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_PRICE)));
        meal.setPrimaryPhoto(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_PRIMARY_PHOTO)));
        cursor.close();
        db.close();
        return meal;
    }

    //Update single meal
    public int updateMeal(Meal meal) {

        // get reference of the MealDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(DBContract.MealDBTable.COLUMN_RESTAURANT_NAME, meal.getRestaurantName());
        values.put(DBContract.MealDBTable.COLUMN_LOCATION, meal.getLocation());
        values.put(DBContract.MealDBTable.COLUMN_DATE, meal.getDateMealEaten());
        values.put(DBContract.MealDBTable.COLUMN_CUISINE_TYPE, meal.getCuisineType());
        values.put(DBContract.MealDBTable.COLUMN_APPETIZERS, meal.getAppetizersNotes());
        values.put(DBContract.MealDBTable.COLUMN_MAIN_COURSES, meal.getMainCoursesNotes());
        values.put(DBContract.MealDBTable.COLUMN_DESSERTS, meal.getDessertsNotes());
        values.put(DBContract.MealDBTable.COLUMN_DRINKS, meal.getDrinksNotes());
        values.put(DBContract.MealDBTable.COLUMN_GENERAL_NOTES, meal.getGeneralNotes());
        values.put(DBContract.MealDBTable.COLUMN_DINED_WITH, meal.getDinedWith());
        values.put(DBContract.MealDBTable.COLUMN_ATMOSPHERE, meal.getAtmosphere());
        values.put(DBContract.MealDBTable.COLUMN_PRICE, meal.getPrice());
        values.put(DBContract.MealDBTable.COLUMN_PRIMARY_PHOTO, meal.getPrimaryPhoto());

        // update
        int i = db.update(DBContract.MealDBTable.MEAL_TABLE, values, DBContract.MealDBTable._ID +
                " = ?", new String[]{String.valueOf(meal.getMealIdNumber())});

        db.close();
        return i;
    }

    // Deleting single meal
    public void deleteMeal(Meal meal) {

        // get reference of the MealDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete meal
        db.delete(DBContract.MealDBTable.MEAL_TABLE, DBContract.MealDBTable._ID
                + " = ?", new String[]{String.valueOf(meal.getMealIdNumber())});
        db.close();
    }

    //////////////////////////////////////

    public Cursor getAllMealsCursor() {
        // select meal query
        String query = "SELECT  * FROM " + DBContract.MealDBTable.MEAL_TABLE;

        // get reference of the mealDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public int getCountOfMeals() {
        String countQuery = "SELECT * FROM " + DBContract.MealDBTable.MEAL_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    public int getMaxMealId() {
        String maxQuery = "SELECT MAX(" + DBContract.MealDBTable._ID + ") AS "
                + DBContract.MealDBTable._ID + " FROM " + DBContract.MealDBTable.MEAL_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(maxQuery, null);
        int maxId = 0;
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                maxId = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("Get Max", "ERRRORRRR");
        } finally {
            db.close();
        }
        return maxId;
    }


    public List getAllMealsList() {
        List meals = new LinkedList();

        // select meal query
        String query = "SELECT  * FROM " + DBContract.MealDBTable.MEAL_TABLE;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Meal meal = null;
        if (cursor.moveToFirst()) {
            do {
                meal = new Meal();
                meal.setMealIdNumber(Integer.parseInt(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable._ID))));
                meal.setRestaurantName(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_RESTAURANT_NAME)));
                meal.setLocation(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_LOCATION)));
                meal.setDateMealEaten(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DATE)));
                meal.setCuisineType(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_CUISINE_TYPE)));
                meal.setAppetizersNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_APPETIZERS)));
                meal.setMainCoursesNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_MAIN_COURSES)));
                meal.setDessertsNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DESSERTS)));
                meal.setDrinksNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DRINKS)));
                meal.setGeneralNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_GENERAL_NOTES)));
                meal.setDinedWith(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_DINED_WITH)));
                meal.setAtmosphere(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_ATMOSPHERE)));
                meal.setPrice(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_PRICE)));
                meal.setPrimaryPhoto(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.MealDBTable.COLUMN_PRIMARY_PHOTO)));

                // Add meal to meals
                meals.add(meal);
            } while (cursor.moveToNext());
        }
        db.close();
        return meals;
    }

    /////////////////////////////////////////////


    //PHOTO TABLE
    //CRUD - Create operations
    public int createPhoto(Photo photo) {
        // get reference of the MealDB database
        long newId;
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted in meal table
        ContentValues values = new ContentValues();
        values.put(DBContract.PhotosDBTable.COLUMN_MEAL_ID, photo.getAssociatedMealIdNumber());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_FILE_PATH, photo.getPhotoFilePath());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_CAPTION, photo.getPhotoCaption());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_COURSE_TAG, photo.getPhotoCourse());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_NOTES, photo.getPhotoNotes());
        values.put(DBContract.PhotosDBTable.COLUMN_PRIMARY_PHOTO, photo.getPhotoIsPrimary());

        // insert meal
        newId = db.insert(DBContract.PhotosDBTable.PHOTO_TABLE, null, values);

        // close database transaction
        db.close();

        return (int) newId;
    }


    //Read photo table -- Probably needs to be converted to List of Photos with meal ID
    //Returns single photo based on ID number. Need to create another read function for list of photos.
    public Photo readPhotoReturnList(int photoId) {
        // get reference of the MealDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get photo query and create cursor
        //Change "" _ID = ?" line??
        Cursor cursor = db.query(DBContract.PhotosDBTable.PHOTO_TABLE, // a. table
                DBContract.PHOTO_COLUMNS, " _ID = ?", // CHANGE ID??
                new String[]{String.valueOf(photoId)}, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();

        Photo photo = new Photo();
        photo.setPhotoIdNumber(cursor.getInt(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable._ID)));
        photo.setAssociatedMealIdNumber(cursor.getInt(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_MEAL_ID)));
        photo.setPhotoFilePath(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_FILE_PATH)));
        photo.setPhotoCaption(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_CAPTION)));
        photo.setPhotoCourse(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_COURSE_TAG)));
        photo.setPhotoNotes(cursor.getString(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_NOTES)));
        photo.setPhotoIsPrimary(cursor.getInt(
                cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PRIMARY_PHOTO)));
        db.close();

        //Change to make List and return List
        return photo;
    }

    //Update single meal
    public int updatePhoto(Photo photo) {

        // get reference of the MealDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(DBContract.PhotosDBTable.COLUMN_MEAL_ID, photo.getAssociatedMealIdNumber());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_FILE_PATH, photo.getPhotoFilePath());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_CAPTION, photo.getPhotoCaption());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_COURSE_TAG, photo.getPhotoCourse());
        values.put(DBContract.PhotosDBTable.COLUMN_PHOTO_NOTES, photo.getPhotoNotes());
        values.put(DBContract.PhotosDBTable.COLUMN_PRIMARY_PHOTO, photo.getPhotoIsPrimary());

        // update
        int i = db.update(DBContract.PhotosDBTable.PHOTO_TABLE, values, DBContract.PhotosDBTable._ID +
                " = ?", new String[]{String.valueOf(photo.getPhotoIdNumber())});

        db.close();
        return i;
    }

    // Deleting single photo
    public void deletePhoto(Photo photo) {

        // get reference of the MealDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete meal
        db.delete(DBContract.PhotosDBTable.PHOTO_TABLE, DBContract.PhotosDBTable._ID
                + " = ?", new String[]{String.valueOf(photo.getPhotoIdNumber())});
        db.close();
    }

    //Delete all photos from a given meal
    public void deleteAllPhotosFromMeal(long mealId) {

        // get reference of the MealDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete photos
        db.delete(DBContract.PhotosDBTable.PHOTO_TABLE, DBContract.PhotosDBTable.COLUMN_MEAL_ID
                + " = ?", new String[]{String.valueOf(mealId)});
        db.close();
    }

    ////////////////////////

    public Cursor getAllPhotosCursor() {
        // select photo query
        String query = "SELECT  * FROM " + DBContract.PhotosDBTable.PHOTO_TABLE;

        // get reference of the mealDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public int getCountOfPhotos() {
        String countQuery = "SELECT * FROM " + DBContract.PhotosDBTable.PHOTO_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    public int getMaxPhotoId() {
        String maxQuery = "SELECT MAX(" + DBContract.PhotosDBTable._ID + ") AS "
                + DBContract.PhotosDBTable._ID + " FROM " + DBContract.PhotosDBTable.PHOTO_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(maxQuery, null);
        int maxId = 0;
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                maxId = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("Get Max", "ERRRORRRR");
        } finally {
            db.close();
        }
        return maxId;
    }


    public List getAllPhotosList() {
        List photos = new LinkedList();

        // select meal query
        String query = "SELECT  * FROM " + DBContract.PhotosDBTable.PHOTO_TABLE;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Photo photo = null;
        if (cursor.moveToFirst()) {
            do {
                photo = new Photo();
                photo.setPhotoIdNumber(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable._ID)));
                photo.setAssociatedMealIdNumber(cursor.getLong(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_MEAL_ID)));
                photo.setPhotoFilePath(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_FILE_PATH)));
                photo.setPhotoCaption(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_CAPTION)));
                photo.setPhotoCourse(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_COURSE_TAG)));
                photo.setPhotoNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_NOTES)));
                photo.setPhotoIsPrimary(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PRIMARY_PHOTO)));

                // Add meal to meals
                photos.add(photo);
            } while (cursor.moveToNext());
        }
        db.close();
        return photos;
    }

    //Get all photos from a given meal ID
    public boolean checkMealIdForPhotos(long mealId) {

        // select meal query
        String query = "SELECT  * FROM " + DBContract.PhotosDBTable.PHOTO_TABLE + " WHERE " +
                DBContract.PhotosDBTable.COLUMN_MEAL_ID + " = " + mealId;

        // get reference of the MealDB database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    //Get all photos from a given meal ID
    public List getAllPhotosForMealList(long mealId) {
        List photosFromMeal = new LinkedList();

        // select meal query
        String query = "SELECT  * FROM " + DBContract.PhotosDBTable.PHOTO_TABLE + " WHERE " +
                DBContract.PhotosDBTable.COLUMN_MEAL_ID + " = " + mealId;

        // get reference of the MealDB database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Photo photo = null;
        if (cursor.moveToFirst()) {
            do {
                photo = new Photo();
                photo.setPhotoIdNumber(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable._ID)));
                photo.setAssociatedMealIdNumber(cursor.getLong(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_MEAL_ID)));
                photo.setPhotoFilePath(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_FILE_PATH)));
                photo.setPhotoCaption(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_CAPTION)));
                photo.setPhotoCourse(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_COURSE_TAG)));
                photo.setPhotoNotes(cursor.getString(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PHOTO_NOTES)));
                photo.setPhotoIsPrimary(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBContract.PhotosDBTable.COLUMN_PRIMARY_PHOTO)));

                // Add photo to photos
                photosFromMeal.add(photo);
            } while (cursor.moveToNext());
        }
        db.close();
        return photosFromMeal;
    }
}