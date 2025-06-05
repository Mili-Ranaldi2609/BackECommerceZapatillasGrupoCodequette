package com.example.ecommercezapatillas.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "imagen")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Imagen extends Base {

    @Column(name = "denominacion")
    private String denominacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_id") 
    private Detalle detalle;

}
