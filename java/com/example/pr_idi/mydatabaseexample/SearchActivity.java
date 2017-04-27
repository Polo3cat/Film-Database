package com.example.pr_idi.mydatabaseexample;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    private int posicion;
    private FilmData filmData;
    private List<Film> resultadoBusqueda;
    private ArrayAdapter<Film> adapterPeliculas;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        //listView = (ListView) findViewById(R.id.lista_titulo);

        setUpToolbar();

        filmData = new FilmData(this);
        busqueda();

        //registerForContextMenu(listView);
    }

    private void setUpToolbar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        String titulo = "Search Result";
        actionBar.setTitle(titulo);
    }

    private void busqueda () {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            setUpRecycler();
           /* adapterPeliculas = new ArrayAdapter<>(this,
                    R.layout.lista_titulos, resultadoBusqueda);
            listView.setAdapter(adapterPeliculas);
            adapterPeliculas.notifyDataSetChanged();*/

        }
    }

    private void setUpRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        filmData.open();
        resultadoBusqueda = filmData.searchFilmByTitle(query);
        filmData.close();
        recyclerAdapter = new RecyclerAdapter(resultadoBusqueda, 3, query); //data
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_buscar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
/*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_floating, menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        posicion = info.position;
        final Context c = this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case R.id.rate:
                builder.setView(R.layout.floating_layout);
                builder.setTitle("Change rating");
                builder.setMessage("Must be between 0 and 10");
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int rating;
                        Dialog dialogCast = (Dialog) dialog;
                        EditText input = (EditText) dialogCast.findViewById(R.id.ratingInput);
                        rating =  Integer.parseInt(input.getText().toString());
                        dialog.dismiss();
                        if (rating >= 0 && rating <= 10) {
                            Film film = (Film) listView.getItemAtPosition(posicion);
                            int oldrating = film.getCritics_rate();
                            cambiarRating(posicion, rating);
                            Toast.makeText(c,"Rating changed from " +String.valueOf(oldrating)+
                                    " to " +String.valueOf(rating),Toast.LENGTH_SHORT).show();
                            adapterPeliculas.clear();
                            String query = getIntent().getStringExtra(SearchManager.QUERY);
                            buscar(query);
                            adapterPeliculas.addAll(resultadoBusqueda);
                            adapterPeliculas.notifyDataSetChanged();
                        }
                        else Toast.makeText(c,"Rating must be between 0 and 10",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.delete:
                builder.setTitle("Do you want to delete this film?");
                builder.setMessage("This can't be undone");
                builder.setCancelable(true);
                filmData.open();
                final List<Film> films = filmData.searchFilmByTitle(getIntent().getStringExtra(SearchManager.QUERY));
                System.out.println("Going to delete the film with this id " + films.get(posicion).getId());
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                // Setting Positive "Yes" Button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Going to delete the film with this id " + films.get(posicion).getId());
                        filmData.deleteFilm(films.get(posicion));
                        films.remove(posicion);
                        Toast.makeText(c,"Film deleted",Toast.LENGTH_SHORT).show();
                        adapterPeliculas.clear();
                        String query = getIntent().getStringExtra(SearchManager.QUERY);
                        buscar(query);
                        adapterPeliculas.addAll(resultadoBusqueda);
                        adapterPeliculas.notifyDataSetChanged();
                        //adapterPeliculas.remove(films.get(posicion));
                    }
                });
                builder.create();
                builder.show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void cambiarRating(int posicion, int rating) {
        Film peli = (Film) listView.getItemAtPosition(posicion);
        long id = peli.getId();
        filmData.open();
        filmData.cambiarRating(id, rating);
        filmData.close();
    }
*/
    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() { super.onPause(); }

    @Override
    protected void onNewIntent (Intent intent) {
        setIntent(intent);
        busqueda();
    }
}
