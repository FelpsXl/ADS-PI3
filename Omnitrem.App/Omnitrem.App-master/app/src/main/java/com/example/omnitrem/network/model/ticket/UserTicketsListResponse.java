package com.example.omnitrem.network.model.ticket;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserTicketsListResponse {
    @SerializedName("tickets")
    private List<TicketResponse> tickets;

    public List<TicketResponse> getTickets() {
        return tickets;
    }
}
