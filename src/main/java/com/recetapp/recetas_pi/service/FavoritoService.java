package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.model.Favorito;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.repository.FavoritoRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    public List<Favorito> getFavoritosByUsuario(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    public Favorito addFavorito(Long usuarioId, Long recetaId) {
        if (favoritoRepository.existsByUsuarioIdAndRecetaId(usuarioId, recetaId)) {
            throw new RuntimeException("La receta ya está en favoritos");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Receta receta = recetaRepository.findById(recetaId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setReceta(receta);
        favorito.setFechaAgregado(LocalDateTime.now());

        return favoritoRepository.save(favorito);
    }

    public void removeFavorito(Long usuarioId, Long recetaId) {
        Favorito favorito = favoritoRepository.findByUsuarioIdAndRecetaId(usuarioId, recetaId)
            .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        favoritoRepository.delete(favorito);
    }
}