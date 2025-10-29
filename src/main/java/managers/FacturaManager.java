package managers;

import org.example.Articulo;
import org.example.Factura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacturaManager {
    EntityManagerFactory emf = null;
    EntityManager em = null;

    public FacturaManager(boolean anularShowSQL) {
        Map<String, Object> properties = new HashMap<>();
        if(anularShowSQL){
            // Desactivar el show_sql (si está activado en el persistence.xml o configuración por defecto)
            properties.put("hibernate.show_sql", "false");
        }else{
            properties.put("hibernate.show_sql", "true");
        }
        emf = Persistence.createEntityManagerFactory("example-unit", properties);
        em = emf.createEntityManager();

    }

    public List<Factura> getFacturas(){
        String jpql = "FROM Factura";
        Query query = em.createQuery(jpql);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public List<Factura> getFacturasActivas(){
        //si quiero buscar distintos de NULL uso -> IS NOT NULL
        String jpql = "FROM Factura WHERE fechaBaja IS NULL ORDER BY fechaComprobante DESC";
        Query query = em.createQuery(jpql);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public List<Factura> getFacturasXNroComprobante(Long nroComprobante){
        String jpql = "FROM Factura WHERE nroComprobante = :nroComprobante";
        Query query = em.createQuery(jpql);
        query.setParameter("nroComprobante", nroComprobante);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public List<Factura> buscarFacturasXRangoFechas(LocalDate fechaInicio, LocalDate fechaFin){
        String jpql = "FROM Factura WHERE fechaComprobante >= :fechaInicio AND fechaComprobante <= :fechaFin";
        Query query = em.createQuery(jpql);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public Factura getFacturaXPtoVentaXNroComprobante(Integer puntoVenta, Long nroComprobante){
        String jpql = "FROM Factura WHERE puntoVenta = :puntoVenta AND nroComprobante = :nroComprobante";
        Query query = em.createQuery(jpql);
        query.setMaxResults(1);
        query.setParameter("puntoVenta", puntoVenta);
        query.setParameter("nroComprobante", nroComprobante);

        Factura factura = (Factura) query.getSingleResult();
        return factura;
    }

    public List<Factura> getFacturasXCliente(Long idCliente){
        String jpql = "FROM Factura WHERE cliente.id = :idCliente";
        Query query = em.createQuery(jpql);
        query.setParameter("idCliente", idCliente);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public List<Factura> getFacturasXCuitCliente(String cuitCliente){
        String jpql = "FROM Factura WHERE cliente.cuit = :cuitCliente";
        Query query = em.createQuery(jpql);
        query.setParameter("cuitCliente", cuitCliente);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public List<Factura> getFacturasXArticulo(Long idArticulo){ //INNER JOIN, LEFT JOIN, LEFT OUTER JOIN, etc
        StringBuilder jpql = new StringBuilder("SELECT fact FROM Factura AS fact LEFT OUTER JOIN fact.detallesFactura AS detalle");
        jpql.append(" WHERE detalle.id = :idArticulo");
        Query query = em.createQuery(jpql.toString());
        query.setParameter("idArticulo", idArticulo);

        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public Long getMaxNroComprobanteFactura(){ //MAX, MIN, COUNT, AVG, SUM
        StringBuilder jpql = new StringBuilder("SELECT MAX(nroComprobante) FROM Factura WHERE fechaBaja IS NULL");
        Query query = em.createQuery(jpql.toString());

        Long maxNroFactura = (Long) query.getSingleResult();
        return maxNroFactura;
    }

    public void cerrarEntityManager(){
        em.close();
        emf.close();
    }

    // 2. Facturas emitidas en el último mes a partir de la fecha actual
    public List<Factura> getFacturasUltimoMes() {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaInicio = fechaActual.minusMonths(1);

        String jpql = "FROM Factura f WHERE f.fechaComprobante BETWEEN :fechaInicio AND :fechaActual";
        Query query = em.createQuery(jpql);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaActual", fechaActual);

        return query.getResultList();
    }

    // 3. Cliente que generó más facturas
    public Object[] getClienteConMasFacturas() { // Object[] porque la consulta devuelve dos tipos de datos
        String jpql = "SELECT f.cliente, COUNT(f) FROM Factura f, GROUP BY f.cliente, ORDER BY COUNT(f) DESC";
        Query query = em.createQuery(jpql);
        query.setMaxResults(1); // solo el cliente con más facturas

        return (Object[]) query.getSingleResult();
    }

    // 4. Productos más vendidos (cantidad total vendida)
    public List<Object[]> getArticulosMasVendidos() {
        String jpql = "SELECT d.articulo, SUM(d.cantidad), FROM FacturaDetalle d, GROUP BY d.articulo, ORDER BY SUM(d.cantidad) DESC)";
        Query query = em.createQuery(jpql);

        return query.getResultList();
    }

    // 5. Facturas de los 3 últimos meses (cliente específico)
    public List<Factura> getFacturasUltimosTresMesesDeCliente(Long idCliente){
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaInicio = fechaActual.minusMonths(3);

        String jpql = "SELECT f FROM Factura f, WHERE f.cliente.id = :idCliente, AND f.fechaComprobante BETWEEN :fechaInicio AND :fechaActual";
        Query query = em.createQuery(jpql);
        query.setParameter("idCliente", idCliente);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaActual", fechaActual);

        return query.getResultList();
    }

    // 6. Monto total facturado por un cliente
    public Double getMontoTotalFacturadoPorCliente(Long idCliente){
        String jpql = "SELECT SUM(f.total) FROM Factura f WHERE f.cliente.id = :idCliente";
        Query query = em.createQuery(jpql);
        query.setParameter("idCliente", idCliente);

        Double montoTotal = (Double) query.getSingleResult();
        return montoTotal != null ? montoTotal : 0.0; // por si no hay facturas
    }

    // 7. Artículos vendidos en una factura específica (ID)
    public List<Articulo> getArticulosDeFactura(Long idFactura) {
        String jpql = "SELECT d.articulo FROM FacturaDetalle d WHERE d.factura.id = :idFactura";
        Query query = em.createQuery(jpql);
        query.setParameter("idFactura", idFactura);

        return query.getResultList();
    }

    // 8. Artículo más caro vendido en una factura
    public Articulo getArticuloMasCaroDeFactura(Long idFactura) {
        String jpql = "SELECT d.articulo FROM FacturaDetalle d WHERE d.factura.id = :idFactura, ORDER BY d.articulo.precioVenta DESC";
        Query query = em.createQuery(jpql);
        query.setParameter("idFactura", idFactura);
        query.setMaxResults(1);

        return (Articulo) query.getSingleResult();
    }

    // 9. Facturas generadas en el sistema
    public Long getCantidadTotalFacturas() {
        String jpql = "SELECT COUNT(f) FROM Factura f";
        Query query = em.createQuery(jpql);

        return (Long) query.getSingleResult();
    }

    // 10. Facturas con total > a uno determinado
    public List<Factura> getFacturasConTotalMayorA(Double valorMinimo) {
        String jpql = "SELECT f FROM Factura f WHERE f.total > :valorMinimo";
        Query query = em.createQuery(jpql);
        query.setParameter("valorMinimo", valorMinimo);

        return query.getResultList();
    }

    // 11. Facturas con artículo específico (nombre)
    public List<Factura> getFacturasPorNombreDeArticulo(String nombreArticulo) {
        String jpql = "SELECT DISTINCT d.factura FROM FacturaDetalle d WHERE d.articulo.denominacion = :nombreArticulo";
        Query query = em.createQuery(jpql);
        query.setParameter("nombreArticulo", nombreArticulo);

        return query.getResultList();
    }

    // 12. Artículos filtrados por código parcial con LIKE
    public List<Articulo> getArticulosPorCodigoParcial(String codigoParcial) {
        String jpql = "SELECT a FROM Articulo a WHERE a.codigoParcial LIKE :codigoParcial";
        Query query = em.createQuery(jpql);
        query.setParameter("codigoParcial", "%" + codigoParcial + "%"); // búsqueda parcial

        return query.getResultList();
    }

    // 13. Artículos con precio mayor que el promedio de todos (subconsultas)
    public List<Articulo> getArticulosConPrecioMayorAlPromedio() {
        String jpql = "SELECT a FROM Articulo a " +
                "WHERE a.precioVenta > (SELECT AVG(a2.precioVenta) FROM Articulo a2)";
        Query query = em.createQuery(jpql);
        return query.getResultList();
    }
}