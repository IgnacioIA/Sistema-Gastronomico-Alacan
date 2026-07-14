package com.Alacan.demo.Produccion.infrastructure.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.query.ArticuloConStock;
import com.Alacan.demo.Produccion.domain.query.Direccion;
import com.Alacan.demo.Produccion.domain.query.FiltroInventario;
import com.Alacan.demo.Produccion.domain.query.FiltroListaArticulos;
import com.Alacan.demo.Produccion.domain.query.OrdenIngrediente;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

@Repository
public class ArticuloRepositoryImpl implements ArticuloRepository {

    private final ArticuloRepositoryJpa articuloRepositoryJpa;

    @PersistenceContext
    private EntityManager entityManager;

    public ArticuloRepositoryImpl(ArticuloRepositoryJpa articuloRepositoryJpa) {
        this.articuloRepositoryJpa = articuloRepositoryJpa;
    }

    @Override
    public Optional<Articulo> buscarPorId(Long id) {
        return articuloRepositoryJpa.findById(id);
    }

    @Override
    public List<Articulo> buscarTodos() {
        return articuloRepositoryJpa.findAll();
    }

    @Override
    public void guardar(Articulo articulo) {
        articuloRepositoryJpa.save(articulo);
    }

    // Consulta de lectura que cruza Articulo y Stock (agregados separados en escritura,
    // ver docs/modules/produccion.md sección 6). No hay mapeo JPA bidireccional entre
    // ambos a propósito, así que se arma el join explícitamente con Criteria API en vez
    // de depender de derived queries de Spring Data, que no permiten filtrar/ordenar de
    // forma dinámica cruzando dos raíces con paginación correcta.
    @Override
    public ResultadoPaginado<ArticuloConStock> buscarConStockPaginado(
            TipoArticulo tipoArticulo,
            FiltroListaArticulos filtro,
            OrdenIngrediente orden,
            Paginacion paginacion) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Articulo> articuloRoot = query.from(Articulo.class);
        Root<Stock> stockRoot = query.from(Stock.class);

        query.multiselect(List.<Selection<?>>of(articuloRoot, stockRoot));
        query.where(construirPredicados(cb, articuloRoot, stockRoot, tipoArticulo, filtro)
                .toArray(new Predicate[0]));
        query.orderBy(construirOrden(cb, articuloRoot, stockRoot, orden));

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(paginacion.offset());
        typedQuery.setMaxResults(paginacion.getSize());

        List<ArticuloConStock> content = typedQuery.getResultList().stream()
                .map(fila -> new ArticuloConStock((Articulo) fila[0], (Stock) fila[1]))
                .toList();

        long totalElements = contar(cb, tipoArticulo, filtro);

        return new ResultadoPaginado<>(content, paginacion.getPage(), paginacion.getSize(), totalElements);
    }

    private long contar(CriteriaBuilder cb, TipoArticulo tipoArticulo, FiltroListaArticulos filtro) {

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Articulo> articuloRoot = countQuery.from(Articulo.class);
        Root<Stock> stockRoot = countQuery.from(Stock.class);

        countQuery.select(cb.count(articuloRoot));
        countQuery.where(construirPredicados(cb, articuloRoot, stockRoot, tipoArticulo, filtro)
                .toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> construirPredicados(CriteriaBuilder cb,
            Root<Articulo> articuloRoot,
            Root<Stock> stockRoot,
            TipoArticulo tipoArticulo,
            FiltroListaArticulos filtro) {

        List<Predicate> predicados = new ArrayList<>();

        predicados.add(cb.equal(stockRoot.get("articulo"), articuloRoot));
        predicados.add(cb.equal(articuloRoot.get("tipoArticulo"), tipoArticulo));

        if (filtro.getActivo() != null) {
            predicados.add(cb.equal(articuloRoot.get("activo"), filtro.getActivo()));
        }

        if (filtro.getSearch() != null && !filtro.getSearch().isBlank()) {
            predicados.add(cb.like(cb.lower(articuloRoot.get("nombre")), "%" + filtro.getSearch().toLowerCase() + "%"));
        }

        if (filtro.getUnidadMedidaId() != null) {
            predicados.add(cb.equal(articuloRoot.get("unidadMedida").get("id"), filtro.getUnidadMedidaId()));
        }

        if (filtro.getFiltroInventario() != null) {
            predicados.add(predicadoInventario(cb, stockRoot, filtro.getFiltroInventario()));
        }

        return predicados;
    }

    private Predicate predicadoInventario(CriteriaBuilder cb, Root<Stock> stockRoot, FiltroInventario filtroInventario) {

        Expression<BigDecimal> cantidadActual = stockRoot.get("cantidadActual");
        Expression<BigDecimal> stockMinimo = stockRoot.get("stockMinimo");

        switch (filtroInventario) {
            case SIN_STOCK:
                return cb.equal(cantidadActual, BigDecimal.ZERO);
            case NECESITA_REPOSICION:
                return cb.and(
                        cb.greaterThan(cantidadActual, BigDecimal.ZERO),
                        cb.lessThan(cantidadActual, stockMinimo));
            case CON_STOCK:
                return cb.greaterThanOrEqualTo(cantidadActual, stockMinimo);
            default:
                throw new IllegalStateException("Filtro de inventario no soportado: " + filtroInventario);
        }
    }

    private List<Order> construirOrden(CriteriaBuilder cb,
            Root<Articulo> articuloRoot,
            Root<Stock> stockRoot,
            OrdenIngrediente orden) {

        Expression<?> expresion;

        switch (orden.getCampo()) {
            case NOMBRE:
                expresion = articuloRoot.<String>get("nombre");
                break;
            case CANTIDAD_ACTUAL:
                expresion = stockRoot.<BigDecimal>get("cantidadActual");
                break;
            case CANTIDAD_DISPONIBLE:
                expresion = cb.<BigDecimal>diff(stockRoot.get("cantidadActual"), stockRoot.get("cantidadReservada"));
                break;
            case STOCK_MINIMO:
                expresion = stockRoot.<BigDecimal>get("stockMinimo");
                break;
            case NECESITA_REPOSICION:
                // 0 = necesita reposición, 1 = no necesita: ascendente muestra primero los que necesitan reponerse.
                expresion = cb.<Integer>selectCase()
                        .when(cb.lessThan(stockRoot.get("cantidadActual"), stockRoot.get("stockMinimo")), 0)
                        .otherwise(1);
                break;
            default:
                throw new IllegalStateException("Campo de ordenamiento no soportado: " + orden.getCampo());
        }

        Order order = orden.getDireccion() == Direccion.ASC ? cb.asc(expresion) : cb.desc(expresion);
        return List.of(order);
    }
}
