package com.example.omnitrem.ui.payment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.omnitrem.R;
import com.example.omnitrem.databinding.ActivityPaymentSuccessBinding;
import com.example.omnitrem.ui.TransportChoiceActivity;
import com.example.omnitrem.ui.ticket.TicketDetailActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    private ActivityPaymentSuccessBinding binding;
    private String ticketId;
    private String qrCodeData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_success);
        binding.setActivity(this);

        ticketId = getIntent().getStringExtra("TICKET_ID_STRING");
        qrCodeData = getIntent().getStringExtra("QR_CODE_DATA");
    }

    public void onViewMyTicketClicked() {
        Intent intent = new Intent(this, TicketDetailActivity.class);
        intent.putExtra("QR_CODE_DATA", qrCodeData);
        startActivity(intent);
        finish();
    }
}