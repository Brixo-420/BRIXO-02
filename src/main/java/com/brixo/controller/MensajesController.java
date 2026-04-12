package com.brixo.controller;

import com.brixo.model.Mensaje;
import com.brixo.repository.MensajeRepository;
import com.brixo.security.BrixoUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mensajes")
public class MensajesController {

    private final MensajeRepository mensajeRepo;

    public MensajesController(MensajeRepository mensajeRepo) {
        this.mensajeRepo = mensajeRepo;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal BrixoUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "mensajes/index";
    }

    @GetMapping("/chat/{otherId}/{otherRol}")
    @Transactional
    public String chat(@PathVariable Integer otherId,
                       @PathVariable String otherRol,
                       @AuthenticationPrincipal BrixoUserDetails user,
                       Model model) {

        List<Mensaje> mensajes = mensajeRepo.findConversacion(
            user.getId(), user.getRol(), otherId, otherRol.toUpperCase()
        );
        mensajeRepo.marcarComoLeidos(
            user.getId(), user.getRol(), otherId, otherRol.toUpperCase()
        );

        model.addAttribute("mensajes",  mensajes);
        model.addAttribute("otherId",   otherId);
        model.addAttribute("otherRol",  otherRol);
        model.addAttribute("user",      user);
        return "mensajes/chat";
    }

    @PostMapping("/enviar")
    @Transactional
    public String enviar(@RequestParam Integer destinatarioId,
                         @RequestParam String destinatarioRol,
                         @RequestParam String contenido,
                         @AuthenticationPrincipal BrixoUserDetails user) {

        Mensaje m = new Mensaje();
        m.setRemitenteId(user.getId());
        m.setRemitenteRol(user.getRol());
        m.setDestinatarioId(destinatarioId);
        m.setDestinatarioRol(destinatarioRol.toUpperCase());
        m.setContenido(contenido.trim());
        mensajeRepo.save(m);

        return "redirect:/mensajes/chat/" + destinatarioId + "/" + destinatarioRol;
    }
}
