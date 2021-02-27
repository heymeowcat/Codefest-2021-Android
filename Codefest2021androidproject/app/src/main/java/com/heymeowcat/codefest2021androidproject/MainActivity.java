package com.heymeowcat.codefest2021androidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.heymeowcat.codefest2021androidproject.CustomerFragments.news;
import com.heymeowcat.codefest2021androidproject.CustomerFragments.products;
import com.heymeowcat.codefest2021androidproject.CustomerFragments.tickets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView btmnav = findViewById(R.id.bottom_navigation);
        btmnav.setOnNavigationItemSelectedListener(navListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment todayfragment = new products();
        todayfragment.setRetainInstance(true);
        transaction.replace(R.id.fragment_container, todayfragment);
        transaction.commit();
    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_products:
                        selectedFragment = new products();
                        break;
                    case R.id.nav_tickets:
                        selectedFragment = new tickets();
                        break;
                    case R.id.nav_news:
                        selectedFragment = new news();
                        break;
                }
                assert selectedFragment != null;
                selectedFragment.setRetainInstance(true);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();

                return true;
            };
}