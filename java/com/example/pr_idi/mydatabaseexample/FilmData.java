package com.example.pr_idi.mydatabaseexample;

/**
 * FilmData
 * Created by pr_idi on 10/11/16.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FilmData {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_COUNTRY,
            MySQLiteHelper.COLUMN_YEAR_RELEASE, MySQLiteHelper.COLUMN_DIRECTOR,
            MySQLiteHelper.COLUMN_PROTAGONIST, MySQLiteHelper.COLUMN_CRITICS_RATE};

    public FilmData(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void reset(Context context) {
        context.deleteDatabase(dbHelper.getDatabaseName());
        dbHelper = new MySQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public Film createFilm(String title, String director, int year, String country, String protagonist, int rate) {
        ContentValues values = new ContentValues();
        Log.d("Creating", "Creating " + title + " " + director);

        // Add data: Note that this method only provides title and director
        // Must modify the method to add the full data
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_DIRECTOR, director);
        values.put(MySQLiteHelper.COLUMN_COUNTRY, country);
        values.put(MySQLiteHelper.COLUMN_YEAR_RELEASE, year);
        values.put(MySQLiteHelper.COLUMN_PROTAGONIST, protagonist);
        values.put(MySQLiteHelper.COLUMN_CRITICS_RATE, rate);

        // Actual insertion of the data using the values variable
        long insertId = database.insert(MySQLiteHelper.TABLE_FILMS, null,
                values);

        // Main activity calls this procedure to create a new film
        // and uses the result to update the listview.
        // Therefore, we need to get the data from the database
        // (you can use this as a query example)
        // to feed the view.

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Film newFilm = cursorToFilm(cursor);

        // Do not forget to close the cursor
        cursor.close();

        // Return the film
        return newFilm;
    }


    public void deleteFilm(Film film) {
        long id = film.getId();
        System.out.println("Film deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_FILMS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Film> getAllFilms() {
        List<Film> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TITLE);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Film comment = cursorToFilm(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    public List<Film> getAllFilmsYR() {
        List<Film> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_YEAR_RELEASE +" DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Film comment = cursorToFilm(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    private Film cursorToFilm(Cursor cursor) {
        Film film = new Film();
        film.setId(cursor.getLong(0));
        film.setTitle(cursor.getString(1));
        film.setDirector(cursor.getString(4));
        film.setCountry(cursor.getString(2));
        film.setYear(cursor.getInt(3));
        film.setProtagonist(cursor.getString(5));
        film.setCritics_rate(cursor.getInt(6));
        return film;
    }

    public List<Film> searchFilmByActor(String actor) {
        List<Film> films_wtih_title = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, MySQLiteHelper.COLUMN_PROTAGONIST +" = '"+actor+"' COLLATE NOCASE", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Film comment = cursorToFilm(cursor);
            films_wtih_title.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return films_wtih_title;

    }

    public void cambiarRating(long id, int rating) {
        ContentValues cv = new ContentValues();
        cv.put(MySQLiteHelper.COLUMN_CRITICS_RATE,Integer.toString(rating));
        database.update(MySQLiteHelper.TABLE_FILMS,
                cv, MySQLiteHelper.COLUMN_ID+" = '"+id+"'", null);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Film film = cursorToFilm(cursor);
        cursor.close();
        System.out.println("Film rerated with rating: " + film.getTitle());

    }

    public List<Film> searchFilmByTitle(String title) {
        List<Film> films_wtih_title = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, MySQLiteHelper.COLUMN_TITLE +" = '"+title+"' COLLATE NOCASE", null, null, null, MySQLiteHelper.COLUMN_YEAR_RELEASE +" DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Film comment = cursorToFilm(cursor);
            films_wtih_title.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return films_wtih_title;

    }

    public List<Film> get5Bestrate() {
        List<Film> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_CRITICS_RATE +" DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Film comment = cursorToFilm(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        if (comments.size() > 4) return comments.subList(0,5);
        if (comments.size() > 3) return comments.subList(0,4);
        if (comments.size() > 2) return comments.subList(0,3);
        if (comments.size() > 1) return comments.subList(0,2);
        if (comments.size() > 0) return comments.subList(0,1);
        return comments;
    }

    public boolean hasFilms() {
        boolean ret = false;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FILMS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TITLE);
        if (cursor != null) {
            ret = cursor.getCount() != 0;
            cursor.close();
        }
        return ret;
    }
}