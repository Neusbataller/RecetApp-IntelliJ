package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.dto.favorito.FavoritoToggleResponse;
import com.recetapp.recetas_pi.model.Favorito;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.repository.FavoritoRepository;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    public List<Favorito> getFavoritosByCorreo(String correo) {
        return favoritoRepository.findByUsuarioCorreoOrderByFechaAgregadoDesc(correo);
    }

    public boolean isFavorito(String correo, Long recetaId) {
        return favoritoRepository.existsByUsuarioCorreoAndRecetaId(correo, recetaId);
    }

    public long countByReceta(Long recetaId) {
        return favoritoRepository.countByRecetaId(recetaId);
    }

    @Transactional
    public Favorito addFavorito(String correo, Long recetaId) {
        if (favoritoRepository.existsByUsuarioCorreoAndRecetaId(correo, recetaId)) {
            throw new RuntimeException("La receta ya esta en favoritos");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setReceta(receta);

        return favoritoRepository.save(favorito);
    }

    @Transactional
    public void removeFavorito(String correo, Long recetaId) {
        Favorito favorito = favoritoRepository.findByUsuarioCorreoAndRecetaId(correo, recetaId)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        favoritoRepository.delete(favorito);
    }

    @Transactional
    public FavoritoToggleResponse toggleFavorito(String correo, Long recetaId) {
        FavoritoToggleResponse response = new FavoritoToggleResponse();

        if (favoritoRepository.existsByUsuarioCorreoAndRecetaId(correo, recetaId)) {
            removeFavorito(correo, recetaId);
            response.setFavorito(false);
            response.setMensaje("Favorito eliminado");
            return response;
        }

        addFavorito(correo, recetaId);
        response.setFavorito(true);
        response.setMensaje("Favorito agregado");
        return response;
    }
}