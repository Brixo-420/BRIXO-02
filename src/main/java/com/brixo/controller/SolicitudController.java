package com.brixo.controller;

import com.brixo.security.BrixoUserDetails;
import com.brixo.service.SolicitudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/solicitud")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    /** GET /solicitud/nueva – Formulario (pre-llenado si viene del cotizador) */
    @GetMapping("/nueva")
    public String nueva(@AuthenticationPrincipal BrixoUserDetails user,
                        HttpSession session,
                        Model model) {

        if (!"CLIENTE".equals(user.getRol())) {
            return "redirect:/panel";
        }

        @SuppressWarnings("unchecked")
        Map<String, String> prefill = (Map<String, String>) session.getAttribute("prefill_solicitud");
        if (prefill != null) {
            model.addAttribute("prefillTitulo",      prefill.get("titulo"));
            model.addAttribute("prefillDescripcion", prefill.get("descripcion"));
            session.removeAttribute("prefill_solicitud");
        }

        return "solicitud/nueva";
    }

    /** POST /solicitud/guardar */
    @PostMapping("/guardar")
    public String guardar(@AuthenticationPrincipal BrixoUserDetails user,
                          @RequestParam String titulo,
                          @RequestParam String descripcion,
                          @RequestParam(required = false) BigDecimal presupuesto,
                          @RequestParam(required = false) String ubicacion,
                          RedirectAttributes ra) {

        if (!"CLIENTE".equals(user.getRol())) {
            return "redirect:/panel";
        }

        solicitudService.crear(user.getId(), titulo, descripcion, presupuesto, ubicacion);
        ra.addFlashAttribute("success", "Solicitud creada correctamente.");
        return "redirect:/panel";
    }

    /** POST /solicitud/eliminar/{id} */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           @AuthenticationPrincipal BrixoUserDetails user,
                           RedirectAttributes ra) {

        solicitudService.findById(id).ifPresent(s -> {
            if (s.getCliente().getId().equals(user.getId())) {
                solicitudService.eliminar(id);
            }
        });

        ra.addFlashAttribute("success", "Solicitud eliminada.");
        return "redirect:/panel";
    }
}
