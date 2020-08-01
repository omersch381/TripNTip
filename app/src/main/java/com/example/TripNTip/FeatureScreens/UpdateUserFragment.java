package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.TripNTip.R;

public class UpdateUserFragment extends DialogFragment {
    View content;

    public static UpdateUserFragment newInstance(String trip_information) {
        return new UpdateUserFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.fragment_update_user, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("change personal details");
        alertDialogBuilder.setView(content);
        alertDialogBuilder.create();
        return alertDialogBuilder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_user, container, false);
    }

    public void updateDetails(View v) {

    }
}