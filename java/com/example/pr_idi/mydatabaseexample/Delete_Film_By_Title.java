package com.example.pr_idi.mydatabaseexample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/**
 * Created by SrPan3 on 05/01/2017.
 */

public class Delete_Film_By_Title extends AppCompatActivity{

    EditText title;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_film_by_title);
        title = (EditText) findViewById(R.id.film_title);
        setUpToolbar();
    }

    public void onClick (View view) {
        FilmData filmData = new FilmData(this);
        filmData.open();
        String nomPeli = title.getText().toString();
        List<Film> list_of_films_with_title = filmData.searchFilmByTitle(nomPeli);
        if (list_of_films_with_title.size() == 0) Toast.makeText(this,"Film not found",Toast.LENGTH_SHORT).show();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerAdapter = new RecyclerAdapter(list_of_films_with_title, 3, nomPeli); //data
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setUpToolbar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        String titulo = "Search by Title";
        actionBar.setTitle(titulo);
    }

}
