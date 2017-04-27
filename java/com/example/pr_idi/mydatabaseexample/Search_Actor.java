package com.example.pr_idi.mydatabaseexample;

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

public class Search_Actor extends AppCompatActivity{

    EditText actor;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_by_actor);
        actor = (EditText) findViewById(R.id.actor_name);
        setUpToolbar();
    }

    public void onClick (View view) {
        FilmData filmData = new FilmData(this);
        filmData.open();
        String nomActor = actor.getText().toString();
        List<Film> peliConActor = filmData.searchFilmByActor(nomActor);
        if (peliConActor.size() == 0) Toast.makeText(this,"Actor/Actress not found",Toast.LENGTH_SHORT).show();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerAdapter = new RecyclerAdapter(peliConActor, 1, nomActor); //data
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
        String titulo = "Search Films with Actor/Actress";
        actionBar.setTitle(titulo);
    }

}
