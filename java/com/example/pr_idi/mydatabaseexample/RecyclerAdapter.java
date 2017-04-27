package com.example.pr_idi.mydatabaseexample;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by SrPan3 on 02/01/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecylcerViewHolder> {
    private List<Film> films;
    private int typeFlag; // 0 = show all, 1 = search by actor, 2 = search by year, 3 search by title
    private String argumento;

    public RecyclerAdapter (List<Film> films, int typeFlag, String argumento) {
        this.films = films;
        this.typeFlag = typeFlag;
        this.argumento = argumento;
    }

    @Override
    public RecylcerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_data_layout,parent,false);
        RecylcerViewHolder recylcerViewHolder = new RecylcerViewHolder(view);
        return recylcerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecylcerViewHolder holder, final int position) {
        holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final Context c = view.getContext();
                final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                final CharSequence[] items = {"Rate","Delete"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                builder.setView(R.layout.floating_layout);
                                builder.setTitle("Change rating");
                                builder.setMessage("Must be between 0 and 10");
                                builder.setCancelable(true);
                                builder.setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int rating;
                                        Dialog dialogCast = (Dialog) dialog;
                                        EditText input = (EditText) dialogCast.findViewById(R.id.ratingInput);
                                        rating = Integer.parseInt(input.getText().toString());
                                        dialog.dismiss();
                                        if (rating >= 0 && rating <= 10) {
                                            FilmData filmData = new FilmData(c);
                                            filmData.open();
                                            Film film = films.get(position);
                                            int oldrating = film.getCritics_rate();
                                            filmData.cambiarRating(films.get(position).getId(), rating);
                                            Toast.makeText(c, "Rating changed from " + String.valueOf(oldrating) +
                                                    " to " + String.valueOf(rating), Toast.LENGTH_SHORT).show();
                                            /*
                                            List<Film> aux = filmData.searchFilmByTitle(films.get(position).getTitle());
                                            boolean b = false;
                                            if (films.size() == filmData.getAllFilms().size()) b = true;
                                            films.clear();
                                            if (b) films.addAll(filmData.getAllFilmsYR());

                                            else films.addAll(aux);*/
                                            switch (typeFlag) {
                                                case 0: //all
                                                    films.clear();
                                                    films = filmData.getAllFilms();
                                                    break;
                                                case 1: //by actor
                                                    films.clear();
                                                    films = filmData.searchFilmByActor(argumento);
                                                    break;
                                                case 2: //by year
                                                    films.clear();
                                                    films = filmData.getAllFilmsYR();
                                                    break;
                                                case 3: //by title
                                                    films.clear();
                                                    films = filmData.searchFilmByTitle(argumento);
                                                    break;
                                                default:
                                                    films.clear();
                                                    break;
                                            }
                                            notifyDataSetChanged();
                                        } else
                                            Toast.makeText(c, "Rating invalid", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create();
                                builder.show();
                                break;
                            case 1:
                                builder.setTitle("Do you want to delete this film?");
                                builder.setMessage("This can't be undone!");
                                builder.setCancelable(true);
                                //System.out.println("Going to delete the film with this id " + films.get(position).getId());
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });

                                // Setting Positive "Yes" Button
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FilmData filmData = new FilmData(c);
                                        filmData.open();
                                        System.out.println("Going to delete the film with this id " + films.get(position).getId());
                                        filmData.deleteFilm(films.get(position));
                                        Toast.makeText(c, "Film deleted", Toast.LENGTH_SHORT).show();
                                        films.remove(position);
                                        notifyDataSetChanged();
                                    }
                                });
                                builder.create();
                                builder.show();
                                break;
                        }
                        System.out.println("Digam que aixo s'executa despres de tot SEMPRE");
                    }
                });
                builder.create();
                builder.show();
                return true;
            }
        });
        holder.tx_title.setText(films.get(position).getTitle());
        holder.tx_director.setText(films.get(position).getDirector());
        holder.tx_country.setText(films.get(position).getCountry());
        holder.tx_protagonist.setText(films.get(position).getProtagonist());
        holder.tx_year.setText(String.valueOf(films.get(position).getYear()));
        holder.tx_critics_rate.setText(String.valueOf(films.get(position).getCritics_rate()));
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class RecylcerViewHolder extends RecyclerView.ViewHolder {
        CardView cardview;
        TextView tx_title, tx_director, tx_country, tx_protagonist, tx_year, tx_critics_rate;

        public RecylcerViewHolder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            cardview = (CardView) itemView.findViewById(R.id.card_view);
            tx_title = (TextView) itemView.findViewById(R.id.title);
            tx_director = (TextView) itemView.findViewById(R.id.director);
            tx_country = (TextView) itemView.findViewById(R.id.country);
            tx_protagonist = (TextView) itemView.findViewById(R.id.protagonist);
            tx_year = (TextView) itemView.findViewById(R.id.year);
            tx_critics_rate = (TextView) itemView.findViewById(R.id.critics_rate);
        }
    }
}
