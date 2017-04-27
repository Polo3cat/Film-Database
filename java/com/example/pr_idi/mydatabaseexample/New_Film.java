package com.example.pr_idi.mydatabaseexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by SrPan3 on 04/01/2017.
 */

public class New_Film extends AppCompatActivity /*implements AdapterView.OnItemClickListener*/ {

    EditText title, director, year, country, protagonist, critics_rate;

    FilmData filmData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_film);
        title = (EditText) findViewById(R.id.title_tx);
        director = (EditText) findViewById(R.id.director_tx);
        year = (EditText) findViewById(R.id.year_tx);
        country = (EditText) findViewById(R.id.country_tx);
        protagonist = (EditText) findViewById(R.id.protagonist_tx);
        critics_rate = (EditText) findViewById(R.id.critics_rate_tx);
        filmData = new FilmData(this);
        filmData.open();
        setUpToolbar();
    }

    public void onClick(View view) {
        String t = title.getText().toString();
        String d = director.getText().toString();
        String c = country.getText().toString();
        String p = protagonist.getText().toString();
        if (t.equals("") || d.equals("") || c.equals("") || p.equals("") || year.getText().toString().equals("") ||
                critics_rate.getText().toString().equals("")) {
            Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show();
        }
        else {
            int y = Integer.valueOf(year.getText().toString());
            int cr = Integer.valueOf(critics_rate.getText().toString());
            if (y <= 2017 && y >= 0) {
                if (cr >= 0 && cr <= 10) {
                    filmData.createFilm(t, d, y, c, p, cr);
                    Toast.makeText(view.getContext(), "Film added", Toast.LENGTH_SHORT).show();
                    title.setText("");
                    director.setText("");
                    year.setText("");
                    country.setText("");
                    protagonist.setText("");
                    critics_rate.setText("");
                } else Toast.makeText(this, "Rating not valid", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Year not valid", Toast.LENGTH_SHORT).show();
        }
        //System.out.println("aixo es el que hi ha si no sentra res->" +t+ "<-o bé això->" +y+"<-");
    }
    private void setUpToolbar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        String titulo = "Create New Film";
        actionBar.setTitle(titulo);
    }
}
