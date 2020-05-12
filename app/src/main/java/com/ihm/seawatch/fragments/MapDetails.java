package com.ihm.seawatch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ihm.seawatch.R;

public class MapDetails extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_details, container, false);
        TextView textView = rootView.findViewById(R.id.detail_input);
        textView.setText(getArguments().getString("detailsInput"));

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Return to home
        view.findViewById(R.id.map_detail_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MapDetails.this).navigate(R.id.action_FourthFragment_to_SecondFragment);
            }
        });
    }
}
