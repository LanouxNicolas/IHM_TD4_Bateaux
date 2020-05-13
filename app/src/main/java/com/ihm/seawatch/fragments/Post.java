package com.ihm.seawatch.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ihm.seawatch.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.ihm.seawatch.fragments.Notifications.CHANNEL_3_ID;

public class Post extends Fragment {

    private final int REQUEST_CODE = 42;
    private final String FILE_NAME = "photo.jpg";
    private File photoFile;
    private int notificationId = 0;
    private LocationManager mLocationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLocationManager = (LocationManager) this.requireContext().getSystemService(Context.LOCATION_SERVICE);




        // Return to menu
        view.findViewById(R.id.button_toHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = getActivity().findViewById(R.id.positionActuelle);
                double latitude = 0;
                double longitude = 0;
                if (checkBox.isChecked()) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } catch(Exception ignored) {
                    }
                } else {
                    try {
                        latitude = Double.parseDouble(((EditText)getActivity().findViewById(R.id.input_N)).getText().toString());
                        longitude = Double.parseDouble(((EditText)getActivity().findViewById(R.id.input_E)).getText().toString());
                    } catch (Exception ignored) {
                    }
                }
                String message = "";
                String title = "Le post a bien été envoyé";
                try {
                    message = ((EditText) getActivity().findViewById(R.id.detail_input)).getText().toString();
                } catch(Exception ignored) {
                }

                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

                SQLiteDatabase sqLiteDatabase = requireContext().openOrCreateDatabase("geopoints.db", Context.MODE_PRIVATE, null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("Latitude", latitude);
                contentValues.put("Longitude", longitude);
                contentValues.put("Details", message);
                contentValues.put("Date",currentDate);
                String tmp = null;
                tmp = ((TextView) getActivity().findViewById(R.id.input_temperature)).getText().toString();
                contentValues.put("Temperature",tmp);
                tmp = ((Spinner) getActivity().findViewById(R.id.post_spinner2)).getSelectedItem().toString();
                contentValues.put("Courant",tmp);
                tmp = ((TextView) getActivity().findViewById(R.id.input_vent)).getText().toString();
                contentValues.put("Vent",tmp);

                tmp = ((TextView) getActivity().findViewById(R.id.input_precipitations)).getText().toString();
                contentValues.put("Precipitations",tmp);
                //contentValues.put("DirectionVent",tmp);
                //contentValues.put("DirectionCourant",tmp);
                sqLiteDatabase.insert("Incidents", null, contentValues);
                sqLiteDatabase.close();

                sendNotificationOnChannel(title, message, CHANNEL_3_ID, NotificationCompat.PRIORITY_HIGH);
                NavHostFragment.findNavController(Post.this)
                        .navigate(R.id.action_ThirdFragment_to_SixthFragment);
            }
        });

        view.findViewById(R.id.positionActuelle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = getActivity().findViewById(R.id.positionActuelle);
                if (checkBox.isChecked()) {
                    EditText editText = getActivity().findViewById(R.id.input_E);
                    editText.setBackgroundColor(Color.GRAY);
                    editText.setEnabled(false);
                    EditText editText2 = getActivity().findViewById(R.id.input_N);
                    editText2.setEnabled(false);
                    editText2.setBackgroundColor(Color.GRAY);
                } else {
                    EditText editText = getActivity().findViewById(R.id.input_E);
                    editText.setEnabled(true);
                    editText.setBackgroundResource(R.drawable.border);
                    EditText editText2 = getActivity().findViewById(R.id.input_N);
                    editText2.setEnabled(true);
                    editText2.setBackgroundResource(R.drawable.border);
                }
            }
        });

        view.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* The idea of taking pictures here:
                    Click the button to create an Intent and pass a filePath.
                    After opening the camera, take a picture and store the image file in the specified filePath.
                    Then the imageView here reads the image from the filePath. */
                // The reason for using filePath instead of IntentBundle: if only intent bundle is used, the file size is limited to 1MB, and HD images cannot be transferred)
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = getPhotoFile(FILE_NAME);
                } catch (IOException e) {
                    Toast.makeText(requireActivity(),"Erreur : " + e, Toast.LENGTH_SHORT).show();
                }
                // Wrap file object as content provider using FileProvider class (higher security, prevent external read file path)
                Uri fileProvider = FileProvider.getUriForFile(requireContext(), "com.ihm.seawatch.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                // Check if camera app is available
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE);
                } else {
                    Toast.makeText(requireActivity(),"Impossible d'ouvrir l'appareil photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Read the saved photos from the path
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            TextView tv = getView().findViewById(R.id.post_picName);
            tv.setText(FILE_NAME);
            ImageView imageView = getView().findViewById(R.id.bitMap);
            imageView.setImageBitmap(takenImage);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private File getPhotoFile(String fileName) throws IOException {
        File storageDirectory = this.requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDirectory);
    }

    private void sendNotificationOnChannel(String title, String message, String channelId, int priority) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getActivity().getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.app_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.app_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority);
        try {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.app_launcher_round);
            notification.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(largeIcon));
        }
        catch(Exception ignored){

        }
        NotificationManagerCompat.from(getActivity()).notify(++notificationId, notification.build());
    }
}