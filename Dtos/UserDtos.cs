using System.ComponentModel.DataAnnotations;

namespace Omnitrem.Api.Dtos;

public class UserRegisterDto
{
    [Required]
    [StringLength(100, MinimumLength = 2)]
    public string Name { get; set; } = string.Empty;

    [Required]
    [EmailAddress] 
    public string Email { get; set; } = string.Empty;

    [Required]
    [StringLength(100, MinimumLength = 6)]
    public string Password { get; set; } = string.Empty;
}

public class UserLoginDto
{
    [Required]
    [EmailAddress] 
    public string Email { get; set; } = string.Empty;

    [Required] public string Password { get; set; } = string.Empty;
}

public class LoginResponseDto
{
    public string Message { get; set; } = string.Empty;
    public string Token { get; set; } = string.Empty;
}

public class RegisterResponseDto
{
    public string Message { get; set; } = string.Empty;
}

public class ChangePasswordDto
{
    [Required]
    public string OldPassword { get; set; } = string.Empty;

    [Required]
    [StringLength(100, MinimumLength = 6, ErrorMessage = "A nova senha deve ter no mínimo 6 caracteres.")]
    public string NewPassword { get; set; } = string.Empty;

    [Required]
    [Compare("NewPassword", ErrorMessage = "A nova senha e a confirmação não coincidem.")]
    public string ConfirmNewPassword { get; set; } = string.Empty;
}