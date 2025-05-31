package com.example.omnitrem.ui.payment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.omnitrem.R;
import com.example.omnitrem.databinding.ActivityPaymentProcessingBinding;

public class PaymentProcessingActivity extends AppCompatActivity {

    private ActivityPaymentProcessingBinding binding;
    private static final long FAKE_PAYMENT_LINK_DELAY_MS = 3000; // 3 seconds
    private String ticketId;
    private String qrCodeData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_processing);
        binding.setActivity(this);

        ticketId = getIntent().getStringExtra("TICKET_ID_STRING");
        qrCodeData = getIntent().getStringExtra("QR_CODE_DATA");


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                binding.tvHiddenPaymentLink.setTextColor(getResources().getColor(R.color.purple_500, getTheme()));
                binding.tvHiddenPaymentLink.setVisibility(View.VISIBLE);
            }
        }, FAKE_PAYMENT_LINK_DELAY_MS);
    }

    public void onHiddenLinkClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        startActivity(browserIntent);

        proceedToPaymentSuccess();
    }

    private void proceedToPaymentSuccess() {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("TICKET_ID_STRING", ticketId);
        intent.putExtra("QR_CODE_DATA", qrCodeData);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}