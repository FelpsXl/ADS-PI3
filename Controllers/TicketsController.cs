using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Omnitrem.Api.Data;
using Omnitrem.Api.Dtos;
using Omnitrem.Api.Models;

namespace Omnitrem.Api.Controllers;

[ApiController]
[Route("[controller]")]
[Authorize]
public class TicketsController(AppDbContext context, ILogger<TicketsController> logger) : ControllerBase
{
    [HttpPost] // POST /tickets
    public async Task<IActionResult> CreateTicket(CreateTicketDto createTicketDto)
    {
        if (!ModelState.IsValid) return BadRequest(ModelState);

        try
        {
            var userId = GetCurrentUserId();

            var userExists = await context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists) return Unauthorized(new { Message = "User not found." });

            var qrCodeData =
                $"ticket-{userId}-{createTicketDto.TransportType.ToString().ToLower()}-{DateTime.UtcNow.Ticks}";

            var ticket = new Ticket
            {
                UserId = userId,
                TransportType = createTicketDto.TransportType,
                PurchaseDate = DateTime.UtcNow,
                QrCodeData = qrCodeData,
                IsValid = true
            };

            context.Tickets.Add(ticket);
            await context.SaveChangesAsync();

            logger.LogInformation("Ticket created for user {UserId}, type {TransportType}", userId,
                ticket.TransportType);

            var ticketResponse = new TicketResponseDto
            {
                Id = ticket.Id,
                UserId = ticket.UserId,
                TransportType = ticket.TransportType.ToString(),
                PurchaseDate = ticket.PurchaseDate,
                QrCodeData = ticket.QrCodeData,
                IsValid = ticket.IsValid
            };

            return CreatedAtAction(nameof(GetTicketById), new { id = ticket.Id },
                new { Message = "Ticket created successfully", Ticket = ticketResponse });
        }
        catch (InvalidOperationException ex)
        {
            logger.LogWarning(ex.Message);
            return Unauthorized(new { ex.Message });
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error creating ticket.");
            return StatusCode(500, new { Message = "An error occurred while creating the ticket." });
        }
    }

    [HttpGet] // GET /tickets
    public async Task<IActionResult> GetUserTickets()
    {
        try
        {
            var userId = GetCurrentUserId();
            var tickets = await context.Tickets
                .Where(t => t.UserId == userId)
                .OrderByDescending(t => t.PurchaseDate)
                .Select(t => new TicketResponseDto 
                {
                    Id = t.Id,
                    UserId = t.UserId,
                    TransportType = t.TransportType.ToString(),
                    PurchaseDate = t.PurchaseDate,
                    QrCodeData = t.QrCodeData,
                    IsValid = t.IsValid
                })
                .ToListAsync();

            return Ok(new { Tickets = tickets });
        }
        catch (InvalidOperationException ex)
        {
            logger.LogWarning(ex.Message);
            return Unauthorized(new { ex.Message });
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error fetching user tickets.");
            return StatusCode(500, new { Message = "An error occurred while fetching tickets." });
        }
    }

    [HttpGet("{id}")] // GET /tickets/{id}
    public async Task<IActionResult> GetTicketById(int id)
    {
        try
        {
            var userId = GetCurrentUserId();
            var ticket = await context.Tickets
                .Where(t => t.Id == id && t.UserId == userId)
                .Select(t => new TicketResponseDto
                {
                    Id = t.Id,
                    UserId = t.UserId,
                    TransportType = t.TransportType.ToString(),
                    PurchaseDate = t.PurchaseDate,
                    QrCodeData = t.QrCodeData,
                    IsValid = t.IsValid
                })
                .FirstOrDefaultAsync();

            if (ticket == null) return NotFound(new { Message = "Ticket not found or access denied." });

            return Ok(new { Ticket = ticket });
        }
        catch (InvalidOperationException ex)
        {
            logger.LogWarning(ex.Message);
            return Unauthorized(new { ex.Message });
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error fetching ticket by ID.");
            return StatusCode(500, new { Message = "An error occurred while fetching the ticket." });
        }
    }
    
    private int GetCurrentUserId()
    {
        var userIdClaim = User.FindFirstValue(ClaimTypes.NameIdentifier);
        if (string.IsNullOrEmpty(userIdClaim) || !int.TryParse(userIdClaim, out var userId))
            throw new InvalidOperationException("User ID not found or invalid in token.");
        return userId;
    }
}