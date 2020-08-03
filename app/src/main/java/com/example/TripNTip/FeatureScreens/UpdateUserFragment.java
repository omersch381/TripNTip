package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TripNTip.R;
import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Map;
import java.util.PriorityQueue;

public class UpdateUserFragment extends DialogFragment {
    View content;
    private EditText username;
    private EditText country;
    private DatabaseReference mDatabase;
    private String newusername="niv naory ";


    public static UpdateUserFragment newInstance(String trip_information) {
        return new UpdateUserFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_update_user, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        username= (EditText) view.findViewById(R.id.change_username);
        country= (EditText) view.findViewById(R.id.change_country);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUserName=username.getText().toString();
                        String newCountry=country.getText().toString();
                        updateDetails(newUserName,newCountry);
                    }
                });

        return alertDialogBuilder.create();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       return inflater.inflate(R.layout.fragment_update_user, container, false);
    }
    //change !! need to make new key of the database or make that cannot change username once it create!
    public void updateDetails(String newusername,String newCountry) {
       // m.put("country",this.country);
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child("niv naory");
    }
}