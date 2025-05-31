package com.example.omnitrem.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.omnitrem.R;
import com.example.omnitrem.databinding.ItemTicketBinding;
import com.example.omnitrem.network.model.ticket.TicketResponse;
import java.util.List;
import java.util.ArrayList;

public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.TicketViewHolder> {

    private List<TicketResponse> tickets;
    private OnTicketClickListener clickListener;
    private Context context;

    public interface OnTicketClickListener {
        void onTicketClick(TicketResponse ticket);
    }

    public MyTicketsAdapter(Context context, OnTicketClickListener listener) {
        this.context = context;
        this.tickets = new ArrayList<>();
        this.clickListener = listener;
    }

    public void setTickets(List<TicketResponse> newTickets) {
        this.tickets.clear();
        if (newTickets != null) {
            int limit = Math.min(newTickets.size(), 5);
            for (int i = 0; i < limit; i++) {
                this.tickets.add(newTickets.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_ticket,
                parent,
                false
        );
        return new TicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketResponse ticket = tickets.get(position);
        holder.bind(ticket, clickListener, context);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final ItemTicketBinding binding;

        public TicketViewHolder(ItemTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TicketResponse ticket, OnTicketClickListener listener, Context context) {
            binding.setTicket(ticket);
            binding.setClickListener(listener);

            String transportTypeLower = ticket.getTransportType().toLowerCase();
            int iconResId;
            if (transportTypeLower.contains("metro")) {
                iconResId = R.drawable.ic_metro_placeholder;
            } else if (transportTypeLower.contains("onibus") || transportTypeLower.contains("ônibus")) {
                iconResId = R.drawable.ic_onibus_placeholder;
            } else if (transportTypeLower.contains("trem")) {
                iconResId = R.drawable.ic_trem_placeholder;
            } else {
                iconResId = R.drawable.ic_error_placeholder;
            }
            binding.ivTransportIcon.setImageResource(iconResId);

            binding.executePendingBindings();
        }
    }
}