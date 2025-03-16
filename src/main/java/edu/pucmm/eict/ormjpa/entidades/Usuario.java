package edu.pucmm.eict.ormjpa.entidades;


import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles rol;

    public Usuario(String username, String name, String password, Roles rol) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.rol = rol;
    }

    public Usuario() {}

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Roles getRol() {
        return rol;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public boolean checkRol(String paramRol) {
        return this.rol.equals(Roles.valueOf(paramRol));
    }


}
