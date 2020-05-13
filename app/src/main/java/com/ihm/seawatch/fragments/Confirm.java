package com.ihm.seawatch.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.ihm.seawatch.R;

public class Confirm extends Fragment {

    private ShareButton shareButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.facebook.com/SeaWatch-101141728277356"))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#JoinUsOnSeaWatch")
                        .build())
                .build();

        shareButton = (ShareButton) rootView.findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Return to menu
        view.findViewById(R.id.button_toHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Confirm.this)
                        .navigate(R.id.action_SixthFragment_to_FirstFragment);
            }
        });
    }
}
