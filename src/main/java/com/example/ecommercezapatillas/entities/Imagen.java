package com.example.ecommercezapatillas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagen")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Imagen extends Base {
    @Column(name = "url", nullable = false)
    private String url;
    @Column(name = "public_id") 
    private String publicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_id") 
    private Detalle detalle;
    

}
