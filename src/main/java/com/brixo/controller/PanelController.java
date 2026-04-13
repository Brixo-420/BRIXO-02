package com.brixo.controller;

import com.brixo.repository.CotizacionConfirmadaRepository;
import com.brixo.security.BrixoUserDetails;
import com.brixo.service.SolicitudService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PanelController {

    private final SolicitudService solicitudService;
    private final CotizacionConfirmadaRepository cotizacionRepo;

    public PanelController(SolicitudService solicitudService,
                           CotizacionConfirmadaRepository cotizacionRepo) {
        this.solicitudService = solicitudService;
        this.cotizacionRepo   = cotizacionRepo;
    }

    @GetMapping("/panel")
    public String panel(@AuthenticationPrincipal BrixoUserDetails user, Model model) {
        model.addAttribute("user", user);

        if ("CLIENTE".equals(user.getRol())) {
            model.addAttribute("solicitudes", solicitudService.listarPorCliente(user.getId()));
            model.addAttribute("cotizaciones", cotizacionRepo.findByIdClienteOrderByConfirmadoEnDesc(user.getId()));
            return "panel-cliente";
        }

        if ("CONTRATISTA".equals(user.getRol())) {
            model.addAttribute("solicitudesAbiertas", solicitudService.listarAbiertas());
            return "panel-contratista";
        }

        return "redirect:/admin/dashboard";
    }
}
