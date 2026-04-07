package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.model.Lista;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.repository.ListaRepository;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListaService {

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    public List<Lista> getListasByUsuario(Long usuarioId) {
        return listaRepository.findByUsuarioId(usuarioId);
    }

    public Lista crearLista(Long usuarioId, String nombre) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Lista lista = new Lista();
        lista.setUsuario(usuario);
        lista.setNombre(nombre);
        lista.setFechaCreacion(LocalDateTime.now());

        return listaRepository.save(lista);
    }

    public Lista addRecetaALista(Long listaId, Long recetaId) {
        Lista lista = listaRepository.findById(listaId)
            .orElseThrow(() -> new RuntimeException("Lista no encontrada"));

        Receta receta = recetaRepository.findById(recetaId)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        lista.getRecetas().add(receta);
        return listaRepository.save(lista);
    }

    public Lista removeRecetaDeLista(Long listaId, Long recetaId) {
        Lista lista = listaRepository.findById(listaId)
            .orElseThrow(() -> new RuntimeException("Lista no encontrada"));

        lista.getRecetas().removeIf(r -> r.getId().equals(recetaId));
        return listaRepository.save(lista);
    }
}