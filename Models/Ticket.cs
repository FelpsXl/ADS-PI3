using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Omnitrem.Api.Enums;

namespace Omnitrem.Api.Models;

public class Ticket
{
    public int Id { get; set; }

    [Required]
    public int UserId { get; set; }

    [Required]
    public TransportType TransportType { get; set; }

    public DateTime PurchaseDate { get; set; } = DateTime.UtcNow;

    public string? QrCodeData { get; set; }

    public bool IsValid { get; set; } = true;

    [ForeignKey("UserId")]
    public User? User { get; set; }
}