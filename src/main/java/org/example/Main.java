package org.example;

import funciones.FuncionApp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("example-unit");
            em = emf.createEntityManager();

            // ------------------------------
            // INICIO DE LA TRANSACCIÓN PRINCIPAL
            // ------------------------------
            em.getTransaction().begin();

            // Unidades de medida
            UnidadMedida unidadMedida = UnidadMedida.builder()
                    .denominacion("Kilogramo")
                    .build();

            UnidadMedida unidadMedidapote = UnidadMedida.builder()
                    .denominacion("Pote")
                    .build();

            em.persist(unidadMedida);
            em.persist(unidadMedidapote);

            // Categorías
            Categoria categoria = Categoria.builder()
                    .denominacion("Frutas")
                    .esInsumo(true)
                    .build();

            Categoria categoriaPostre = Categoria.builder()
                    .denominacion("Postres")
                    .esInsumo(false)
                    .build();

            em.persist(categoria);
            em.persist(categoriaPostre);

            // Artículos insumo (códigos únicos)
            ArticuloInsumo articuloInsumo = ArticuloInsumo.builder()
                    .denominacion("Manzana")
                    .codigo("A" + new Date().getTime())
                    .precioCompra(1.5)
                    .precioVenta(5d)
                    .stockActual(100)
                    .stockMaximo(200)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedida)
                    .build();

            ArticuloInsumo articuloInsumoPera = ArticuloInsumo.builder()
                    .denominacion("Pera")
                    .codigo("B" + new Date().getTime())
                    .precioCompra(2.5)
                    .precioVenta(10d)
                    .stockActual(130)
                    .stockMaximo(200)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedida)
                    .build();

            em.persist(articuloInsumo);
            em.persist(articuloInsumoPera);

            // Imágenes
            Imagen manza1 = Imagen.builder().denominacion("Manzana Verde").build();
            Imagen manza2 = Imagen.builder().denominacion("Manzana Roja").build();
            Imagen pera1 = Imagen.builder().denominacion("Pera Verde").build();
            Imagen pera2 = Imagen.builder().denominacion("Pera Roja").build();

            // Categorías con artículos
            categoria.getArticulos().add(articuloInsumo);
            categoria.getArticulos().add(articuloInsumoPera);

            // Artículos manufacturados y detalles
            ArticuloManufacturadoDetalle detalleManzana = ArticuloManufacturadoDetalle.builder()
                    .cantidad(2)
                    .articuloInsumo(articuloInsumo)
                    .build();

            ArticuloManufacturadoDetalle detallePera = ArticuloManufacturadoDetalle.builder()
                    .cantidad(2)
                    .articuloInsumo(articuloInsumoPera)
                    .build();

            ArticuloManufacturado articuloManufacturado = ArticuloManufacturado.builder()
                    .denominacion("Ensalada de frutas")
                    .descripcion("Ensalada de manzanas y peras")
                    .precioVenta(150d)
                    .tiempoEstimadoMinutos(10)
                    .preparacion("Cortar las frutas en trozos pequeños y mezclar")
                    .unidadMedida(unidadMedidapote)
                    .build();

            articuloManufacturado.getImagenes().add(manza1);
            articuloManufacturado.getImagenes().add(pera1);
            articuloManufacturado.getDetalles().add(detalleManzana);
            articuloManufacturado.getDetalles().add(detallePera);

            categoriaPostre.getArticulos().add(articuloManufacturado);
            em.persist(articuloManufacturado);

            // Confirmar los objetos principales
            em.getTransaction().commit();

            // ------------------------------
            // AGREGAR UNA NUEVA IMAGEN
            // ------------------------------
            em.getTransaction().begin();
            articuloManufacturado.getImagenes().add(manza2);
            em.merge(articuloManufacturado);
            em.getTransaction().commit();

            // ------------------------------
            // CREAR CLIENTE
            // ------------------------------
            em.getTransaction().begin();
            Cliente cliente = Cliente.builder()
                    .cuit(FuncionApp.generateRandomCUIT())
                    .razonSocial("Juan Perez")
                    .build();
            em.persist(cliente);
            em.getTransaction().commit();

            // ------------------------------
            // CREAR FACTURA CON DETALLES
            // ------------------------------
            em.getTransaction().begin();

            FacturaDetalle detalle1 = new FacturaDetalle(3, articuloInsumo);
            detalle1.calcularSubTotal();

            FacturaDetalle detalle2 = new FacturaDetalle(3, articuloInsumoPera);
            detalle2.calcularSubTotal();

            FacturaDetalle detalle3 = new FacturaDetalle(3, articuloManufacturado);
            detalle3.calcularSubTotal();

            Factura factura = Factura.builder()
                    .puntoVenta(2024)
                    .fechaAlta(new Date())
                    .fechaComprobante(FuncionApp.generateRandomDate())
                    .cliente(cliente)
                    .nroComprobante(FuncionApp.generateRandomNumber())
                    .build();

            factura.addDetalleFactura(detalle1);
            factura.addDetalleFactura(detalle2);
            factura.addDetalleFactura(detalle3);
            factura.calcularTotal();

            em.persist(factura);
            em.getTransaction().commit();

            // ------------------------------
            // CONSULTA DE VERIFICACIÓN
            // ------------------------------
            System.out.println("\n==== Verificando datos guardados ====");

            Query qArticulos = em.createQuery("SELECT a FROM Articulo a");
            List<Articulo> articulos = qArticulos.getResultList();
            for (Articulo a : articulos) {
                System.out.println("Artículo: " + a.getDenominacion() + " | Precio: $" + a.getPrecioVenta());
            }

            Query qFacturas = em.createQuery("SELECT f FROM Factura f");
            List<Factura> facturas = qFacturas.getResultList();
            for (Factura f : facturas) {
                System.out.println("Factura #" + f.getNroComprobante() + " | Total: $" + f.getTotal());
            }

            System.out.println("==== Fin de verificación ====\n");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }
}