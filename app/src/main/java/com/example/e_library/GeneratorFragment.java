package com.example.e_library;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.google.firebase.auth.FirebaseAuth;


public class GeneratorFragment extends AppCompatActivity {
    private FirebaseAuth auth;
    private ImageView qrCodeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_fragment);
        qrCodeImage = findViewById(R.id.qr_code_image);
        auth = FirebaseAuth.getInstance();
        generatorQ();
        ImageView backIcon = findViewById(R.id.backIcon);
        // Nút Back
        backIcon.setOnClickListener(view -> finish());
    }

    private void generatorQ() {
        String userlId = auth.getCurrentUser().getUid();
        if (!userlId.isEmpty()) {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(userlId, BarcodeFormat.QR_CODE, 300, 300);
                    qrCodeImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            };
    }
}