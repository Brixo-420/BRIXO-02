package com.brixo.controller;

import com.brixo.repository.CategoriaRepository;
import com.brixo.repository.ContratistaRepository;
import com.brixo.repository.ServicioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    private final CategoriaRepository categoriaRepo;
    private final ContratistaRepository contratistaRepo;
    private final ServicioRepository servicioRepo;

    public HomeController(CategoriaRepository categoriaRepo,
                          ContratistaRepository contratistaRepo,
                          ServicioRepository servicioRepo) {
        this.categoriaRepo = categoriaRepo;
        this.contratistaRepo = contratistaRepo;
        this.servicioRepo = servicioRepo;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "index";
    }

    @GetMapping("/map")
    public String map(Model model) {
        model.addAttribute("contratistas", contratistaRepo.findAll());
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "map";
    }

    @GetMapping("/especialidades")
    public String especialidades(Model model) {
        model.addAttribute("categorias", categoriaRepo.findAll());
        model.addAttribute("servicios", servicioRepo.findAll());
        model.addAttribute("categoriaSeleccionada", null);
        return "especialidades";
    }

    @GetMapping("/especialidades/categoria/{id}")
    public String especialidadesPorCategoria(@PathVariable Integer id, Model model) {
        model.addAttribute("categorias", categoriaRepo.findAll());
        model.addAttribute("servicios", servicioRepo.findByCategoriaId(id));
        model.addAttribute("categoriaSeleccionada", categoriaRepo.findById(id).orElse(null));
        return "especialidades";
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
