package com.example.omnitrem.network.model.ticket;

import com.google.gson.annotations.SerializedName;

public class CreateTicketBody {
    @SerializedName("TransportType")
    private String transportType;

    public CreateTicketBody(String transportType) {
        this.transportType = transportType;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}