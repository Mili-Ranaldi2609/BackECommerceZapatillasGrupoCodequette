    package com.example.ecommercezapatillas.entities;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;

    @Entity
    @Table(name = "imagen")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class Imagen extends Base{

        @Column(name = "denominacion")
        private String denominacion;
        @ManyToMany(mappedBy = "imagenes")
        @JsonBackReference
        private List<Detalle> detalles;



    }
