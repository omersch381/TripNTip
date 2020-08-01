package com.example.TripNTip.TripNTip;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.TripNTip.R;

import com.example.TripNTip.FeatureScreens.Add_Activity;
import com.example.TripNTip.FeatureScreens.GridFragment;
import com.example.TripNTip.FeatureScreens.ProfileActivity;
import com.example.TripNTip.FeatureScreens.SearchFragment;
import com.example.TripNTip.Utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;


public class TravelFeedActivity extends AppCompatActivity implements SearchFragment.OnDataPass, Constants {

    private String apiKey;
    private HashMap<String, Trip> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_feed_activity);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        apiKey = bundle.getString("apiKey");
        trips = (HashMap<String, Trip>) getIntent().getSerializableExtra(TRIPS_KEYWORD);
        handleViews();
    }


    private void handleViews() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRIPS_KEYWORD, trips);
        commitFragment(new SearchFragment(), R.id.search_fragment_container, trips, ADD_ACTION, bundle);
        commitFragment(new GridFragment(apiKey), R.id.grid_fragment_container, trips, ADD_ACTION, bundle);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navAdd:
                            Intent intentAdd = new Intent(TravelFeedActivity.this, Add_Activity.class);
                            TravelFeedActivity.this.startActivity(intentAdd);
                            break;
                        case R.id.navProfile:
                            Intent intentProfile = new Intent(TravelFeedActivity.this, ProfileActivity.class);
                            TravelFeedActivity.this.startActivity(intentProfile);
                            break;

                        //TODO Niv
//                        case R.id.navSearch:
//                            Intent intentSearch = new Intent(TravelFeedActivity.this, SearchActivity.class);
//                            TravelFeedActivity.this.startActivity(intentSearch);
//                            break;
                    }
                    return true;
                }
            };

    @Override
    public void onDataPass(String data) {
        Bundle bundle = new Bundle();
        bundle.putString(GridFragment.QUERY_RECEIVED, data);
        bundle.putSerializable(TRIPS_KEYWORD, trips);
        commitFragment(new GridFragment(), R.id.grid_fragment_container, trips, REPLACE_ACTION, bundle);
    }

    private void commitFragment(Fragment fragment, int fragment_container_id, HashMap<String, Trip> trips, String action, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (action.equals(ADD_ACTION))
            transaction.add(fragment_container_id, fragment);
        else
            transaction.replace(fragment_container_id, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


