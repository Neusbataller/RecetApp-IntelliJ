package com.recetapp.recetas_pi.controller.receta;

import com.recetapp.recetas_pi.dto.receta.RecetaCreateRequest;
import com.recetapp.recetas_pi.dto.receta.RecetaIngredienteResponse;
import com.recetapp.recetas_pi.dto.receta.RecetaResponse;
import com.recetapp.recetas_pi.dto.receta.RecetaUpdateRequest;
import com.recetapp.recetas_pi.model.Categoria;
import com.recetapp.recetas_pi.model.IngredienteReceta;
import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.service.RecetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recetas")
@Validated
@Tag(name = "Recetas", description = "Endpoints para CRUD y busqueda de recetas")
/**
 * Controlador REST para la gestión de recetas.
 *
 * <p>
 * Este controlador está pensado para que frontend pueda:
 * listar recetas, filtrarlas (título, ingredientes, tags),
 * consultar detalle y hacer CRUD completo.
 * </p>
 */
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    /**
     * Obtener todas las recetas o filtrar por título.
     * GET /api/recetas/getAll
     *
     * Query params opcionales:
     * - titulo: busca coincidencias por texto en el título.
     *
     * Ejemplo:
     * /api/recetas/getAll?titulo=pasta
     */
    @Operation(
            summary = "Listar recetas",
            description = "Devuelve todas las recetas o filtra por titulo. React Native: fetch('/api/recetas/getAll?titulo=pasta')."
    )
    @ApiResponse(responseCode = "200", description = "Listado de recetas")
    @GetMapping({"/getAll"})
    public ResponseEntity<List<RecetaResponse>> getAll(@RequestParam(required = false) String titulo) {
        List<Receta> recetas;
        if (titulo != null && !titulo.isBlank()) {
            recetas = recetaService.buscarRecetas(titulo);
        } else {
            recetas = recetaService.getAllRecetas();
        }

        List<RecetaResponse> response = new ArrayList<>();
        for (Receta r : recetas) {
            response.add(toResponse(r));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener recetas del usuario autenticado.
     * GET /api/recetas/getMine
     *
     * Requiere JWT. Devuelve 401 si no hay usuario autenticado.
     */
    @Operation(
            summary = "Mis recetas",
            description = "Devuelve las recetas del usuario autenticado. React Native: fetch('/api/recetas/getMine', { headers: { Authorization: 'Bearer '+token } })."
    )
    @ApiResponse(responseCode = "200", description = "Listado de recetas del usuario")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping({"/getMine"})
    public ResponseEntity<?> getMine() {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        return ResponseEntity.ok(toResponseList(recetaService.getRecetasByUsuarioCorreo(correo)));
    }

    /**
     * Buscar recetas por dificultad.
     * GET /api/recetas/searchByDificultad?dificultad=media
     */
    @Operation(
            summary = "Buscar por dificultad",
            description = "Filtra por dificultad exacta. React Native: fetch('/api/recetas/searchByDificultad?dificultad=media')."
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado")
    @GetMapping({"/searchByDificultad"})
    public ResponseEntity<List<RecetaResponse>> searchByDificultad(@RequestParam String dificultad) {
        List<Receta> recetas = recetaService.buscarPorDificultad(dificultad);
        return ResponseEntity.ok(toResponseList(recetas));
    }

    /**
     * Buscar recetas por tiempo total máximo (preparación + cocción).
     * GET /api/recetas/searchByTiempoMax?maxTiempo=30
     */
    @Operation(
            summary = "Buscar por tiempo maximo",
            description = "Filtra por tiempo total maximo en minutos. React Native: fetch('/api/recetas/searchByTiempoMax?maxTiempo=30')."
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado")
    @GetMapping({"/searchByTiempoMax"})
    public ResponseEntity<List<RecetaResponse>> searchByTiempoMax(@RequestParam Integer maxTiempo) {
        List<Receta> recetas = recetaService.buscarPorTiempoMax(maxTiempo);
        return ResponseEntity.ok(toResponseList(recetas));
    }

    /**
     * Búsqueda avanzada combinando filtros en una sola llamada.
     * GET /api/recetas/searchAdvanced
     *
     * Query params opcionales:
     * - titulo
     * - dificultad
     * - tag
     * - maxTiempo
     * - ingredientes (CSV)
     * - matchAll (si true exige todos los ingredientes)
     *
     * Ejemplo:
     * /api/recetas/searchAdvanced?titulo=pasta&dificultad=media&ingredientes=tomate,ajo&matchAll=true&maxTiempo=30
     */
    @Operation(
            summary = "Busqueda avanzada",
            description = "Combina filtros en una sola llamada. React Native: fetch('/api/recetas/searchAdvanced?titulo=pasta&ingredientes=tomate,ajo&matchAll=true')."
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado")
    @GetMapping({"/searchAdvanced"})
    public ResponseEntity<List<RecetaResponse>> searchAdvanced(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String dificultad,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer maxTiempo,
            @RequestParam(required = false) String ingredientes,
            @RequestParam(defaultValue = "false") boolean matchAll
    ) {
        List<String> ingredientesList = new ArrayList<>();
        if (ingredientes != null && !ingredientes.isBlank()) {
            ingredientesList = Arrays.stream(ingredientes.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        }

        List<Receta> recetas = recetaService.buscarAvanzado(
                titulo,
                dificultad,
                tag,
                maxTiempo,
                ingredientesList,
                matchAll
        );
        return ResponseEntity.ok(toResponseList(recetas));
    }

    /**
     * Buscar recetas que contengan un ingrediente exacto (por nombre).
     * GET /api/recetas/searchByIngrediente?ingrediente=tomate
     */
    @Operation(
            summary = "Buscar por ingrediente",
            description = "Busca recetas que contengan un ingrediente. React Native: fetch('/api/recetas/searchByIngrediente?ingrediente=tomate')."
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado")
    @GetMapping({"/searchByIngrediente"})
    public ResponseEntity<List<RecetaResponse>> searchByIngrediente(@RequestParam String ingrediente) {
        List<Receta> recetas = recetaService.buscarPorIngrediente(ingrediente);
        return ResponseEntity.ok(toResponseList(recetas));
    }

    /**
     * Buscar recetas por varios ingredientes.
     * GET /api/recetas/searchByIngredientes
     *
     * Query params:
     * - ingredientes: lista separada por comas (ej: tomate,ajo,cebolla)
     * - matchAll: si true exige que estén todos; si false, con cualquiera vale.
     *
     * Ejemplo:
     * /api/recetas/searchByIngredientes?ingredientes=tomate,ajo&matchAll=true
     */
    @Operation(
            summary = "Buscar por varios ingredientes",
            description = "Permite busqueda por lista CSV de ingredientes. React Native: fetch('/api/recetas/searchByIngredientes?ingredientes=tomate,ajo&matchAll=false')."
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado")
    @GetMapping({"/searchByIngredientes"})
    public ResponseEntity<List<RecetaResponse>> searchByIngredientes(
            @RequestParam String ingredientes,
            @RequestParam(defaultValue = "false") boolean matchAll
    ) {
        List<String> ingredientesList = Arrays.stream(ingredientes.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        List<Receta> recetas = recetaService.buscarPorIngredientes(ingredientesList, matchAll);
        return ResponseEntity.ok(toResponseList(recetas));
    }

    /**
     * Buscar recetas por tag.
     * GET /api/recetas/searchByTag?tag=vegano
     *
     * Nota: actualmente tag se basa en categorías asociadas a la receta.
     */
    @Operation(
            summary = "Buscar por tag",
            description = "Filtra por categoria/tag. React Native: fetch('/api/recetas/searchByTag?tag=vegano')."
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado")
    @GetMapping({"/searchByTag"})
    public ResponseEntity<List<RecetaResponse>> searchByTag(@RequestParam String tag) {
        List<Receta> recetas = recetaService.buscarPorTag(tag);
        return ResponseEntity.ok(toResponseList(recetas));
    }

    /**
     * Obtener una receta por su ID.
     * GET /api/recetas/get/{id}
     *
     * Devuelve 404 si la receta no existe.
     */
    @Operation(
            summary = "Detalle de receta",
            description = "Obtiene una receta por ID. React Native: fetch('/api/recetas/get/12')."
    )
    @ApiResponse(responseCode = "200", description = "Receta encontrada")
    @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    @GetMapping({"/get/{id}"})
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Receta> receta = recetaService.getRecetaById(id);
        if (receta.isPresent()) {
            return ResponseEntity.ok(toResponse(receta.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receta no encontrada");
    }

    /**
     * Crear una receta nueva.
     * POST /api/recetas/create
     *
     * Body JSON esperado:
     * {
     *   "usuarioId": 1,
     *   "titulo": "Pasta rápida",
     *   "descripcion": "Ideal para diario",
     *   "preparacion": "...",
     *   "tiempoPreparacionMin": 10,
     *   "tiempoCoccionMin": 12,
     *   "porciones": 2,
     *   "dificultad": "media",
     *   "imagenUrl": "https://...",
     *   "ingredientes": [
     *     { "nombre": "Pasta", "cantidad": 200, "unidad": "g" }
     *   ]
     * }
     */
    @Operation(
            summary = "Crear receta",
            description = "Crea una receta con ingredientes. React Native: fetch('/api/recetas/create', { method: 'POST', headers: { Authorization: 'Bearer '+token, 'Content-Type':'application/json' }, body: JSON.stringify(payload) })."
    )
    @ApiResponse(responseCode = "201", description = "Receta creada")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping({"/create"})
    public ResponseEntity<?> create(@Valid @RequestBody RecetaCreateRequest req) {
        try {
            Receta created = recetaService.create(
                    req.getUsuarioId(),
                    req.getTitulo(),
                    req.getDescripcion(),
                    req.getPreparacion(),
                    req.getTiempoPreparacionMin(),
                    req.getTiempoCoccionMin(),
                    req.getPorciones(),
                    req.getDificultad(),
                    req.getImagenUrl(),
                    req.getIngredientes());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Actualizar una receta existente.
     * PUT /api/recetas/update/{id}
     *
     * Usa la misma estructura de body que create.
     * Devuelve 404 si no existe la receta.
     */
    @Operation(
            summary = "Actualizar receta",
            description = "Actualiza una receta existente. React Native: fetch('/api/recetas/update/'+id, { method: 'PUT', headers: { Authorization: 'Bearer '+token, 'Content-Type':'application/json' }, body: JSON.stringify(payload) })."
    )
    @ApiResponse(responseCode = "200", description = "Receta actualizada")
    @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping({"/update/{id}"})
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RecetaUpdateRequest req) {
        try {
            Receta updated = recetaService.update(
                    id,
                    req.getUsuarioId(),
                    req.getTitulo(),
                    req.getDescripcion(),
                    req.getPreparacion(),
                    req.getTiempoPreparacionMin(),
                    req.getTiempoCoccionMin(),
                    req.getPorciones(),
                    req.getDificultad(),
                    req.getImagenUrl(),
                    req.getIngredientes());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Receta no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    /**
     * Eliminar una receta por ID.
     * DELETE /api/recetas/delete/{id}
     *
     * Devuelve 404 si la receta no existe.
     */
    @Operation(
            summary = "Eliminar receta",
            description = "Elimina una receta por ID. React Native: fetch('/api/recetas/delete/'+id, { method:'DELETE', headers:{ Authorization:'Bearer '+token } })."
    )
    @ApiResponse(responseCode = "200", description = "Receta eliminada")
    @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping({"/delete/{id}"})
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            recetaService.deleteById(id);
            return ResponseEntity.ok("Receta eliminada correctamente");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    private String getCorreoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof String correo && !"anonymousUser".equals(correo)) {
            return correo;
        }
        return null;
    }

    private List<RecetaResponse> toResponseList(List<Receta> recetas) {
        List<RecetaResponse> response = new ArrayList<>();
        for (Receta receta : recetas) {
            response.add(toResponse(receta));
        }
        return response;
    }

    private RecetaResponse toResponse(Receta r) {
        RecetaResponse response = new RecetaResponse();
        response.setId(r.getId());
        if (r.getUsuario() != null) {
            response.setUsuarioId(r.getUsuario().getId());
        }
        response.setTitulo(r.getTitulo());
        response.setDescripcion(r.getDescripcion());
        response.setPreparacion(r.getPreparacion());
        response.setTiempoPreparacionMin(r.getTiempoPreparacionMin());
        response.setTiempoCoccionMin(r.getTiempoCoccionMin());
        response.setPorciones(r.getPorciones());
        response.setDificultad(r.getDificultad());
        response.setImagenUrl(r.getImagenUrl());
        response.setFechaCreacion(r.getFechaCreacion());

        if (r.getIngredientesReceta() != null) {
            List<RecetaIngredienteResponse> ingredientes = new ArrayList<>();
            for (IngredienteReceta ir : r.getIngredientesReceta()) {
                RecetaIngredienteResponse ingrediente = new RecetaIngredienteResponse();
                if (ir.getIngrediente() != null) {
                    ingrediente.setId(ir.getIngrediente().getId());
                    ingrediente.setNombre(ir.getIngrediente().getNombre());
                }
                ingrediente.setCantidad(ir.getCantidad());
                ingrediente.setUnidad(ir.getUnidad());
                ingredientes.add(ingrediente);
            }
            response.setIngredientes(ingredientes);
        }

        if (r.getCategorias() != null) {
            List<String> tags = new ArrayList<>();
            for (Categoria categoria : r.getCategorias()) {
                tags.add(categoria.getNombre());
            }
            response.setTags(tags);
        }
        return response;
    }
}
