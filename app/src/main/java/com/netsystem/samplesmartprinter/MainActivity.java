package com.netsystem.samplesmartprinter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check and request storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                requestManageExternalStoragePermission();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }

        // Initialize UI components
        EditText productName = findViewById(R.id.productName);
        EditText Price = findViewById(R.id.Price);
        EditText Barcode = findViewById(R.id.Barcode);
        EditText Qty = findViewById(R.id.Qty);
        Button printButton = findViewById(R.id.printButton);
        TextView ResponseText = findViewById(R.id.Response);

        // Set print button click listener
        printButton.setOnClickListener(view -> {
            URL url;
            try {
                Uri.Builder builder = new Uri.Builder();

                // API URL to download the *.spfmtz format file
                String apiUrl = "http://192.168.101.56:3000/download/smapri.spfmtz";

                // Log format file path
                Log.d("MainActivity", "Format file URL: " + apiUrl);

                // Server endpoint for printing
                builder.scheme("http");
                builder.encodedAuthority("localhost:8080");
                builder.path("/Format/Print");

                // Add parameters for the print request
                builder.appendQueryParameter("__format_archive_url", apiUrl);
                builder.appendQueryParameter("__format_archive_update", "update");
                builder.appendQueryParameter("__format_id_number", "1");
                builder.appendQueryParameter("商品名", productName.getText().toString());
                builder.appendQueryParameter("価格", Price.getText().toString());
                builder.appendQueryParameter("バーコード", Barcode.getText().toString());
                builder.appendQueryParameter("(発行枚数)", Qty.getText().toString());

                url = new URL(builder.build().toString());

                // Log URL before sending the request
                Log.d("MainActivity", "Sending print request to URL: " + url.toString());

                // Show a message before sending the request
                ResponseText.setText("Sending print request...");

                // Send the request (asynchronously)
                printButton.setEnabled(false);
                HttpRequestAsync httpRequestAsync = new HttpRequestAsync(ResponseText, printButton);
                httpRequestAsync.execute(url);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                ResponseText.setText("Error: Invalid URL");

                // Log error creating URL
                Log.e("MainActivity", "Error creating URL: " + e.getMessage());
            }
        });
}
    private void requestManageExternalStoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "External storage permission granted");
            } else {
                Log.e("Permissions", "External storage permission denied");
            }
        } else if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Manage external storage permission granted");
            } else {
                Log.e("Permissions", "Manage external storage permission denied");
            }
        }
    }
}


