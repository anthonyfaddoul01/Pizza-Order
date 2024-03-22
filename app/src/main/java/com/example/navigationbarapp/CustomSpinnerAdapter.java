package com.example.navigationbarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] pizzaNames;
    private int[] pizzaImages;

    public CustomSpinnerAdapter(Context context, String[] pizzaNames, int[] pizzaImages) {
        super(context, R.layout.spinner_item, pizzaNames);
        this.context = context;
        this.pizzaNames = pizzaNames;
        this.pizzaImages = pizzaImages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_item, parent, false);

        TextView textView = row.findViewById(R.id.pizzaName);
        ImageView imageView = row.findViewById(R.id.pizzaImage);

        textView.setText(pizzaNames[position]);
        imageView.setImageResource(pizzaImages[position]);

        return row;
    }
}
