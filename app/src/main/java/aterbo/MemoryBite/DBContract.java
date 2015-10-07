package aterbo.MemoryBite;

import android.provider.BaseColumns;

/**
 * Created by ATerbo on 8/13/15.
 */
public class DBContract {

    //Variables for table create
    public static final String ID_TYPE = " INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    public void DBContract() {
    }

    //Data for meal table
    public class MealDBTable implements BaseColumns {


        //Table definitions
        public static final String MEAL_TABLE = "mymeals";

        //Column names for Meal Table
        public static final String COLUMN_RESTAURANT_NAME = "restaurantname";
        public static final String COLUMN_DINED_WITH = "dinedwith";
        public static final String COLUMN_GENERAL_NOTES = "generalnotes";
        public static final String COLUMN_DRINKS = "drinknotes";
        public static final String COLUMN_DESSERTS = "dessertnotes";
        public static final String COLUMN_MAIN_COURSES = "maincoursenotes";
        public static final String COLUMN_APPETIZERS = "appetizernotes";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_ATMOSPHERE = "atmosphere";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_CUISINE_TYPE = "cuisinetype";

        public static final String COLUMN_PRIMARY_PHOTO = "primaryphoto";


        //Create Table String
        public static final String CREATE_MEAL_TABLE = "CREATE TABLE " +
                MEAL_TABLE + " (" +
                _ID + ID_TYPE + COMMA_SEP +
                COLUMN_RESTAURANT_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
                COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_CUISINE_TYPE + TEXT_TYPE + COMMA_SEP +
                COLUMN_APPETIZERS + TEXT_TYPE + COMMA_SEP +
                COLUMN_MAIN_COURSES + TEXT_TYPE + COMMA_SEP +
                COLUMN_DESSERTS + TEXT_TYPE + COMMA_SEP +
                COLUMN_DRINKS + TEXT_TYPE + COMMA_SEP +
                COLUMN_GENERAL_NOTES + TEXT_TYPE + COMMA_SEP +
                COLUMN_DINED_WITH + TEXT_TYPE + COMMA_SEP +
                COLUMN_ATMOSPHERE + TEXT_TYPE + COMMA_SEP +
                COLUMN_PRICE + TEXT_TYPE + COMMA_SEP +
                COLUMN_PRIMARY_PHOTO + TEXT_TYPE + " )";

    }

    //Photo Table data
    public class PhotosDBTable implements BaseColumns {
        //Table definitions
        public static final String PHOTO_TABLE = "myphotos";

        //Column names for photo Table
        public static final String COLUMN_MEAL_ID = "mealid";
        public static final String COLUMN_PHOTO_FILE_PATH = "photofilepath";
        public static final String COLUMN_PHOTO_CAPTION = "photocaption";
        public static final String COLUMN_PHOTO_COURSE_TAG = "photocoursetag";
        public static final String COLUMN_PHOTO_NOTES = "photonotes";
        public static final String COLUMN_PRIMARY_PHOTO = "primaryphoto";

        //Create Table String -- including Foreign Key reference to table
        public static final String CREATE_PHOTO_TABLE = "CREATE TABLE " +
                PHOTO_TABLE + " (" +
                _ID + ID_TYPE + COMMA_SEP +
                COLUMN_MEAL_ID + INT_TYPE + COMMA_SEP +
                COLUMN_PHOTO_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                COLUMN_PHOTO_CAPTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_PHOTO_COURSE_TAG + TEXT_TYPE + COMMA_SEP +
                COLUMN_PHOTO_NOTES + TEXT_TYPE + COMMA_SEP +
                COLUMN_PRIMARY_PHOTO + INT_TYPE + COMMA_SEP +
                "FOREIGN KEY("+ COLUMN_MEAL_ID + ") REFERENCES " +
                MealDBTable.MEAL_TABLE +"(" + MealDBTable._ID + ") )";
    }

    //Meal Table
    //PUT VARIABLES FOR ALL TABLE COLUMNS IN {}
    public static final String[] MEAL_COLUMNS = {MealDBTable._ID,
            MealDBTable.COLUMN_RESTAURANT_NAME,
            MealDBTable.COLUMN_LOCATION,
            MealDBTable.COLUMN_DATE,
            MealDBTable.COLUMN_CUISINE_TYPE,
            MealDBTable.COLUMN_APPETIZERS,
            MealDBTable.COLUMN_MAIN_COURSES,
            MealDBTable.COLUMN_DESSERTS,
            MealDBTable.COLUMN_DRINKS,
            MealDBTable.COLUMN_GENERAL_NOTES,
            MealDBTable.COLUMN_DINED_WITH,
            MealDBTable.COLUMN_ATMOSPHERE,
            MealDBTable.COLUMN_PRICE,
            MealDBTable.COLUMN_PRIMARY_PHOTO};

    //Photo Table
    //PUT VARIABLES FOR ALL TABLE COLUMNS IN {}
    public static final String[] PHOTO_COLUMNS = {PhotosDBTable._ID,
            PhotosDBTable.COLUMN_MEAL_ID,
            PhotosDBTable.COLUMN_PHOTO_FILE_PATH,
            PhotosDBTable.COLUMN_PHOTO_CAPTION,
            PhotosDBTable.COLUMN_PHOTO_COURSE_TAG,
            PhotosDBTable.COLUMN_PHOTO_NOTES,
            PhotosDBTable.COLUMN_PRIMARY_PHOTO};


}
