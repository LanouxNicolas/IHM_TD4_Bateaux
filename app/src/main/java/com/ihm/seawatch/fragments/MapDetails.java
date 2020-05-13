package com.ihm.seawatch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ihm.seawatch.R;

public class MapDetails extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_details, container, false);
        String[] elements = getArguments().getString("everything").split("///");

        for(String tmp : elements){
            System.out.println(tmp);
        }

        TextView textView = rootView.findViewById(R.id.detail_input);
        textView.setText(elements[0]);

        TextView textView2 = rootView.findViewById(R.id.mapDetail_time);
        textView2.setText(elements[1]);

        TextView textView3 = rootView.findViewById(R.id.mapDetail_detail1);
        textView3.setText(elements[4]);

        TextView textView4 = rootView.findViewById(R.id.mapDetail_detail2);
        textView4.setText(elements[2]);

        TextView textView5 = rootView.findViewById(R.id.mapDetail_detail3);
        textView5.setText(elements[5]);

        System.out.println(getArguments().getString("Date"));

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
