package com.example.navigationbarapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartItemAdapter extends CursorAdapter {
    private boolean[] selectedItems;
    public CartItemAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        selectedItems = new boolean[cursor.getCount()];
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_item, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Extract data from the cursor
        String pizzaName = cursor.getString(cursor.getColumnIndexOrThrow(PizzaDb.COLUMN_PIZZA_NAME));
        String size = cursor.getString(cursor.getColumnIndexOrThrow(PizzaDb.COLUMN_SIZE));
        String toppings = cursor.getString(cursor.getColumnIndexOrThrow(PizzaDb.COLUMN_TOPPINGS));
        double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(PizzaDb.COLUMN_TOTAL_PRICE));

        // Get references to the views in the cart item layout
        TextView pizzaNameTextView = view.findViewById(R.id.pizzaNameTextView);
        TextView sizeTextView = view.findViewById(R.id.sizeTextView);
        TextView toppingsTextView = view.findViewById(R.id.toppingsTextView);
        TextView priceTextView = view.findViewById(R.id.priceTextView);

        // Populate the views with data
        pizzaNameTextView.setText(pizzaName);
        sizeTextView.setText("Size: " + size);
        toppingsTextView.setText("Toppings: " + toppings);
        priceTextView.setText("Price: $" + totalPrice);

        int position = cursor.getPosition();
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.checkBox.setChecked(selectedItems[position]);

        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems[position] = isChecked;
        });
    }

    public void toggleSelection(long itemId) {
        int position = getPositionForItemId(itemId);
        if (position != AdapterView.INVALID_POSITION) {
            selectedItems[position] = !selectedItems[position];
            notifyDataSetChanged();
        }
    }
    private int getPositionForItemId(long itemId) {
        for (int i = 0; i < getCount(); i++) {
            if (getItemId(i) == itemId) {
                return i;
            }
        }
        return AdapterView.INVALID_POSITION;
    }

    // Add a method to clear the selection
    public void clearSelection() {
        Arrays.fill(selectedItems, false);
        notifyDataSetChanged();
    }

    // Add a method to get selected item count
    public int getSelectedItemCount() {
        int count = 0;
        for (boolean selected : selectedItems) {
            if (selected) {
                count++;
            }
        }
        return count;
    }

    // Add a method to get selected items' IDs
    public List<Long> getSelectedItemsIds(Cursor cursor) {
        List<Long> selectedIds = new ArrayList<>();
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                cursor.moveToPosition(i);
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(PizzaDb.COLUMN_ID));
                selectedIds.add(itemId);
            }
        }
        return selectedIds;
    }

    static class ViewHolder {
        CheckBox checkBox;

        ViewHolder(View view) {
            checkBox = view.findViewById(R.id.checkBox);
        }
    }
}
