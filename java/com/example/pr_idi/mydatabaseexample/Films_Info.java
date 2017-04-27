package com.example.pr_idi.mydatabaseexample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

/**
 * Created by SrPan3 on 04/01/2017.
 */

public class Films_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
        RecyclerAdapter recyclerAdapter;
        RecyclerView.LayoutManager layoutManager;
        FilmData filmData;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.films_info);
        filmData = new FilmData(this);
        filmData.open();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerAdapter = new RecyclerAdapter(filmData.getAllFilmsYR(), 2, null); //data
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        setUpToolbar();
    }

    private void setUpToolbar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        String titulo = "All films by year";
        actionBar.setTitle(titulo);
    }

}
