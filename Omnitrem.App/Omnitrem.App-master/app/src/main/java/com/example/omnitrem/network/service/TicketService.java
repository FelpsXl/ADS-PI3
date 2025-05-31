package com.example.omnitrem.network.service;

import com.example.omnitrem.network.model.ticket.CreateTicketBody;
import com.example.omnitrem.network.model.ticket.SingleTicketApiResponse;
import com.example.omnitrem.network.model.ticket.UserTicketsListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TicketService {

    @POST("tickets")
    Call<SingleTicketApiResponse> createTicket( @Body CreateTicketBody body );

    @GET("tickets")
    Call<UserTicketsListResponse> getUserTickets();

    @GET("tickets/{id}")
    Call<SingleTicketApiResponse> getTicketById(@Path("id") int ticketId );
}