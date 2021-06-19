package com.anshagrawal.dcmlkit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.ActionBar;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anshagrawal.dcmlkit.databinding.ActivityMainBinding;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Grid;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int rotationAfterCrop;
    ActivityMainBinding activityMainBinding;
    String url = "https://acm-dcryptor.herokuapp.com/api/v1/";
    ArrayAdapter<String> arrayAdapter;
<<<<<<< HEAD
    ArrayList<String> grocery;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int checkedItem;
    private String selected;


    private final String CHECKEDITEM = "checked_item";
=======
    ArrayList<String> decodes;

>>>>>>> 6136268fb60bc20dd997400e45822d57393b8c95
    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);
        sharedPreferences = this.getSharedPreferences("themes", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        switch (getCheckedItem()) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;

            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }

        decodes = new ArrayList<>();

        activityMainBinding.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMainBinding.myListView.setAdapter(null);
                decodes.clear();
                activityMainBinding.trash.setVisibility(View.GONE);
            }
        });

        activityMainBinding.empty.setBackgroundResource(R.drawable.rounded_corner);
        activityMainBinding.empty.setClipToOutline(true);

//        if(activityMainBinding.myListView.getAdapter()==null){
//            activityMainBinding.trash.setVisibility(View.GONE);
//        }
//        if(activityMainBinding.myListView.getAdapter()!=null){
//            activityMainBinding.trash.setVisibility(View.VISIBLE);
//        }

        activityMainBinding.myListView.setEmptyView(activityMainBinding.empty);

        activityMainBinding.cameraView.setBackgroundResource(R.drawable.rounded_corner);
        activityMainBinding.cameraView.setClipToOutline(true);

        activityMainBinding.scannedText.setBackgroundResource(R.drawable.rounded_corner);
        activityMainBinding.scannedText.setClipToOutline(true);

        activityMainBinding.myListView.setBackgroundResource(R.drawable.rounded_corner);
        activityMainBinding.myListView.setClipToOutline(true);

        //makes the TextView scrollable
        activityMainBinding.scannedText.setMovementMethod(new ScrollingMovementMethod());


        //camera listener, listens the activity of camera
        activityMainBinding.cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                rotationAfterCrop = result.getRotation();
                result.toBitmap(new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        try {
                            cropImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        processBitmap(bitmap, result.getRotation());
                    }
                });
            }
        });

        activityMainBinding.myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView) view).getText().toString();
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied to clipboard!", text);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
        activityMainBinding.cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);

        activityMainBinding.btnDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedFinal = activityMainBinding.scannedText.getText().toString();

                try {
                    decodeCipher(editedFinal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        activityMainBinding.cameraView.setLifecycleOwner(this);

        //take photo button click listener
        activityMainBinding.btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }


    //Saves the bitmap to external cache directory so that the image does not get stored in the device
    // storage and return Uri for the image cropping library


    private Uri saveBitmapToCache(Bitmap bitmap) {
        //get cache directory
        File cachePath = new File(getExternalCacheDir(), "my_images/");
        cachePath.mkdirs();
        File file = new File(cachePath, "Image_123.png");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //get file uri
        Uri myImageFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        return myImageFileUri;
    }


    private void processBitmap(Bitmap bitmap, int rotation) {
        InputImage img = InputImage.fromBitmap(bitmap, rotation);

        //taking an instance of google mlkit textrecognizer
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(img).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                activityMainBinding.scannedText.setText(text.getText());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.themes){
            showDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    private void takePhoto() {
        activityMainBinding.cameraView.takePictureSnapshot();
    }

    private void cropImage(Bitmap bitmap) throws IOException {
        Uri uri = saveBitmapToCache(bitmap);
        CropImage.activity(uri)
                .start(this);
    }

    @Override
    //crop image activity result listener, this block is executed when the image is cropped
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap imageAfterCrop = null;
                try {
                    imageAfterCrop = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    ocr(imageAfterCrop, rotationAfterCrop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                activityMainBinding.cropImageView.setVisibility(View.VISIBLE);
                activityMainBinding.cropImageView.setImageBitmap(imageAfterCrop);

            }
        }
    }

    //processes bitmap of the cropped image
    private void ocr(Bitmap imageAfterCrop, int rotationAfterCrop) {
        processBitmap(imageAfterCrop, rotationAfterCrop);
    }

    //call the api to recursively decode the cipher
    private void decodeCipher(String text) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final int[] length = new int[1];

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("decoded_data");
                    StringBuilder finalDecodedText = new StringBuilder();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String s = jsonArray.getString(i);
                        decodes.add(s);
                        if (i < (jsonArray.length() - 1)) {
                            finalDecodedText.append(jsonArray.getString(i)).append("\n");
                        } else {
                            finalDecodedText.append(jsonArray.getString(i));
                        }
                    }
                    String[] strArray = new String[decodes.size()];
                    for (int i = 0; i < decodes.size(); i++) {
                        strArray[i] = decodes.get(i);
                    }
                    activityMainBinding.trash.setVisibility(View.VISIBLE);

                    arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_listview, strArray);
                    activityMainBinding.myListView.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void showDialog() {

        String[] themes = this.getResources().getStringArray(R.array.theme);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Select Theme");
        builder.setSingleChoiceItems(R.array.theme, getCheckedItem(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = themes[which];
                checkedItem = which;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selected == null) {
                    selected = themes[which];
                    checkedItem = which;
                }

                switch (selected) {
                    case "System Default":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;

                    case "Dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;

                    case "Light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                }
                setCheckedItem(checkedItem);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private int getCheckedItem() {
        return sharedPreferences.getInt(CHECKEDITEM, 0);
    }

    private void setCheckedItem(int i) {
        editor.putInt(CHECKEDITEM, i);
        editor.apply();

    }


}