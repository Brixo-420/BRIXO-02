package com.brixo.security;

import com.brixo.model.Cliente;
import com.brixo.model.Contratista;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapta Cliente o Contratista al contrato UserDetails de Spring Security.
 */
public class BrixoUserDetails implements UserDetails {

    private final Integer id;
    private final String nombre;
    private final String correo;
    private final String contrasena;
    private final String rol;         // "CLIENTE" | "CONTRATISTA" | "ADMIN"
    private final String fotoPerfil;

    /** Construye desde un Cliente */
    public BrixoUserDetails(Cliente c) {
        this.id          = c.getId();
        this.nombre      = c.getNombre();
        this.correo      = c.getCorreo();
        this.contrasena  = c.getContrasena();
        this.rol         = "CLIENTE";
        this.fotoPerfil  = c.getFotoPerfil();
    }

    /** Construye desde un Contratista */
    public BrixoUserDetails(Contratista c) {
        this.id          = c.getId();
        this.nombre      = c.getNombre();
        this.correo      = c.getCorreo();
        this.contrasena  = c.getContrasena();
        this.rol         = "CONTRATISTA";
        this.fotoPerfil  = c.getFotoPerfil();
    }

    // ── Getters adicionales (accesibles desde Thymeleaf) ─────────────────────
    public Integer getId()        { return id; }
    public String  getNombre()    { return nombre; }
    public String  getRol()       { return rol; }
    public String  getFotoPerfil(){ return fotoPerfil; }

    // ── UserDetails ───────────────────────────────────────────────────────────
    @Override public String getUsername()  { return correo; }
    @Override public String getPassword()  { return contrasena; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return true; }
}
