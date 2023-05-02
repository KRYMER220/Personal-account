package com.example.rjdapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rjdapp.ItemClickListener;
import com.example.rjdapp.R;


public class AppealsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView titleAppeals, textAppeals, idAppeals;
    public ItemClickListener listener;
    public AppealsViewHolder(View itemView) {
        super(itemView);

        titleAppeals = itemView.findViewById(R.id.titleAppeals);
        textAppeals = itemView.findViewById(R.id.textAppeals);
        idAppeals = itemView.findViewById(R.id.idAppeals);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
