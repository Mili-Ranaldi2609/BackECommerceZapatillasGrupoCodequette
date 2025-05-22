    package com.example.ecommercezapatillas.entities;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import com.example.ecommercezapatillas.entities.enums.Rol;

    import org.springframework.security.core.authority.SimpleGrantedAuthority;

    import java.util.Collection;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "users")
    public class User extends Base implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false)
        private String firstname;
        @Column(unique = true, nullable = false,length = 30)
        private String username;
        @Column(nullable = false)
        private String lastname;
        @Column(unique = true,nullable = false)
        private String email;
        @Column(nullable = false)
        private String password;

        @Enumerated(EnumType.STRING)
        private Rol role;
        @ManyToMany
        @JoinTable(name = "usuario_direccion",joinColumns = @JoinColumn(name = "usuarioId"),inverseJoinColumns = @JoinColumn(name="direccionId"))
        @JsonIgnore
        private Set<Direccion> direcciones=new HashSet<>();

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(role.name()));
        }
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

