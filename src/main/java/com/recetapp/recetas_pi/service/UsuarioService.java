package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.model.Alergia;
import com.recetapp.recetas_pi.repository.AlergiaRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Locale;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlergiaRepository alergiaRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<Usuario> login(String correo, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            return usuario;
        }
        return Optional.empty();
    }

    @Transactional
    public Usuario registro(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        // Hash password before saving
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Register a user and associate allergies by name.
     * - If an allergy name doesn't exist yet, it will be created.
        */
    @Transactional
    public Usuario registroConAlergias(Usuario usuario, List<String> alergiasNombres) {
        if (alergiasNombres != null && !alergiasNombres.isEmpty()) {
            List<Alergia> alergias = new ArrayList<>();
            for (String nombre : alergiasNombres) {
                if (nombre == null || nombre.isBlank()) continue;
                Alergia alergia = alergiaRepository.findByNombre(nombre.trim())
                        .orElseGet(() -> {
                            Alergia a = new Alergia();
                            a.setNombre(nombre.trim());
                            return alergiaRepository.save(a);
                        });
                alergias.add(alergia);
            }
            usuario.setAlergias(alergias);
        }

        return registro(usuario);
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional
    public Usuario actualizarPerfilPorCorreo(String correoActual, String nombre, String apellidos, String nuevoCorreo) {
        Usuario usuario = usuarioRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String correoNormalizado = nuevoCorreo.trim();
        if (!usuario.getCorreo().equalsIgnoreCase(correoNormalizado)) {
            Optional<Usuario> existente = usuarioRepository.findByCorreo(correoNormalizado);
            if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
                throw new RuntimeException("El correo ya está registrado");
            }
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellidos(apellidos.trim());
        usuario.setCorreo(correoNormalizado);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarAlergiasPorCorreo(String correo, List<String> alergiasNombres) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Alergia> alergias = new ArrayList<>();
        if (alergiasNombres != null) {
            for (String nombre : alergiasNombres) {
                if (nombre == null || nombre.isBlank()) {
                    continue;
                }
                String limpio = nombre.trim();
                Alergia alergia = alergiaRepository.findByNombre(limpio)
                        .orElseGet(() -> {
                            Alergia a = new Alergia();
                            a.setNombre(limpio);
                            return alergiaRepository.save(a);
                        });

                boolean yaIncluida = alergias.stream().anyMatch(a -> a.getId().equals(alergia.getId()));
                if (!yaIncluida) {
                    alergias.add(alergia);
                }
            }
        }

        usuario.setAlergias(alergias);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario agregarAlergiasPorCorreo(String correo, List<String> alergiasNombres) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<Long, Alergia> porId = new LinkedHashMap<>();
        if (usuario.getAlergias() != null) {
            for (Alergia a : usuario.getAlergias()) {
                porId.put(a.getId(), a);
            }
        }

        if (alergiasNombres != null) {
            for (String nombre : alergiasNombres) {
                if (nombre == null || nombre.isBlank()) {
                    continue;
                }
                String limpio = nombre.trim();
                Alergia alergia = alergiaRepository.findByNombre(limpio)
                        .orElseGet(() -> {
                            Alergia a = new Alergia();
                            a.setNombre(limpio);
                            return alergiaRepository.save(a);
                        });
                porId.put(alergia.getId(), alergia);
            }
        }

        usuario.setAlergias(new ArrayList<>(porId.values()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario eliminarAlergiaPorCorreo(String correo, String nombreAlergia) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nombreAlergia == null || nombreAlergia.isBlank()) {
            throw new RuntimeException("Nombre de alergia inválido");
        }

        String objetivo = nombreAlergia.trim().toLowerCase(Locale.ROOT);
        List<Alergia> actuales = usuario.getAlergias() != null ? new ArrayList<>(usuario.getAlergias()) : new ArrayList<>();
        boolean removed = actuales.removeIf(a -> a.getNombre() != null && a.getNombre().trim().toLowerCase(Locale.ROOT).equals(objetivo));

        if (!removed) {
            throw new RuntimeException("La alergia no está asociada al usuario");
        }

        usuario.setAlergias(actuales);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void actualizarPasswordPorCorreo(String correo, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }
}