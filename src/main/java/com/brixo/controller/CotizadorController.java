package com.brixo.controller;

import com.brixo.dto.CotizacionResult;
import com.brixo.security.BrixoUserDetails;
import com.brixo.service.CotizadorService;
import com.brixo.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/cotizador")
public class CotizadorController {

    private static final String SESSION_KEY = "ultima_cotizacion";

    private final CotizadorService cotizadorService;
    private final ObjectMapper objectMapper;

    public CotizadorController(CotizadorService cotizadorService, ObjectMapper objectMapper) {
        this.cotizadorService = cotizadorService;
        this.objectMapper     = objectMapper;
    }

    /** GET /cotizador – Muestra el formulario */
    @GetMapping
    public String index(Model model,
                        @AuthenticationPrincipal BrixoUserDetails user) {
        model.addAttribute("loggedIn", user != null);
        return "cotizador";
    }

    /**
     * POST /cotizador/generar – Llama al LLM y devuelve JSON (AJAX) o
     * redirige con resultados (fallback form clásico).
     */
    @PostMapping("/generar")
    public Object generar(
            @RequestParam @NotBlank @Size(min = 10, max = 2000) String descripcion,
            @RequestHeader(value = "X-Requested-With", required = false) String ajaxHeader,
            @AuthenticationPrincipal BrixoUserDetails user,
            HttpSession session,
            Model model) {

        LlmService.LlmResult resultado = cotizadorService.generar(descripcion);

        boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);

        if (!resultado.ok()) {
            if (isAjax) {
                return ResponseEntity.ok(Map.of("ok", false, "error", resultado.error()));
            }
            model.addAttribute("error", resultado.error());
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("loggedIn", user != null);
            return "cotizador";
        }

        // Guardar en sesión con timestamp
        session.setAttribute(SESSION_KEY, Map.of(
            "descripcion", descripcion,
            "data",        resultado.data(),
            "generada_en", LocalDateTime.now()
        ));

        if (isAjax) {
            return ResponseEntity.ok(Map.of("ok", true, "data", resultado.data()));
        }

        model.addAttribute("cotizacion", resultado.data());
        model.addAttribute("descripcion", descripcion);
        model.addAttribute("loggedIn", user != null);
        return "cotizador";
    }

    /**
     * POST /cotizador/confirmar – Persiste la cotización y pre-llena la solicitud.
     * Requiere usuario autenticado.
     */
    @PostMapping("/confirmar")
    public String confirmar(@AuthenticationPrincipal BrixoUserDetails user,
                            HttpSession session,
                            RedirectAttributes ra) {

        if (user == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> cot = (Map<String, Object>) session.getAttribute(SESSION_KEY);

        if (cot == null || cot.get("data") == null) {
            ra.addFlashAttribute("error", "No hay cotización para confirmar. Genera una primero.");
            return "redirect:/cotizador";
        }

        CotizacionResult data      = (CotizacionResult) cot.get("data");
        String           descripcion = (String) cot.get("descripcion");
        LocalDateTime    generadaEn  = (LocalDateTime) cot.get("generada_en");

        // Solo clientes pueden confirmar
        if (!"CLIENTE".equals(user.getRol())) {
            ra.addFlashAttribute("error", "Solo los clientes pueden confirmar cotizaciones.");
            return "redirect:/cotizador";
        }

        String desglose = cotizadorService.confirmar(user.getId(), descripcion, data, generadaEn);

        // Pre-llenar formulario de nueva solicitud
        session.setAttribute("prefill_solicitud", Map.of(
            "titulo",      data.getServicioPrincipal(),
            "descripcion", descripcion + "\n\n--- Desglose estimado (IA) ---\n" + desglose
        ));

        session.removeAttribute(SESSION_KEY);

        return "redirect:/solicitud/nueva";
    }
}
