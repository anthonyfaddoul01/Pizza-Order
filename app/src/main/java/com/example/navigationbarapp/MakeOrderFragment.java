package com.example.navigationbarapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MakeOrderFragment extends Fragment {

    private Spinner pizzaSpinner;
    private RadioGroup sizeRadioGroup;
    private RadioButton smallRadioButton;
    private RadioButton mediumRadioButton;
    private RadioButton largeRadioButton;
    private ListView toppingsListView;
    private Button addToCartButton;
    private Button showHideToppingsButton;
    private Button goToCartButton;
    private String name;
    private String location;
    private List<Map<String, Object>> cart = new ArrayList<>();
    private int itemCount = 0;
    private Map<String, Double> toppingPrices;
    private PizzaDb dbHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_makeorder, container, false);
        dbHelper = new PizzaDb(requireContext());
        //Intent intent = getIntent();
        //name = intent.getStringExtra("NAME");
        //location = intent.getStringExtra("LOCATION");

        pizzaSpinner = view.findViewById(R.id.pizzaSpinner);
        sizeRadioGroup = view.findViewById(R.id.sizeRadioGroup);
        smallRadioButton = view.findViewById(R.id.smallRadioButton);
        mediumRadioButton = view.findViewById(R.id.mediumRadioButton);
        largeRadioButton = view.findViewById(R.id.largeRadioButton);
        toppingsListView = view.findViewById(R.id.toppingsListView);
        showHideToppingsButton = view.findViewById(R.id.showHideToppingsButton);
        addToCartButton = view.findViewById(R.id.addToCartButton);


        ArrayAdapter<CharSequence> pizzaAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.pizza_options, android.R.layout.simple_spinner_item);
        pizzaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pizzaSpinner.setAdapter(pizzaAdapter);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayAdapter<CharSequence> toppingsAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.toppings_array, R.layout.list_item);
        toppingsListView.setAdapter(toppingsAdapter);
        toppingsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Resources res = getResources();
        String[] pizzaNames = res.getStringArray(R.array.pizza_options);
        int[] pizzaImages = {R.drawable.intro, R.drawable.margherita, R.drawable.pepperoni, R.drawable.veggie, R.drawable.bbq_chicken, R.drawable.four_cheese};

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(requireContext(), pizzaNames, pizzaImages);
        pizzaSpinner.setAdapter(adapter);

        pizzaSpinner.setSelection(0);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddToCart();
            }
        });

        pizzaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    sizeRadioGroup.setVisibility(View.GONE);
                    smallRadioButton.setVisibility(View.GONE);
                    mediumRadioButton.setVisibility(View.GONE);
                    largeRadioButton.setVisibility(View.GONE);
                    showHideToppingsButton.setVisibility(View.GONE);
                    addToCartButton.setVisibility(View.GONE);
                } else {
                    sizeRadioGroup.setVisibility(View.VISIBLE);
                    smallRadioButton.setVisibility(View.VISIBLE);
                    mediumRadioButton.setVisibility(View.VISIBLE);
                    largeRadioButton.setVisibility(View.VISIBLE);
                    showHideToppingsButton.setVisibility(View.VISIBLE);
                    addToCartButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddToCart();
            }
        });
        showHideToppingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toppingsListView.getVisibility() == View.VISIBLE) {
                    toppingsListView.setVisibility(View.GONE);
                    showHideToppingsButton.setText(R.string.show_toppings);
                } else {
                    toppingsListView.setVisibility(View.VISIBLE);
                    showHideToppingsButton.setText(R.string.hide_toppings);
                }
            }
        });

        sizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                smallRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                mediumRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                largeRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                int defaultTextColor = getResources().getColor(R.color.black);
                int selectedTextColor = getResources().getColor(R.color.orange);

                smallRadioButton.setTextColor(defaultTextColor);
                mediumRadioButton.setTextColor(defaultTextColor);
                largeRadioButton.setTextColor(defaultTextColor);

                if (checkedId == R.id.smallRadioButton) {
                    smallRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    smallRadioButton.setTextColor(selectedTextColor);
                } else if (checkedId == R.id.mediumRadioButton) {
                    mediumRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    mediumRadioButton.setTextColor(selectedTextColor);
                } else if (checkedId == R.id.largeRadioButton) {
                    largeRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    largeRadioButton.setTextColor(selectedTextColor);
                }
            }
        });
        return view;
    }

    private void handleAddToCart() {
        String selectedPizza = pizzaSpinner.getSelectedItem().toString();
        String selectedSize = getSelectedSize();
        List<String> selectedToppings = getSelectedToppings();

        double totalPizzaPrice = calculateTotalPrice(selectedPizza, selectedSize, selectedToppings);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = dbHelper.insertPizzaOrder(selectedPizza, selectedSize, String.join(", ", selectedToppings), totalPizzaPrice);

        db.close();

        if (rowId != -1) {
            Toast.makeText(requireContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to add item to cart", Toast.LENGTH_SHORT).show();
        }
        pizzaSpinner.setSelection(0);
        sizeRadioGroup.clearCheck();
        toppingsListView.setVisibility(View.GONE);
        for (int i = 0; i < toppingsListView.getCount(); i++) {
            toppingsListView.setItemChecked(i, false);
        }
    }



    private String getSelectedSize() {
        int selectedRadioButtonId = sizeRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == R.id.smallRadioButton) {
            return getString(R.string.small_size);
        } else if (selectedRadioButtonId == R.id.mediumRadioButton) {
            return getString(R.string.medium_size);
        } else if (selectedRadioButtonId == R.id.largeRadioButton) {
            return getString(R.string.large_size);
        } else {
            return "Not selected";
        }
    }

    private List<String> getSelectedToppings() {
        List<String> selectedToppings = new ArrayList<>();
        SparseBooleanArray checkedItems = toppingsListView.getCheckedItemPositions();

        for (int i = 0; i < toppingsListView.getCount(); i++) {
            if (checkedItems.get(i)) {
                selectedToppings.add(toppingsListView.getItemAtPosition(i).toString());
            }
        }

        return selectedToppings;
    }

    private double getBasePizzaPrice(String pizza) {
        String[] pizzaNames = getResources().getStringArray(R.array.pizza_options);
        if (pizzaNames[1].equals(pizza)) {
            return 6.99;
        } else if (pizzaNames[2].equals(pizza)) {
            return 6.99;
        } else if (pizzaNames[3].equals(pizza)) {
            return 7.99;
        }else if (pizzaNames[4].equals(pizza)) {
            return 9.99;
        } else if (pizzaNames[5].equals(pizza)) {
            return 9.99;
        }else {
            return 0.0;
        }
    }
    private double getSizePrice(String size) {
        if (getString(R.string.small_size).equals(size)) {
            return 0.0;
        } else if (getString(R.string.medium_size).equals(size)) {
            return 2;
        } else if (getString(R.string.large_size).equals(size)) {
            return 4;
        } else {
            return 0.0;
        }
    }

    public double calculateTotalPrice(String selectedPizza, String selectedSize, List<String> selectedToppings) {
        initializeToppingPrices();
        double basePrice = getBasePizzaPrice(selectedPizza);
        double sizePrice = getSizePrice(selectedSize);
        double toppingPrice = 0.0;

        for (String topping : selectedToppings) {
            if (toppingPrices.containsKey(topping)) {
                toppingPrice += toppingPrices.get(topping);
            }
        }

        return basePrice + sizePrice + toppingPrice;
    }

    private void initializeToppingPrices() {
        toppingPrices = new HashMap<>();
        String[] toppings = getResources().getStringArray(R.array.toppings_array);
        toppingPrices.put(toppings[0], 2.00);
        toppingPrices.put(toppings[1], 2.00);
        toppingPrices.put(toppings[2], 0.5);
        toppingPrices.put(toppings[3], 0.5);
        toppingPrices.put(toppings[4], 2.00);
        toppingPrices.put(toppings[5], 2.00);
        toppingPrices.put(toppings[6], 2.00);
        toppingPrices.put(toppings[7], 2.00);
        toppingPrices.put(toppings[8], 0.5);
    }
}