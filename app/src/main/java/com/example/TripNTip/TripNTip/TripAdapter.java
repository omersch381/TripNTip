package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.TripNTip.R;
import com.example.TripNTip.SquaredImageView;
import com.example.TripNTip.Trip;
import com.example.TripNTip.TripView;
import com.example.TripNTip.TripsBoard;
import com.example.Utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripAdapter extends BaseAdapter implements Constants {

    private TripsBoard board;
    private Context myContext;
    private StorageReference listRef;
    private ArrayList<Trip> filteredTrips;

    private SquaredImageView[] tripsAlbum;
    private int counter = 0;

    public TripAdapter(Context myContext, TripsBoard board) {
        this.board = board;
        this.myContext = myContext;
        listRef = FirebaseStorage.getInstance().getReference().getRoot().child("Images");
//        generateTripsAlbum();
        filteredTrips = new ArrayList<>();
        initializeTrips();
    }

    private void initializeTrips() {
        for (int i = 0; i < board.getBoardSize(); i++)
            filteredTrips.add(board.getTrip(i));
    }

    private void generateTripsAlbum() {

        // Instantiating the Album
        int numOfTrips = board.getBoardSize();
        tripsAlbum = new SquaredImageView[numOfTrips];

        for (int i = 0; i < numOfTrips; i++)
            tripsAlbum[i] = new SquaredImageView(myContext);

        // Downloading the Album's images from Firebase
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (final StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(tripsAlbum[counter]);
                            tripsAlbum[counter++].setName(item.getName());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(myContext, myContext.getResources().getString(R.string.picturesLoadError), Toast.LENGTH_LONG).show();
            }
        });
        counter = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TripView tripView = (TripView) convertView;
        if (tripView == null)
            tripView = new TripView(myContext);

        tripView = getASquaredTile(tripView);

        final Trip trip = filteredTrips.get(position);

        tripView.setText(trip.getName());

        return tripView;
    }

    private int getTripIndex(Trip trip) {
        for (int i = 0; i < tripsAlbum.length; i++) {
            if (trip.getName().equals(tripsAlbum[i].getName()))
                return i;
        }
        return -1;
    }

    public void filter(ArrayList<Trip> filteredTrips) {
        setFilteredTrips(filteredTrips);
    }

    public void setFilteredTrips(ArrayList<Trip> filteredTrips) {
        if (filteredTrips != null)
            this.filteredTrips = filteredTrips;
    }

//    private void getPictureFromFirebase(StorageReference currentGsReference, final SquaredImageView finalView) {
//        currentGsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                finalView.setImageBitmap(bitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // TODO
//            }
//        });
//    }
//
//
//    private String getReference(StorageReference item) {
//        return APP_URL_REFERENCE_PREFIX + item.getPath();
//    }

    @Override
    public int getCount() {
        return filteredTrips.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredTrips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private TripView getASquaredTile(View convertView) {
        final TripView tripView;
        if (convertView == null)
            tripView = new TripView(myContext);
        else
            tripView = (TripView) convertView;

        tripView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        tripView.getViewTreeObserver().removeOnPreDrawListener(this);
                        tripView.setLayoutParams(new GridView.LayoutParams(tripView.getWidth(), tripView.getWidth()));
                        return false;
                    }
                }
        );
        return tripView;
    }

    public SquaredImageView[] getTripsAlbum() {
        return tripsAlbum;
    }
}
