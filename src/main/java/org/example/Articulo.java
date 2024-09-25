package org.example;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true)
    protected String codigo;
    protected String denominacion;
    protected Double precioVenta;

    @OneToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,  CascadeType.REMOVE})
    @JoinColumn(name = "articulo_fk")
    @Builder.Default
    protected Set<Imagen> imagenes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "unidadMedida_fk")
    protected UnidadMedida unidadMedida;


    public Double getPrecioVenta() {
        if(precioVenta != null)
            return precioVenta;
        return 0d;
    }
    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

}
