package com.example.TripNTip.TripNTip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.TripNTip.FeatureScreens.AddTripActivity;
import com.example.TripNTip.FeatureScreens.GridFragment;
import com.example.TripNTip.FeatureScreens.ProfileActivity;
import com.example.TripNTip.FeatureScreens.SearchFragment;
import com.example.TripNTip.R;
import com.example.TripNTip.Utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;


public class TravelFeedActivity extends AppCompatActivity implements SearchFragment.OnDataPass, Constants {

    private String apiKey;
    private HashMap<String, Trip> trips;
    private DatabaseReference rootRef;
    private HashMap<String, Bitmap> tripsAlbum;
    private int numOfPictures = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_feed_activity);

        rootRef = FirebaseDatabase.getInstance().getReference();

        loadData();
    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        apiKey = bundle.getString(API_KEY_LABEL, "");
        if (apiKey.equals(""))
            loadAPIKeyAgain();

        trips = (HashMap<String, Trip>) getIntent().getSerializableExtra(TRIPS_LABEL);
        if (trips == null)
            loadTripsAgain();

        tripsAlbum = new HashMap<>();

        loadBitmaps();
    }

    private void loadBitmaps() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Task<ListResult> listRef = storage.getReference().child("images").listAll();
        listRef.addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (final StorageReference item : listResult.getItems()) {
                    final ProgressDialog progressDialog = ProgressDialog.show(TravelFeedActivity.this, "", getResources().getString(R.string.waitMessage));

                    item.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            tripsAlbum.put(item.getName(), bitmap);
                            progressDialog.dismiss();
                            numOfPictures++;
                            if (numOfPictures == trips.size())
                                handleViews();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.tripAlbumLoadingFailureMsg), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tripAlbumLoadingFailureMsg), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void handleViews() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRIPS_LABEL, trips);
        bundle.putSerializable(TRIPS_ALBUM_LABEL, tripsAlbum);
        commitFragment(new SearchFragment(), R.id.search_fragment_container, ADD_ACTION, bundle);
        commitFragment(new GridFragment(apiKey, tripsAlbum), R.id.grid_fragment_container, ADD_ACTION, bundle);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navAdd:
                            Intent intentAdd = new Intent(TravelFeedActivity.this, AddTripActivity.class);
                            TravelFeedActivity.this.startActivity(intentAdd);
                            break;
                        case R.id.navProfile:
                            Intent intentProfile = new Intent(TravelFeedActivity.this, ProfileActivity.class);
                            TravelFeedActivity.this.startActivity(intentProfile);
                            break;

                        //TODO Niv - nav bar fix
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
        bundle.putSerializable(TRIPS_LABEL, trips);
        commitFragment(new GridFragment(apiKey, tripsAlbum), R.id.grid_fragment_container, REPLACE_ACTION, bundle);
    }

    private void commitFragment(Fragment fragment, int fragment_container_id, String action, Bundle bundle) {
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
        finish();
    }

    private void loadTripsAgain() {
        loadData(rootRef.child("trips"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Trip currentTrip = ds.getValue(Trip.class);
                    assert currentTrip != null;
                    trips.put(currentTrip.getName(), currentTrip);

                    //update gridView
                    onDataPass("");
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.tripLoadingFailureMsg), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadAPIKeyAgain() {
        loadData(rootRef.child("apiKeys"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    apiKey = (String) ds.getValue();

                //update gridView
                onDataPass("");
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apiKeyLoadingFailureMsg), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public interface OnGetDataListener {
        void onSuccess(DataSnapshot dataSnapshot);

        void onFailure();
    }

    public void loadData(final DatabaseReference ref, final OnGetDataListener listener) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }
        });
    }
}


