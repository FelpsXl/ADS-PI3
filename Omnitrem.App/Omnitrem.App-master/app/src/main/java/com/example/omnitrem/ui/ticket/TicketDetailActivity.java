package com.example.omnitrem.ui.ticket;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.omnitrem.R;
import com.example.omnitrem.databinding.ActivityTicketDetailBinding;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TicketDetailActivity extends AppCompatActivity {

    private ActivityTicketDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ticket_detail);

        String qrCodeData = getIntent().getStringExtra("QR_CODE_DATA");

        if (qrCodeData != null && !qrCodeData.isEmpty()) {
            binding.tvQrDataDebug.setText("Dados QR: " + qrCodeData);
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeData, BarcodeFormat.QR_CODE, 800, 800);
                binding.ivQrCode.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_LONG).show();
                binding.ivQrCode.setImageResource(R.drawable.ic_error_placeholder);
            }
        } else {
            Toast.makeText(this, "Dados do QR Code não encontrados", Toast.LENGTH_LONG).show();
            binding.ivQrCode.setImageResource(R.drawable.ic_error_placeholder);
        }
    }
}