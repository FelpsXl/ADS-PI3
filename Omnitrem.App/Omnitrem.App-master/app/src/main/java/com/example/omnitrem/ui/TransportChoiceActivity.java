package com.example.omnitrem.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // For logging clicks
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast; // For example actions

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil; // Import for DataBinding

// Import your generated binding class
import com.example.omnitrem.BR;
import com.example.omnitrem.R;
import com.example.omnitrem.databinding.ActivityTransportChoiceBinding;
import com.example.omnitrem.databinding.DialogConfirmTicketPurchaseBinding;
import com.example.omnitrem.network.ApiClient;
import com.example.omnitrem.network.model.ticket.CreateTicketBody;
import com.example.omnitrem.network.model.ticket.SingleTicketApiResponse;
import com.example.omnitrem.network.model.ticket.TicketResponse;
import com.example.omnitrem.network.service.TicketService;
import com.example.omnitrem.ui.payment.PaymentProcessingActivity;
import com.example.omnitrem.ui.ticket.MyTicketsActivity;
import com.example.omnitrem.ui.user.LoginActivity;
import com.example.omnitrem.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransportChoiceActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TicketService ticketService;
    private AlertDialog confirmDialog;

    private static final String TAG = "TransportChoiceActivity";

    public static class ConfirmDialogViewModel extends BaseObservable {
        public String transportTypeDisplay;
        public String transportTypeApiValue;
        private String selectedPaymentMethod;

        public ConfirmDialogViewModel(String display, String apiValue) {
            this.transportTypeDisplay = display;
            this.transportTypeApiValue = apiValue;
            this.selectedPaymentMethod = "PIX";
        }

        @Bindable
        public String getSelectedPaymentMethod() {
            return selectedPaymentMethod;
        }

        public void setSelectedPaymentMethod(String selectedPaymentMethod) {
            if (this.selectedPaymentMethod == null || !this.selectedPaymentMethod.equals(selectedPaymentMethod)) {
                this.selectedPaymentMethod = selectedPaymentMethod;
                notifyPropertyChanged(BR.selectedPaymentMethod);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.omnitrem.databinding.ActivityTransportChoiceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_transport_choice);
        binding.setActivity(this);

        sessionManager = new SessionManager(getApplicationContext());
        ticketService = ApiClient.getTicketService();

        if (sessionManager.isLoggedIn()) {
            String name = sessionManager.getUserName();
            String email = sessionManager.getUserEmail();
            String welcomeMessage = "Bem vindo ao Omnitrem";
            if (name != null && !name.isEmpty()) {
                welcomeMessage = "Bem vindo, " + name + "!";
            } else if (email != null && !email.isEmpty()) {
                welcomeMessage = "Bem vindo, " + email.split("@")[0] + "!";
            }
            binding.tvHeaderWelcome.setText(welcomeMessage);
        } else {
            redirectToLogin();
        }
    }

    public void onTransportOptionClicked(String transportTypeApiValue) {
        Log.d(TAG, "Transport option clicked: " + transportTypeApiValue);

        String transportTypeDisplay;
        switch (transportTypeApiValue.toLowerCase()) {
            case "metro":
                transportTypeDisplay = "Metrô";
                break;
            case "onibus":
                transportTypeDisplay = "Ônibus";
                break;
            case "trem":
                transportTypeDisplay = "Trem";
                break;
            default:
                transportTypeDisplay = transportTypeApiValue;
        }

        showConfirmationDialog(transportTypeDisplay, transportTypeApiValue);
    }

    public void onDialogConfirmPurchaseClicked(String transportTypeApiValue, String paymentMethod) {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
        Log.d(TAG, "Confirm purchase: " + transportTypeApiValue + ", Payment: " + paymentMethod);

        Toast.makeText(this, "Processando compra para " + transportTypeApiValue + " via " + paymentMethod, Toast.LENGTH_LONG).show();
        createTicketApiCall(transportTypeApiValue);
    }

    public void onDialogCancelClicked() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
        Log.d(TAG, "Purchase cancelled");
    }

    public void onViewTicketsClicked() {
        Log.d(TAG, "View Tickets clicked");
        Intent intent = new Intent(this, MyTicketsActivity.class);
        startActivity(intent);
    }

    public void onLogoutClicked() {
        Log.d(TAG, "Logout clicked");
        if (sessionManager == null) {
            sessionManager = new SessionManager(getApplicationContext());
        }
        sessionManager.clearSession();
        redirectToLogin();
    }

    private void showConfirmationDialog(String transportTypeDisplay, String transportTypeApiValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogConfirmTicketPurchaseBinding dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_confirm_ticket_purchase,
                null,
                false
        );

        ConfirmDialogViewModel viewModel = new ConfirmDialogViewModel(transportTypeDisplay, transportTypeApiValue);
        dialogBinding.setDialogViewModel(viewModel);
        dialogBinding.setActivity(this);

        String[] paymentMethods = new String[]{"PIX"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, paymentMethods);
        dialogBinding.actvPaymentMethod.setAdapter(adapter);
        dialogBinding.actvPaymentMethod.setText(viewModel.getSelectedPaymentMethod(), false);

        builder.setView(dialogBinding.getRoot());
        builder.setCancelable(true);

        confirmDialog = builder.create();
        confirmDialog.show();
    }

    private void createTicketApiCall(String transportTypeApiValue) {
        String apiEnumValue = "";
        if (transportTypeApiValue != null && !transportTypeApiValue.isEmpty()) {
            apiEnumValue = transportTypeApiValue.substring(0, 1).toUpperCase() + transportTypeApiValue.substring(1).toLowerCase();
        } else {
            Toast.makeText(this, "Tipo de transporte inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting to create ticket with API value: " + apiEnumValue);
        CreateTicketBody body = new CreateTicketBody(apiEnumValue);
        Call<SingleTicketApiResponse> call = ticketService.createTicket(body);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SingleTicketApiResponse> call, @NonNull Response<SingleTicketApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getTicket() != null) {
                    TicketResponse ticket = response.body().getTicket();
                    Log.d(TAG, "Ticket created successfully: ID " + ticket.getId() + ", QR: " + ticket.getQrCodeData());
                    Toast.makeText(TransportChoiceActivity.this, "Bilhete comprado! ID: " + ticket.getId(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(TransportChoiceActivity.this, PaymentProcessingActivity.class);
                    intent.putExtra("TICKET_ID_STRING", String.valueOf(ticket.getId()));
                    intent.putExtra("QR_CODE_DATA", ticket.getQrCodeData());
                    startActivity(intent);
                } else {
                    String errorMsg = "Falha ao comprar bilhete.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "API Error: " + response.code() + " " + errorMsg);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    } else {
                        Log.e(TAG, "API Error: " + response.code() + " (No error body)");
                    }
                    Toast.makeText(TransportChoiceActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleTicketApiResponse> call, @NonNull Throwable t) {
                // binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network failure purchasing ticket", t);
                Toast.makeText(TransportChoiceActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(TransportChoiceActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}