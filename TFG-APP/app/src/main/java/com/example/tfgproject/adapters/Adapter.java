package com.example.tfgproject.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgproject.R;
import com.example.tfgproject.entities.TimeCard;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.TimeCardViewHolder> {

    List<TimeCard> list;

    public Adapter(List<TimeCard> list) {
        this.list = list;
    }

    public static class TimeCardViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView entry;
        private TextView out;
        private TextView noWorkDay;
        private CardView cardView;

        public TimeCardViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.textview_date);
            entry = (TextView) itemView.findViewById(R.id.textview_entry);
            out = (TextView) itemView.findViewById(R.id.textview_out);
            noWorkDay = (TextView) itemView.findViewById(R.id.textview_noWorkDay);
            cardView = (CardView) itemView.findViewById(R.id.card_viewOut);
        }
    }

    @NonNull
    @Override
    // Generamos el holder
    public TimeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_view, parent, false);
        TimeCardViewHolder holder = new TimeCardViewHolder(v);
        return holder;
    }

    // Cargamos los elementos especificando su holder
    @Override
    public void onBindViewHolder(@NonNull TimeCardViewHolder holder, int position) {
        TimeCard timecard = list.get(position);
        if (timecard.isDayOff()) {
            holder.date.setText(timecard.getDate());
            holder.entry.setVisibility(View.INVISIBLE);
            holder.out.setVisibility(View.INVISIBLE);
            holder.noWorkDay.setVisibility(View.VISIBLE);
            holder.noWorkDay.setText("Permiso");
            holder.cardView.setCardBackgroundColor(Color.parseColor("#546E7A"));

        } else if (timecard.isHoliday()) {
            holder.date.setText(timecard.getDate());
            holder.entry.setVisibility(View.INVISIBLE);
            holder.out.setVisibility(View.INVISIBLE);
            holder.noWorkDay.setVisibility(View.VISIBLE);
            holder.noWorkDay.setText("Vacaciones");
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FF018786"));

        } else {
            holder.date.setText(timecard.getDate());
            holder.entry.setVisibility(View.VISIBLE);
            holder.out.setVisibility(View.VISIBLE);
            holder.entry.setText("Inicio: " + timecard.getEntryTime());
            holder.out.setText("Fin: " + timecard.getOutTime());
            holder.noWorkDay.setVisibility(View.INVISIBLE);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#014c84"));
        }
    }

    // Número de elementos del RecyclerView
    @Override
    public int getItemCount() {
        return list.size();
    }

}
