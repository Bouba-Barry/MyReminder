package com.example.myreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AlarmeAdapter extends ArrayAdapter<Alarme> {
    private final Context context;
    private List<Alarme> alarmeList;

    public AlarmeAdapter(@NonNull Context context, int resource, @NonNull List<Alarme> alarmeList) {
        super(context, resource, alarmeList);
        this.context = context;
        this.alarmeList = alarmeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        Button buttonComplete = convertView.findViewById(R.id.buttonComplete);

        Alarme alarme = alarmeList.get(position);

        textViewTitle.setText(alarme.getTitle());
        textViewDate.setText(alarme.getCreate_date()+" "+alarme.getTime());

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alarme.setTimeOut(true);
                notifyDataSetChanged(); // Rafraîchir l'affichage de la liste
            }
        });

        return convertView;
    }
}
