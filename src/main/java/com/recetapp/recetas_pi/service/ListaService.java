package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.model.Lista;
import com.recetapp.recetas_pi.model.ListaReceta;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.repository.ListaRecetaRepository;
import com.recetapp.recetas_pi.repository.ListaRepository;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListaService {

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private ListaRecetaRepository listaRecetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    public List<Lista> getMisListas(String correo) {
        return listaRepository.findByUsuarioCorreoOrderByFechaCreacionDesc(correo);
    }

    public Lista getMiListaById(String correo, Long listaId) {
        return listaRepository.findByIdAndUsuarioCorreo(listaId, correo)
                .orElseThrow(() -> new RuntimeException("Lista no encontrada"));
    }

    @Transactional
    public Lista crearLista(String correo, String nombre, String imagenUrl) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (listaRepository.existsByUsuarioCorreoAndNombreIgnoreCase(correo, nombreNormalizado)) {
            throw new RuntimeException("Ya tienes una lista con ese nombre");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Lista lista = new Lista();
        lista.setUsuario(usuario);
        lista.setNombre(nombreNormalizado);
        lista.setImagenUrl(imagenUrl);
        lista.setFechaCreacion(LocalDateTime.now());

        return listaRepository.save(lista);
    }

    @Transactional
    public Lista actualizarLista(String correo, Long listaId, String nombre, String imagenUrl) {
        Lista lista = getMiListaById(correo, listaId);

        if (nombre != null) {
            String nombreNormalizado = normalizarNombre(nombre);
            boolean nombreCambio = !nombreNormalizado.equalsIgnoreCase(lista.getNombre());
            if (nombreCambio && listaRepository.existsByUsuarioCorreoAndNombreIgnoreCase(correo, nombreNormalizado)) {
                throw new RuntimeException("Ya tienes una lista con ese nombre");
            }
            lista.setNombre(nombreNormalizado);
        }

        if (imagenUrl != null) {
            lista.setImagenUrl(imagenUrl);
        }

        return listaRepository.save(lista);
    }

    @Transactional
    public void eliminarLista(String correo, Long listaId) {
        Lista lista = getMiListaById(correo, listaId);
        listaRepository.delete(lista);
    }

    public List<ListaReceta> getRecetasDeLista(String correo, Long listaId) {
        // Validamos primero la propiedad de la lista para no filtrar datos de otro usuario.
        getMiListaById(correo, listaId);
        return listaRecetaRepository.findByListaIdOrderByFechaAgregadoDesc(listaId);
    }

    @Transactional
    public ListaReceta addRecetaALista(String correo, Long listaId, Long recetaId) {
        Lista lista = getMiListaById(correo, listaId);

        if (listaRecetaRepository.existsByListaIdAndRecetaId(listaId, recetaId)) {
            throw new RuntimeException("La receta ya esta en la lista");
        }

        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        ListaReceta listaReceta = new ListaReceta();
        listaReceta.setLista(lista);
        listaReceta.setReceta(receta);

        return listaRecetaRepository.save(listaReceta);
    }

    @Transactional
    public void removeRecetaDeLista(String correo, Long listaId, Long recetaId) {
        getMiListaById(correo, listaId);

        ListaReceta listaReceta = listaRecetaRepository.findByListaIdAndRecetaId(listaId, recetaId)
                .orElseThrow(() -> new RuntimeException("La receta no esta en la lista"));

        listaRecetaRepository.delete(listaReceta);
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null) {
            throw new RuntimeException("El nombre de la lista es obligatorio");
        }
        String trimmed = nombre.trim();
        if (trimmed.isBlank()) {
            throw new RuntimeException("El nombre de la lista es obligatorio");
        }
        return trimmed;
    }
}