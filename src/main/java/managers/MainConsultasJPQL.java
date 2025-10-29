package managers;

import funciones.FuncionApp;
import org.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainConsultasJPQL {

    public static void main(String[] args) {
        //REPOSITORIO-> https://github.com/gerardomagni/jpqlquerys.git

        // 1. Listar todos los clientes registrados en el sistema
        listarTodosLosClientes();

        // 2. Facturas emitidas en el último mes a partir de la fecha actual
        listarFacturasUltimoMes();

        // 3. Cliente que generó más facturas
        clienteConMasFacturas();

        // 4. Productos más vendidos (cantidad total vendida)
        listarArticulosMasVendidos();

        // 5. Facturas de los 3 últimos meses (cliente específico)
        listarFacturasUltimosTresMesesDeCliente(2L); // se podría pedir por consola también

        // 6. Monto total facturado por un cliente
        calcularMontoTotalFacturadoPorCliente(1L);

        // 7. Artículos vendidos en una factura específica (ID)
        listarArticulosDeFactura(1L); // cambiar ID si se quiere

        // 8. Artículo más caro vendido en una factura
        obtenerArticuloMasCaroDeFactura(1L);

        // 9. Facturas generadas en el sistema
        contarFacturas();

        // 10. Facturas con total > a uno determinado
        listarFacturasConTotalMayorA(1000.0);

        // 11. Facturas con artículo específico (nombre)
        listarFacturasPorNombreDeArticulo("Manzana");

        // 12. Artículos filtrados por código parcial con LIKE
        listarArticulosPorCodigoParcial("123");

        // 13. Artículos con precio mayor que el promedio de todos (subconsultas)
        listarArticulosConPrecioMayorAlPromedio();

        // 14. Ejemplo de EXISTS
        listarClientesConFacturas();
    }

    public static void buscarFacturas(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturas();
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturasActivas(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasActivas();
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturasXNroComprobante(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasXNroComprobante(796910l);
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturasXRangoFechas(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaInicio = FuncionApp.restarSeisMeses(fechaActual);
            List<Factura> facturas = mFactura.buscarFacturasXRangoFechas(fechaInicio, fechaActual);
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturaXPtoVentaXNroComprobante(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            Factura factura = mFactura.getFacturaXPtoVentaXNroComprobante(2024, 796910l);
            mostrarFactura(factura);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturasXCliente(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasXCliente(7l);
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturasXCuitCliente(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasXCuitCliente("27236068981");
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarFacturasXArticulo(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasXArticulo(6l);
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void mostrarMaximoNroFactura(){
        FacturaManager mFactura = new FacturaManager(true);
        try {
            Long nroCompMax = mFactura.getMaxNroComprobanteFactura();
            System.out.println("N° " + nroCompMax);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mFactura.cerrarEntityManager();
        }
    }

    public static void buscarClientesXIds(){
        ClienteManager mCliente = new ClienteManager(true);
        try {
            List<Long> idsClientes = new ArrayList<>();
            idsClientes.add(1l);
            idsClientes.add(2l);
            List<Cliente> clientes = mCliente.getClientesXIds(idsClientes);
            for(Cliente cli : clientes){
                System.out.println("Id: " + cli.getId());
                System.out.println("CUIT: " + cli.getCuit());
                System.out.println("Razon Social: " + cli.getRazonSocial());
                System.out.println("-----------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mCliente.cerrarEntityManager();
        }
    }

    public static void buscarClientesXRazonSocialParcial(){
        ClienteManager mCliente = new ClienteManager(true);
        try {
            List<Long> idsClientes = new ArrayList<>();
            idsClientes.add(1l);
            idsClientes.add(2l);
            List<Cliente> clientes = mCliente.getClientesXRazonSocialParcialmente("Lui");
            for(Cliente cli : clientes){
                System.out.println("Id: " + cli.getId());
                System.out.println("CUIT: " + cli.getCuit());
                System.out.println("Razon Social: " + cli.getRazonSocial());
                System.out.println("-----------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            mCliente.cerrarEntityManager();
        }
    }



    public static void mostrarFactura(Factura factura){
        List<Factura> facturas = new ArrayList<>();
        facturas.add(factura);
        mostrarFacturas(facturas);
    }

    public static void mostrarFacturas(List<Factura> facturas){
        for(Factura fact : facturas){
            System.out.println("N° Comp: " + fact.getStrProVentaNroComprobante());
            System.out.println("Fecha: " + FuncionApp.formatLocalDateToString(fact.getFechaComprobante()));
            System.out.println("CUIT Cliente: " + FuncionApp.formatCuitConGuiones(fact.getCliente().getCuit()));
            System.out.println("Cliente: " + fact.getCliente().getRazonSocial() + " ("+fact.getCliente().getId() + ")");
            System.out.println("------Articulos------");
            for(FacturaDetalle detalle : fact.getDetallesFactura()){
                System.out.println(detalle.getArticulo().getDenominacion() + ", " + detalle.getCantidad() + " unidades, $" + FuncionApp.getFormatMilDecimal(detalle.getSubTotal(), 2));
            }
            System.out.println("Total: $" + FuncionApp.getFormatMilDecimal(fact.getTotal(),2));
            System.out.println("*************************");
        }
    }

    // 1. Listar todos los clientes registrados en el sistema
    public static void listarTodosLosClientes() {
        ClienteManager mCliente = new ClienteManager(true);
        try {
            List<Cliente> clientes = mCliente.getTodosLosClientes();
            for (Cliente cli : clientes) {
                System.out.println(cli);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mCliente.cerrarEntityManager();
        }
    }

    // 2. Facturas emitidas en el último mes a partir de la fecha actual
    public static void listarFacturasUltimoMes() {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasUltimoMes();
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 3. Cliente que generó más facturas
    public static void clienteConMasFacturas() {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            Object[] resultado = mFactura.getClienteConMasFacturas(); // la consulta devuelve dos valores por fila, un array con dos posiciones
            // Casteamos
            Cliente cliente = (Cliente) resultado[0]; // posición 0 tipo Cliente
            Long totalFacturas =  (Long) resultado[1]; // posición 1 tipo Long (número de facturas)
            System.out.println("Cliente con más facturas: " + cliente.getId());
            System.out.println("\nTotal de facturas: " + totalFacturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 4. Productos más vendidos (cantidad total vendida)
    public static void listarArticulosMasVendidos() {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Object[]> resultados = mFactura.getArticulosMasVendidos();
            for (Object[] fila : resultados) {
                Articulo articulo = (Articulo) fila[0];
                Long totalVendido = (Long) fila[1];
                System.out.println("Artículo: " + articulo.getDenominacion() + " | Total vendido: " + totalVendido);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }  finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 5. Facturas de los 3 últimos meses (cliente específico)
    public static void listarFacturasUltimosTresMesesDeCliente(Long idCliente) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasUltimosTresMesesDeCliente(idCliente);
            mostrarFacturas(facturas);
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 6. Monto total facturado por un cliente
    public static void calcularMontoTotalFacturadoPorCliente(Long idCliente) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            Double total = mFactura.getMontoTotalFacturadoPorCliente(idCliente);
            System.out.println("Monto total facturado por el cliente con ID " + idCliente + ": $" + total);
        } catch (Exception ex) {
            ex.printStackTrace();
        }  finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 7. Artículos vendidos en una factura específica (ID)
    public static void listarArticulosDeFactura(Long idFactura) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Articulo> articulos = mFactura.getArticulosDeFactura(idFactura);
            System.out.println("Artículos vendidos en la factura ID " + idFactura + ":");
            for (Articulo articulo : articulos) {
                System.out.println("- " + articulo.getDenominacion());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }  finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 8. Artículo más caro vendido en una factura
    public static void obtenerArticuloMasCaroDeFactura(Long idFactura) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            Articulo articulo = mFactura.getArticuloMasCaroDeFactura(idFactura);
            System.out.println("Artículo más caro en la factura ID " + idFactura + ": " + articulo.getDenominacion() + " ($" + articulo.getPrecioVenta() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 9. Facturas generadas en el sistema
    public static void contarFacturas() {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            Long cantidad = mFactura.getCantidadTotalFacturas();
            System.out.println("Cantidad total de facturas generadas en el sistema: " + cantidad);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 10. Facturas con total > a uno determinado
    public static void listarFacturasConTotalMayorA(Double valorMinimo) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasConTotalMayorA(valorMinimo);
            System.out.println("Facturas con total mayor a $" + valorMinimo + ":");
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 11. Facturas con artículo específico (nombre)
    public static void listarFacturasPorNombreDeArticulo(String nombreArticulo) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Factura> facturas = mFactura.getFacturasPorNombreDeArticulo(nombreArticulo);
            System.out.println("Facturas que contienen el artículo '" + nombreArticulo + "':");
            mostrarFacturas(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 12. Artículos filtrados por código parcial con LIKE
    public static void listarArticulosPorCodigoParcial(String codigoParcial) {
        FacturaManager mFactura = new FacturaManager(true);
        try {
            List<Articulo> articulos = mFactura.getArticulosPorCodigoParcial(codigoParcial);
            System.out.println("Artículos cuyo código contiene '" + codigoParcial + "':");
            for (Articulo art : articulos) {
                System.out.println("- " + art.getCodigo() + " | " + art.getDenominacion());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 13. Artículos con precio mayor que el promedio de todos (subconsultas)
    public static void listarArticulosConPrecioMayorAlPromedio() {
        FacturaManager mFactura = new FacturaManager(true); // o ArticuloManager si lo tenés
        try {
            List<Articulo> articulos = mFactura.getArticulosConPrecioMayorAlPromedio();
            System.out.println("Artículos con precio mayor al promedio:");
            for (Articulo art : articulos) {
                System.out.println("- " + art.getDenominacion() + " | $" + art.getPrecioVenta());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mFactura.cerrarEntityManager();
        }
    }

    // 14. Ejemplo de EXISTS
    public static void listarClientesConFacturas() {
        ClienteManager mCliente = new ClienteManager(true);
        try {
            List<Cliente> clientes = mCliente.getClientesConFacturas();
            System.out.println("Clientes con al menos una factura:");
            for (Cliente cli : clientes) {
                System.out.println("- " + cli.getRazonSocial() + " (ID: " + cli.getId() + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mCliente.cerrarEntityManager();
        }
    }
}