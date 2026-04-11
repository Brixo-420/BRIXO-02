package com.brixo.service;

import com.brixo.dto.CotizacionResult;
import com.brixo.model.CotizacionConfirmada;
import com.brixo.repository.CotizacionConfirmadaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Service
public class CotizadorService {

    private final LlmService llmService;
    private final CotizacionConfirmadaRepository repo;
    private final ObjectMapper objectMapper;

    public CotizadorService(LlmService llmService,
                            CotizacionConfirmadaRepository repo,
                            ObjectMapper objectMapper) {
        this.llmService   = llmService;
        this.repo         = repo;
        this.objectMapper = objectMapper;
    }

    /** Llama al LLM y devuelve el resultado. */
    public LlmService.LlmResult generar(String descripcion) {
        return llmService.generarCotizacion(descripcion);
    }

    /**
     * Persiste la cotización confirmada en BD y devuelve el texto de desglose
     * para pre-llenar el formulario de solicitud.
     */
    @Transactional
    public String confirmar(Integer idCliente,
                            String descripcion,
                            CotizacionResult data,
                            LocalDateTime generadaEn) {

        CotizacionConfirmada cc = new CotizacionConfirmada();
        cc.setIdCliente(idCliente);
        cc.setDescripcion(descripcion);
        cc.setServicioPrincipal(data.getServicioPrincipal());
        cc.setComplejidad(CotizacionConfirmada.Complejidad.valueOf(data.getComplejidad()));
        cc.setEstado(CotizacionConfirmada.Estado.pendiente);
        cc.setCreadoEn(generadaEn != null ? generadaEn : LocalDateTime.now());
        cc.setConfirmadoEn(LocalDateTime.now());

        try {
            cc.setMaterialesJson(objectMapper.writeValueAsString(data.getMateriales()));
            cc.setPersonalJson(objectMapper.writeValueAsString(data.getPersonal()));
        } catch (JsonProcessingException e) {
            cc.setMaterialesJson("[]");
            cc.setPersonalJson("[]");
        }

        repo.save(cc);

        return buildDesglose(data);
    }

    /** Genera el texto de desglose para la descripción de la solicitud. */
    public String buildDesglose(CotizacionResult data) {
        var sb = new StringJoiner("\n");

        if (data.getMateriales() != null && !data.getMateriales().isEmpty()) {
            sb.add("Materiales:");
            data.getMateriales().forEach(m ->
                sb.add("  • " + m.getNombre() + " — Cant: " + m.getCantidadEstimada()));
        }

        if (data.getPersonal() != null && !data.getPersonal().isEmpty()) {
            sb.add("Personal:");
            data.getPersonal().forEach(p ->
                sb.add("  • " + p.getRol() + " — " + p.getHorasEstimadas() + " hrs"));
        }

        String comp = data.getComplejidad();
        sb.add("Complejidad: " + (comp == null ? "medio" : (comp.substring(0, 1).toUpperCase() + comp.substring(1))));

        return sb.toString();
    }
}
