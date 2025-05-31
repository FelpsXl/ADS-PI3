using Microsoft.AspNetCore.Mvc;
using Omnitrem.Api.Dtos;
using Omnitrem.Api.Service;

namespace Omnitrem.Api.Controllers;

[ApiController]
[Route("[controller]")]
public class AuthController(IAuthService authService, ILogger<AuthController> logger)
    : ControllerBase
{
    [HttpPost("register")]
    public async Task<IActionResult> Register(UserRegisterDto registerDto)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var (succeeded, errorMessage, user) = await authService.RegisterAsync(registerDto);

        if (!succeeded)
        {
            logger.LogWarning("Registration failed: {ErrorMessage}", errorMessage);
            if (errorMessage != null && errorMessage.Contains("already registered"))
                return Conflict(new RegisterResponseDto { Message = errorMessage }); // 409
            return BadRequest(new RegisterResponseDto { Message = errorMessage ?? "Registration failed." });
        }

        logger.LogInformation("User registered: {Email}", user?.Email);
        return StatusCode(201, new RegisterResponseDto { Message = "User registered successfully!" });
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login(UserLoginDto loginDto)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var (succeeded, errorMessage, token, user) = await authService.LoginAsync(loginDto);

        if (!succeeded)
        {
            logger.LogWarning("Login failed: {ErrorMessage}", errorMessage);
            return Unauthorized(new LoginResponseDto { Message = errorMessage ?? "Login failed." });
        }

        logger.LogInformation("User logged in: {Email}", user?.Email);
        return Ok(new LoginResponseDto
        {
            Message = "Login successful!",
            Token = token
        });
    }
}