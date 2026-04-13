package com.brixo.controller;

import com.brixo.repository.CategoriaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CategoriaRepository categoriaRepo;

    public HomeController(CategoriaRepository categoriaRepo) {
        this.categoriaRepo = categoriaRepo;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "index";
    }

    @GetMapping("/sobre-nosotros")
    public String sobreNosotros() { return "info/sobre-nosotros"; }

    @GetMapping("/como-funciona")
    public String comoFunciona() { return "info/como-funciona"; }

    @GetMapping("/ayuda")
    public String ayuda() { return "info/ayuda"; }

    @GetMapping("/seguridad")
    public String seguridad() { return "info/seguridad"; }

    @GetMapping("/politica-cookies")
    public String politicaCookies() { return "info/politica-cookies"; }
}
