package com.example.omnitrem.network.model.ticket;

import com.google.gson.annotations.SerializedName;

public class SingleTicketApiResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("ticket")
    private TicketResponse ticket;

    public String getMessage() { return message; }
    public TicketResponse getTicket() { return ticket; }
}
