package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.dto.valoracion.ValoracionStatsResponse;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.model.Valoracion;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import com.recetapp.recetas_pi.repository.ValoracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    public List<Valoracion> getByReceta(Long recetaId) {
        assertRecetaExiste(recetaId);
        return valoracionRepository.findByRecetaIdOrderByFechaCreacionDesc(recetaId);
    }

    public ValoracionStatsResponse getStats(Long recetaId) {
        assertRecetaExiste(recetaId);

        ValoracionStatsResponse stats = new ValoracionStatsResponse();
        stats.setRecetaId(recetaId);
        stats.setTotalValoraciones(valoracionRepository.countByRecetaId(recetaId));
        stats.setMediaPuntuacion(valoracionRepository.averageByRecetaId(recetaId));
        return stats;
    }

    public Optional<Valoracion> getMine(String correo, Long recetaId) {
        assertRecetaExiste(recetaId);
        return valoracionRepository.findByUsuarioCorreoAndRecetaId(correo, recetaId);
    }

    @Transactional
    public Valoracion create(String correo, Long recetaId, Integer puntuacion, String comentario) {
        if (valoracionRepository.existsByUsuarioCorreoAndRecetaId(correo, recetaId)) {
            throw new RuntimeException("Ya has valorado esta receta");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        Valoracion valoracion = new Valoracion();
        valoracion.setUsuario(usuario);
        valoracion.setReceta(receta);
        valoracion.setPuntuacion(puntuacion);
        valoracion.setComentario(comentario);

        return valoracionRepository.save(valoracion);
    }

    @Transactional
    public Valoracion updateMine(String correo, Long recetaId, Integer puntuacion, String comentario) {
        Valoracion valoracion = valoracionRepository.findByUsuarioCorreoAndRecetaId(correo, recetaId)
                .orElseThrow(() -> new RuntimeException("Valoracion no encontrada"));

        valoracion.setPuntuacion(puntuacion);
        valoracion.setComentario(comentario);

        return valoracionRepository.save(valoracion);
    }

    @Transactional
    public void deleteMine(String correo, Long recetaId) {
        Valoracion valoracion = valoracionRepository.findByUsuarioCorreoAndRecetaId(correo, recetaId)
                .orElseThrow(() -> new RuntimeException("Valoracion no encontrada"));

        valoracionRepository.delete(valoracion);
    }

    private void assertRecetaExiste(Long recetaId) {
        if (!recetaRepository.existsById(recetaId)) {
            throw new RuntimeException("Receta no encontrada");
        }
    }
}
