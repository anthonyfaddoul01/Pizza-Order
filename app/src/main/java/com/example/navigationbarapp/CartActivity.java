package com.example.navigationbarapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private PizzaDb dbHelper;
    private CartItemAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");

        TextView titleTextView = findViewById(R.id.customerNameTextView);
        titleTextView.setText(userName);

        dbHelper = new PizzaDb(this);

        ListView cartItemsListView = findViewById(R.id.cartItemsListView);
        cartItemsListView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.toggleSelection(id);
        });
        // Retrieve data from the database
        cursor = dbHelper.getAllPizzaOrders();

        double totalPrice = dbHelper.calculateTotalPrice();
        TextView totalPriceTextView = findViewById(R.id.totalPrice);
        totalPriceTextView.setText("Total Price: $" + totalPrice);
        // Check if there are items in the cart before creating the adapter
        if (cursor != null && cursor.getCount() > 0) {
            adapter = new CartItemAdapter(this, cursor);
            cartItemsListView.setAdapter(adapter);
        } else {
            // Handle the case where there are no items in the cart
            // Display a message or perform appropriate action
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the cart is empty
        }

        Button removeItemsButton = findViewById(R.id.removeButton);
        removeItemsButton.setOnClickListener(v -> {
            int selectedCount = adapter.getSelectedItemCount();
            if (selectedCount > 0) {
                // Get selected item IDs and remove them from the database
                List<Long> selectedIds = adapter.getSelectedItemsIds(cursor);
                dbHelper.removePizzaOrders(selectedIds);

                // Reload the cursor to update the ListView
                cursor = dbHelper.getAllPizzaOrders();
                adapter.changeCursor(cursor);

                // Clear the selection
                adapter.clearSelection();
                double totalPrice2 = dbHelper.calculateTotalPrice();
                TextView totalPriceTextView2 = findViewById(R.id.totalPrice);
                totalPriceTextView2.setText("Total Price: $" + totalPrice2);
            } else {
                Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            }
        });

        Button checkout = findViewById(R.id.checkoutButton);
        checkout.setOnClickListener(v -> {
            double total = dbHelper.calculateTotalPrice();
            Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
            checkoutIntent.putExtra("TOTAL_PRICE", total);
            checkoutIntent.putExtra("USER_NAME", userName);
            startActivity(checkoutIntent);
        });

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to go back to the main activity
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}