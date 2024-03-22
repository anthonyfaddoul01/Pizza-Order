package com.example.navigationbarapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
public class MenuFragment extends Fragment {

    private List<MenuItem> menuItems;
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = view.findViewById(R.id.menu_recycler_view);
        menuItems = createMenuItems(); // Implement this method to create your menu items
        menuAdapter = new MenuAdapter(menuItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(menuAdapter);

        return view;
    }

    private List<MenuItem> createMenuItems() {
        String[] pizzaOptionsArray = getResources().getStringArray(R.array.pizza_options);

        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem(pizzaOptionsArray[1], "$6.99", R.drawable.margherita));
        items.add(new MenuItem(pizzaOptionsArray[2], "$9.99", R.drawable.pepperoni));
        items.add(new MenuItem(pizzaOptionsArray[3], "$7.99", R.drawable.veggie));
        items.add(new MenuItem(pizzaOptionsArray[4], "$9.99", R.drawable.bbq_chicken));
        items.add(new MenuItem(pizzaOptionsArray[5], "$9.99", R.drawable.four_cheese));

        return items;
    }
}
