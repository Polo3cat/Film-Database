package com.example.pr_idi.mydatabaseexample;


import java.util.List;
import java.util.Random;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{
    private FilmData filmData;
    ListView listView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private int posicion;
    private ArrayAdapter<Film> adapterPeliculas;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private SearchView searchView;
    private MenuItem menuItemLupa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        filmData = new FilmData(this);
        filmData.open();
        if(!filmData.hasFilms()) {
            iniciarPeliculas(filmData);
        }
        //filmData.reset(this);
        //filmData.deleteAllRows();

        //iniciarPeliculas(filmData);
        List<Film> values = filmData.get5Bestrate();
        listView = (ListView) findViewById(android.R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        adapterPeliculas = new ArrayAdapter<>(this,
                R.layout.lista_titulos, values);
       /*adapterPeliculas = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, values);*/
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(adapterPeliculas);
        setupDrawer();
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Film film = (Film) adapterView.getItemAtPosition(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View content = getLayoutInflater().inflate(R.layout.floating_info_film, null);
                builder.setView(content);
                TextView textView = (TextView) content.findViewById(R.id.textViewTitulo);
                textView.setText(film.getTitle());
                textView = (TextView) content.findViewById(R.id.textViewDirector);
                textView.setText(film.getDirector());
                textView = (TextView) content.findViewById(R.id.textViewAnyo);
                textView.setText(String.valueOf(film.getYear()));
                textView = (TextView) content.findViewById(R.id.textViewPais);
                textView.setText(film.getCountry());
                textView = (TextView) content.findViewById(R.id.textViewRate);
                textView.setText(String.valueOf(film.getCritics_rate()));
                textView = (TextView) content.findViewById(R.id.textViewProta);
                textView.setText(film.getProtagonist());
                builder.setCancelable(true);
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setTitle("Information");
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        menuItemLupa.collapseActionView();
    }

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
                            adapterPeliculas.addAll(filmData.getAllFilms());
                            adapterPeliculas.notifyDataSetChanged();
                        }
                        else Toast.makeText(c,"Rating invalid",Toast.LENGTH_SHORT).show();
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
                final FilmData filmData = new FilmData(this);
                filmData.open();
                final List<Film> films = filmData.getAllFilms();
                System.out.println("Going to delete the film with this id " + films.get(posicion).getId());
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                // Setting Positive "Yes" Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Going to delete the film with this id " + films.get(posicion).getId());
                        filmData.deleteFilm(films.get(posicion));
                        films.remove(posicion);
                        Toast.makeText(c,"Film deleted",Toast.LENGTH_SHORT).show();
                        adapterPeliculas.clear();
                        adapterPeliculas.addAll(filmData.getAllFilms());
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
        filmData.cambiarRating(id, rating);
    }


    private void iniciarPeliculas(FilmData filmData) {
        String[] p1 = {"Mulholland Drive", "David Lynch", "EEUU", "Naomi Watts"};
        int yearP1 = 2001;
        int rateP1 = 7;
        filmData.createFilm(p1[0], p1[1], yearP1, p1[2], p1[3], rateP1);

        String[] p2 = {"In the Mood for Love", "Wong Kar-wai", "Hong Kong", "Tony Leung Chiu Wai"};
        int yearP2 = 2000;
        int rateP2 = 7;
        filmData.createFilm(p2[0], p2[1], yearP2, p2[2], p2[3], rateP2);

        String[] p3 = {"Spirited Away", "Hayao Miyazaki", "Japan", "Daveigh Chase"};
        int yearP3 = 2001;
        int rateP3 = 9;
        filmData.createFilm(p3[0], p3[1], yearP3, p3[2], p3[3], rateP3);

        String[] p4 = {"There Will Be Blood", "Paul Thomas Anderson", "EEUU", "Daniel Day-Lewis"};
        int yearP4 = 2007;
        int rateP4 = 8;
        filmData.createFilm(p4[0], p4[1], yearP4, p4[2], p4[3], rateP4);
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        mActivityTitle = getTitle().toString();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {

            }
            public void onDrawerClosed(View view) {

            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navview);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }


    public void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem.getItemId());
                        return true;
                    }
                }
        );
        navigationView.inflateHeaderView(R.layout.header_navview);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_buscar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        menuItemLupa = menu.findItem(R.id.action_buscar);
        return true;
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    //TODO el click de cada uno de los botones del panel lateral de navegacion
    private void seleccionarItem(int itemId) {
        mDrawerLayout.closeDrawers();
        switch (itemId) {
            case R.id.inicio:
                Log.i("Apretado:", "Inicio");
                Intent myIntent4 = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(myIntent4);
                break;
            case R.id.cerca_titol:
                Log.i("Apretado:", "cerca titol");
                Intent myIntent2 = new Intent(MainActivity.this, Delete_Film_By_Title.class);
                MainActivity.this.startActivity(myIntent2);
                break;
            case R.id.donar_alta:
                Log.i("Apretado:", "donar alta");
                Intent myIntent1 = new Intent(MainActivity.this, New_Film.class);
                MainActivity.this.startActivity(myIntent1);
                break;
            case R.id.todas_año:
                Log.i("Apretado:", "tods año");
                if (filmData.getAllFilms().size() > 0) {
                    Intent myIntent = new Intent(MainActivity.this, Films_Info.class);
                    MainActivity.this.startActivity(myIntent);
                }
                else {
                    Toast.makeText(this,"No films to show",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.help:
                Log.i("Apretado:", "help");
                Intent myIntent6 = new Intent(MainActivity.this, Help.class);
                MainActivity.this.startActivity(myIntent6);
                break;
            case R.id.about:
                Log.i("Apretado:", "about");
                Intent myIntent3 = new Intent(MainActivity.this, About.class);
                MainActivity.this.startActivity(myIntent3);
                break;
            case  R.id.cerca_actor:
                Intent myIntent5 = new Intent(MainActivity.this, Search_Actor.class);
                MainActivity.this.startActivity(myIntent5);
                break;
            default:
                break;
        }
        adapterPeliculas.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
        else {
            this.finish();
        }
    }

    @Override
    protected void onResume() {
        filmData.open();
        adapterPeliculas.clear();
        adapterPeliculas.addAll(filmData.getAllFilms());
        adapterPeliculas.notifyDataSetChanged();
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        filmData.close();
        super.onPause();
    }

}