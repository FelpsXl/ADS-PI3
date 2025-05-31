package com.example.omnitrem.network.model.ticket;

import com.google.gson.annotations.SerializedName;

public class TicketResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("transportType")
    private String transportType;

    @SerializedName("purchaseDate")
    private String purchaseDate;

    @SerializedName("qrCodeData")
    private String qrCodeData;

    @SerializedName("isValid")
    private boolean isValid;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTransportType() { return transportType; }
    public String getPurchaseDate() { return purchaseDate; }
    public String getQrCodeData() { return qrCodeData; }
    public boolean isValid() { return isValid; }
}
