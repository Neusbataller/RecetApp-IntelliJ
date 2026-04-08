package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.dto.receta.RecetaIngredienteRequest;
import com.recetapp.recetas_pi.model.Ingrediente;
import com.recetapp.recetas_pi.model.IngredienteReceta;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.repository.IngredienteRepository;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    public List<Receta> getAllRecetas() {
        return recetaRepository.findAll();
    }

    public Optional<Receta> getRecetaById(Long id) {
        return recetaRepository.findById(id);
    }

    public List<Receta> getRecetasByCategoria(Long categoriaId) {
        return recetaRepository.findByCategoriaId(categoriaId);
    }

    public List<Receta> getRecetasByUsuarioCorreo(String correo) {
        String limpio = normalizeText(correo);
        if (limpio == null) {
            return new ArrayList<>();
        }
        return recetaRepository.findByUsuarioCorreoOrderByFechaCreacionDesc(limpio);
    }

    public List<Receta> buscarRecetas(String titulo) {
        String limpio = titulo == null ? "" : titulo.trim();
        if (limpio.isBlank()) {
            return recetaRepository.findAll();
        }
        return recetaRepository.findByTituloContainingIgnoreCase(limpio);
    }

    public List<Receta> buscarPorDificultad(String dificultad) {
        String limpio = normalizeText(dificultad);
        if (limpio == null) {
            return recetaRepository.findAll();
        }
        return recetaRepository.findByDificultadIgnoreCase(limpio);
    }

    public List<Receta> buscarPorTiempoMax(Integer maxTiempo) {
        if (maxTiempo == null || maxTiempo < 0) {
            return recetaRepository.findAll();
        }
        return recetaRepository.findByTiempoTotalMax(maxTiempo);
    }

    public List<Receta> buscarAvanzado(
            String titulo,
            String dificultad,
            String tag,
            Integer maxTiempo,
            List<String> ingredientes,
            boolean matchAll
    ) {
        List<Receta> base = recetaRepository.findAll();

        String tituloLimpio = normalizeText(titulo);
        if (tituloLimpio != null) {
            String t = tituloLimpio.toLowerCase(Locale.ROOT);
            base = base.stream()
                    .filter(r -> r.getTitulo() != null && r.getTitulo().toLowerCase(Locale.ROOT).contains(t))
                    .collect(Collectors.toList());
        }

        String dificultadLimpia = normalizeText(dificultad);
        if (dificultadLimpia != null) {
            String d = dificultadLimpia.toLowerCase(Locale.ROOT);
            base = base.stream()
                    .filter(r -> r.getDificultad() != null && r.getDificultad().toLowerCase(Locale.ROOT).equals(d))
                    .collect(Collectors.toList());
        }

        String tagLimpio = normalizeText(tag);
        if (tagLimpio != null) {
            String tg = tagLimpio.toLowerCase(Locale.ROOT);
            base = base.stream()
                    .filter(r -> r.getCategorias() != null && r.getCategorias().stream()
                            .anyMatch(c -> c.getNombre() != null && c.getNombre().toLowerCase(Locale.ROOT).contains(tg)))
                    .collect(Collectors.toList());
        }

        if (maxTiempo != null && maxTiempo >= 0) {
            base = base.stream()
                    .filter(r -> (safeTime(r.getTiempoPreparacionMin()) + safeTime(r.getTiempoCoccionMin())) <= maxTiempo)
                    .collect(Collectors.toList());
        }

        List<String> ingredientesLimpios = normalizeIngredientNames(ingredientes);
        if (!ingredientesLimpios.isEmpty()) {
            base = base.stream()
                    .filter(r -> recetaCumpleIngredientes(r, ingredientesLimpios, matchAll))
                    .collect(Collectors.toList());
        }

        return base;
    }

    public List<Receta> buscarPorIngrediente(String ingrediente) {
        String limpio = normalizeText(ingrediente);
        if (limpio == null) {
            return recetaRepository.findAll();
        }
        return recetaRepository.findByIngredienteNombre(limpio.toLowerCase(Locale.ROOT));
    }

    public List<Receta> buscarPorIngredientes(List<String> ingredientes, boolean matchAll) {
        List<String> limpios = normalizeIngredientNames(ingredientes);
        if (limpios.isEmpty()) {
            return recetaRepository.findAll();
        }

        if (matchAll) {
            return recetaRepository.findByAllIngredienteNombres(limpios, limpios.size());
        }
        return recetaRepository.findByAnyIngredienteNombres(limpios);
    }

    public List<Receta> buscarPorTag(String tag) {
        String limpio = normalizeText(tag);
        if (limpio == null) {
            return recetaRepository.findAll();
        }
        return recetaRepository.findByTag(limpio);
    }

    @Transactional
    public Receta create(
            Long usuarioId,
            String titulo,
            String descripcion,
            String preparacion,
            Integer tiempoPreparacionMin,
            Integer tiempoCoccionMin,
            Integer porciones,
            String dificultad,
            String imagenUrl,
            List<RecetaIngredienteRequest> ingredientes
    ) {
        Receta receta = new Receta();
        applyData(
                receta,
                usuarioId,
                titulo,
                descripcion,
                preparacion,
                tiempoPreparacionMin,
                tiempoCoccionMin,
                porciones,
                dificultad,
                imagenUrl,
                ingredientes,
                false
        );
        return recetaRepository.save(receta);
    }

    @Transactional
    public Receta update(
            Long id,
            Long usuarioId,
            String titulo,
            String descripcion,
            String preparacion,
            Integer tiempoPreparacionMin,
            Integer tiempoCoccionMin,
            Integer porciones,
            String dificultad,
            String imagenUrl,
            List<RecetaIngredienteRequest> ingredientes
    ) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        applyData(
                receta,
                usuarioId,
                titulo,
                descripcion,
                preparacion,
                tiempoPreparacionMin,
                tiempoCoccionMin,
                porciones,
                dificultad,
                imagenUrl,
                ingredientes,
                true
        );
        return recetaRepository.save(receta);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!recetaRepository.existsById(id)) {
            throw new RuntimeException("Receta no encontrada");
        }
        recetaRepository.deleteById(id);
    }

    private void applyData(
            Receta receta,
            Long usuarioId,
            String titulo,
            String descripcion,
            String preparacion,
            Integer tiempoPreparacionMin,
            Integer tiempoCoccionMin,
            Integer porciones,
            String dificultad,
            String imagenUrl,
            List<RecetaIngredienteRequest> ingredientes,
            boolean keepIngredientesIfNull
    ) {
        String tituloLimpio = titulo == null ? "" : titulo.trim();
        if (tituloLimpio.isBlank()) {
            throw new RuntimeException("El titulo es obligatorio");
        }

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        receta.setUsuario(usuario);
        receta.setTitulo(tituloLimpio);
        receta.setDescripcion(normalizeText(descripcion));
        receta.setPreparacion(normalizeText(preparacion));
        receta.setTiempoPreparacionMin(tiempoPreparacionMin);
        receta.setTiempoCoccionMin(tiempoCoccionMin);
        receta.setPorciones(porciones);
        receta.setDificultad(normalizeText(dificultad));
        receta.setImagenUrl(normalizeText(imagenUrl));

        if (ingredientes != null) {
            receta.setIngredientesReceta(buildIngredientesReceta(receta, ingredientes));
        } else if (!keepIngredientesIfNull) {
            receta.setIngredientesReceta(new ArrayList<>());
        }
    }

    private List<IngredienteReceta> buildIngredientesReceta(Receta receta, List<RecetaIngredienteRequest> ingredientesReq) {
        Map<String, RecetaIngredienteRequest> porNombre = new LinkedHashMap<>();
        if (ingredientesReq != null) {
            for (RecetaIngredienteRequest req : ingredientesReq) {
                if (req == null || req.getNombre() == null || req.getNombre().isBlank()) {
                    continue;
                }
                porNombre.put(req.getNombre().trim().toLowerCase(Locale.ROOT), req);
            }
        }

        List<IngredienteReceta> ingredientes = new ArrayList<>();
        for (RecetaIngredienteRequest req : porNombre.values()) {
            String nombre = req.getNombre().trim();

            Ingrediente ingrediente = ingredienteRepository.findByNombreIgnoreCase(nombre)
                    .orElseGet(() -> {
                        Ingrediente nuevo = new Ingrediente();
                        nuevo.setNombre(nombre);
                        return ingredienteRepository.save(nuevo);
                    });

            IngredienteReceta ingredienteReceta = new IngredienteReceta();
            ingredienteReceta.setReceta(receta);
            ingredienteReceta.setIngrediente(ingrediente);
            ingredienteReceta.setCantidad(req.getCantidad());
            ingredienteReceta.setUnidad(normalizeText(req.getUnidad()));
            ingredientes.add(ingredienteReceta);
        }

        return ingredientes;
    }

    private List<String> normalizeIngredientNames(List<String> ingredientes) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (ingredientes == null) {
            return new ArrayList<>();
        }

        for (String ingrediente : ingredientes) {
            String limpio = normalizeText(ingrediente);
            if (limpio != null) {
                set.add(limpio.toLowerCase(Locale.ROOT));
            }
        }

        return new ArrayList<>(set);
    }

    private boolean recetaCumpleIngredientes(Receta receta, List<String> ingredientes, boolean matchAll) {
        if (receta.getIngredientesReceta() == null || receta.getIngredientesReceta().isEmpty()) {
            return false;
        }

        LinkedHashSet<String> nombres = new LinkedHashSet<>();
        for (IngredienteReceta ir : receta.getIngredientesReceta()) {
            if (ir.getIngrediente() != null && ir.getIngrediente().getNombre() != null) {
                nombres.add(ir.getIngrediente().getNombre().trim().toLowerCase(Locale.ROOT));
            }
        }

        if (matchAll) {
            return nombres.containsAll(ingredientes);
        }

        for (String ingrediente : ingredientes) {
            if (nombres.contains(ingrediente)) {
                return true;
            }
        }
        return false;
    }

    private int safeTime(Integer value) {
        return value == null || value < 0 ? 0 : value;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String limpio = value.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
