package com.brixo.controller;

import com.brixo.model.Cliente;
import com.brixo.repository.ClienteRepository;
import com.brixo.repository.ContratistaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.regex.Pattern;

@Controller
public class AuthController {

    private final ClienteRepository clienteRepo;
    private final ContratistaRepository contratistaRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(ClienteRepository clienteRepo,
                          ContratistaRepository contratistaRepo,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepo      = clienteRepo;
        this.contratistaRepo  = contratistaRepo;
        this.passwordEncoder  = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("error",  "Correo o contraseña incorrectos.");
        if (logout != null) model.addAttribute("logout", "Sesión cerrada correctamente.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nombre,
                           @RequestParam String correo,
                           @RequestParam String contrasena,
                           @RequestParam String confirmar,
                           @RequestParam String telefono,
                           @RequestParam String ciudad,
                           RedirectAttributes ra) {

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmar)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/register";
        }

        // Validar requisitos de contraseña
        String passwordError = validatePassword(contrasena);
        if (passwordError != null) {
            ra.addFlashAttribute("error", passwordError);
            return "redirect:/register";
        }

        if (clienteRepo.existsByCorreo(correo) || contratistaRepo.existsByCorreo(correo)) {
            ra.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/register";
        }

        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setCorreo(correo);
        c.setContrasena(passwordEncoder.encode(contrasena));
        c.setTelefono(telefono);
        c.setCiudad(ciudad);
        clienteRepo.save(c);

        ra.addFlashAttribute("success", "Cuenta creada. Inicia sesión.");
        return "redirect:/login";
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "La contraseña debe contener al menos una letra mayúscula.";
        }
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return "La contraseña debe contener al menos una letra minúscula.";
        }
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return "La contraseña debe contener al menos un número.";
        }
        return null;
    }
}
