using System.ComponentModel.DataAnnotations;
using Omnitrem.Api.Enums;

namespace Omnitrem.Api.Dtos;

public class CreateTicketDto
{
    [Required] public TransportType TransportType { get; set; }
}

public class TicketResponseDto
{
    public int Id { get; set; }
    public int UserId { get; set; }
    public string TransportType { get; set; } = string.Empty;
    public DateTime PurchaseDate { get; set; }
    public string? QrCodeData { get; set; }
    public bool IsValid { get; set; }
}