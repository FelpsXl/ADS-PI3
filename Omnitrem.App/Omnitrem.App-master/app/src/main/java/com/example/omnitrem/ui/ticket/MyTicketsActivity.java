package com.example.omnitrem.ui.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.omnitrem.R;
import com.example.omnitrem.databinding.ActivityMyTicketsBinding;
import com.example.omnitrem.network.ApiClient;
import com.example.omnitrem.network.model.ticket.TicketResponse;
import com.example.omnitrem.network.model.ticket.UserTicketsListResponse;
import com.example.omnitrem.network.service.TicketService;
import com.example.omnitrem.ui.MyTicketsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTicketsActivity extends AppCompatActivity implements MyTicketsAdapter.OnTicketClickListener {

    private ActivityMyTicketsBinding binding;
    private TicketService ticketService;
    private MyTicketsAdapter ticketsAdapter;
    private static final String TAG = "MyTicketsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_tickets);
        binding.setActivity(this);

        setSupportActionBar(binding.toolbarMyTickets);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ticketService = ApiClient.getTicketService();
        setupRecyclerView();
        fetchUserTickets();
    }

    private void setupRecyclerView() {
        ticketsAdapter = new MyTicketsAdapter(this, this);
        binding.rvTickets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTickets.setAdapter(ticketsAdapter);
    }

    private void fetchUserTickets() {
        binding.progressBarMyTickets.setVisibility(View.VISIBLE);
        binding.tvNoTickets.setVisibility(View.GONE);
        binding.rvTickets.setVisibility(View.GONE);

        Call<UserTicketsListResponse> call = ticketService.getUserTickets();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserTicketsListResponse> call, @NonNull Response<UserTicketsListResponse> response) {
                binding.progressBarMyTickets.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getTickets() != null) {
                    List<TicketResponse> fetchedTickets = response.body().getTickets();
                    if (fetchedTickets.isEmpty()) {
                        binding.tvNoTickets.setVisibility(View.VISIBLE);
                        binding.rvTickets.setVisibility(View.GONE);
                    } else {
                        ticketsAdapter.setTickets(fetchedTickets);
                        binding.tvNoTickets.setVisibility(View.GONE);
                        binding.rvTickets.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "Tickets fetched: " + fetchedTickets.size());
                } else {
                    Log.e(TAG, "Error fetching tickets: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MyTicketsActivity.this, "Falha ao carregar bilhetes.", Toast.LENGTH_SHORT).show();
                    binding.tvNoTickets.setVisibility(View.VISIBLE);
                    binding.rvTickets.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserTicketsListResponse> call, @NonNull Throwable t) {
                binding.progressBarMyTickets.setVisibility(View.GONE);
                Log.e(TAG, "Network error fetching tickets: ", t);
                Toast.makeText(MyTicketsActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
                binding.tvNoTickets.setVisibility(View.VISIBLE);
                binding.rvTickets.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onTicketClick(TicketResponse ticket) {
        Log.d(TAG, "Clicked ticket: " + ticket.getId() + " with QR Data: " + ticket.getQrCodeData());
        Intent intent = new Intent(this, TicketDetailActivity.class);
        intent.putExtra("QR_CODE_DATA", ticket.getQrCodeData());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}