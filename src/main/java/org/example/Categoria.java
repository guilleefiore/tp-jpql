package org.example;


import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String denominacion;
    private boolean esInsumo;

    @OneToMany
    @JoinColumn(name = "categoria_fk")
    @Builder.Default
    private Set<Articulo> articulos = new HashSet<>();



}