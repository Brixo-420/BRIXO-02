package com.brixo.controller;

import com.brixo.model.EstadoServicio;
import com.brixo.model.Servicio;
import com.brixo.service.ServicioService;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String emailCliente,
            Authentication authentication,
            Model model
    ) {
        boolean esAdmin = tieneRol(authentication, "ROLE_ADMIN");
        boolean esCliente = tieneRol(authentication, "ROLE_CLIENTE");

        model.addAttribute("servicios", servicioService.listarFiltrados(titulo, estado, emailCliente, authentication.getName(), esAdmin, esCliente));
        model.addAttribute("estados", EstadoServicio.values());
        model.addAttribute("titulo", titulo);
        model.addAttribute("estado", estado);
        model.addAttribute("emailCliente", emailCliente);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("esCliente", esCliente);
        return "servicios/lista";
    }

    @GetMapping(value = "/reporte.csv", produces = "text/csv")
    @ResponseBody
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String emailCliente,
            Authentication authentication
    ) {
        boolean esAdmin = tieneRol(authentication, "ROLE_ADMIN");
        boolean esCliente = tieneRol(authentication, "ROLE_CLIENTE");

        List<Servicio> servicios = servicioService.listarFiltrados(titulo, estado, emailCliente, authentication.getName(), esAdmin, esCliente);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder csv = new StringBuilder();
        csv.append("id,titulo,descripcion,ubicacion,presupuesto,estado,clienteEmail,fechaCreacion\n");

        for (Servicio servicio : servicios) {
            csv.append(servicio.getId()).append(",")
                .append(escaparCsv(servicio.getTitulo())).append(",")
                .append(escaparCsv(servicio.getDescripcion())).append(",")
                .append(escaparCsv(servicio.getUbicacion())).append(",")
                .append(servicio.getPresupuesto()).append(",")
                .append(servicio.getEstado()).append(",")
                .append(escaparCsv(servicio.getCliente() != null ? servicio.getCliente().getEmail() : "")).append(",")
                .append(servicio.getFechaCreacion() != null ? servicio.getFechaCreacion().format(formatter) : "")
                .append("\n");
        }

        byte[] body = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=servicios-reporte.csv")
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
            .body(body);
    }

    @PostMapping("/importar-excel")
    public String importarExcel(@RequestParam("archivo") MultipartFile archivo, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (!puedeEditar(authentication)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos.");
            return "redirect:/servicios";
        }

        if (archivo == null || archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Archivo vacío.");
            return "redirect:/servicios";
        }

        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || filaVacia(row, formatter)) continue;

                Servicio s = new Servicio();
                s.setTitulo(valorTexto(row.getCell(0), formatter));
                s.setDescripcion(valorTexto(row.getCell(1), formatter));
                s.setUbicacion(valorTexto(row.getCell(2), formatter));
                
                String p = valorTexto(row.getCell(3), formatter).replace(",", ".");
                s.setPresupuesto(p.isEmpty() ? BigDecimal.ZERO : new BigDecimal(p));

                String est = valorTexto(row.getCell(4), formatter).toUpperCase();
                s.setEstado(est.isEmpty() ? EstadoServicio.ABIERTO : EstadoServicio.valueOf(est));

                servicioService.crear(s, authentication.getName());
            }
            redirectAttributes.addFlashAttribute("ok", "Importación exitosa");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error en Excel: " + e.getMessage());
        }
        return "redirect:/servicios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            servicioService.eliminar(id, authentication.getName(), tieneRol(authentication, "ROLE_ADMIN"));
            redirectAttributes.addFlashAttribute("ok", "Eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/servicios";
    }

    // Métodos Auxiliares
    private boolean puedeEditar(Authentication auth) {
        return tieneRol(auth, "ROLE_ADMIN") || tieneRol(auth, "ROLE_CLIENTE");
    }

    private boolean tieneRol(Authentication auth, String rol) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
    }

    private String escaparCsv(String v) {
        return v == null ? "\"\"" : "\"" + v.replace("\"", "\"\"") + "\"";
    }

    private boolean filaVacia(Row row, DataFormatter f) {
        return valorTexto(row.getCell(0), f).isBlank() && valorTexto(row.getCell(1), f).isBlank();
    }

    private String valorTexto(Cell c, DataFormatter f) {
        return c == null ? "" : f.formatCellValue(c).trim();
    }
}