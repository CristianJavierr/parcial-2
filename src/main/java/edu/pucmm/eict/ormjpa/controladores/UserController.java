package edu.pucmm.eict.ormjpa.controladores;

import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import edu.pucmm.eict.ormjpa.entidades.Usuario;
import edu.pucmm.eict.ormjpa.entidades.Roles;
import edu.pucmm.eict.ormjpa.servicios.GestionDb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {

    private GestionDb<Usuario> gestionUsuario;

    public UserController() {
        this.gestionUsuario = new GestionDb<>(Usuario.class);
    }

    public void route(Javalin app) {

        app.get("/usuarios", ctx -> {
            Usuario usuarioSession = ctx.sessionAttribute("usuario");

            if (usuarioSession == null || usuarioSession.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            List<Usuario> usuarios = gestionUsuario.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarios", usuarios);
            ctx.render("/templates/listar-usuarios.html", model);
        });

        app.get("/usuarios/editar/{id}", ctx -> {
            Usuario usuarioSession = ctx.sessionAttribute("usuario");

            if (usuarioSession == null || usuarioSession.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            long id = Long.parseLong(ctx.pathParam("id"));
            Usuario usuario = gestionUsuario.find(id);
            Map<String, Object> model = new HashMap<>();
            model.put("usuario", usuario);
            model.put("roles", Arrays.asList(Roles.values()));
            ctx.render("/templates/editar-usuario.html", model);
        });

        app.post("/usuarios/editar/{id}", ctx -> {
            Usuario usuarioSession = ctx.sessionAttribute("usuario");

            if (usuarioSession == null || usuarioSession.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            long id = Long.parseLong(ctx.pathParam("id"));
            Usuario usuario = gestionUsuario.find(id);
            usuario.setUsername(ctx.formParam("username"));
            usuario.setName(ctx.formParam("name"));
            usuario.setRol(Roles.valueOf(ctx.formParam("rol")));
            gestionUsuario.editar(usuario);
            ctx.redirect("/usuarios");
        });

        app.post("/usuarios/eliminar/{id}", ctx -> {
            Usuario usuarioSession = ctx.sessionAttribute("usuario");

            if (usuarioSession == null || usuarioSession.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            long id = Long.parseLong(ctx.pathParam("id"));
            gestionUsuario.eliminar(id);
            ctx.redirect("/usuarios");
        });

        app.get("/login", ctx -> {
            ctx.render("/templates/login.html");
        });

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            List<Usuario> usuarios = gestionUsuario.findAll();
            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);

            if (usuario != null) {
                ctx.sessionAttribute("usuario", usuario);
                ctx.status(200);
                ctx.redirect("/");
            } else {
                ctx.status(401);
                ctx.sessionAttribute("error", "Usuario o contraseña incorrectos");
                ctx.redirect("/login");
            }
        });

        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });

        app.get("/cambiar-cuenta", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/login");
        });

        app.get("/Registro", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Registro");
            modelo.put("action", "/Registro");
            ctx.render("/templates/registro.html", modelo);
        });


        app.get("/Crear", ctx -> {
            Usuario usuarioSession = ctx.sessionAttribute("usuario");

            if (usuarioSession == null || usuarioSession.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Registro");
            modelo.put("action", "/Crear");
            ctx.render("/templates/registro.html", modelo);
        });

        app.post("/Crear", ctx -> {
            Usuario usuarioSession = ctx.sessionAttribute("usuario");

            if (usuarioSession == null || usuarioSession.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            String nombre = ctx.formParam("name");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            Usuario usuario = new Usuario(username, nombre, password, Roles.USUARIO_REGULAR);
            gestionUsuario.crear(usuario);
            List<Usuario> usuarios = gestionUsuario.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarios", usuarios);
            ctx.render("/templates/listar-usuarios.html", model);
        });

        app.post("/Registro", ctx -> {
            // Recoge los parámetros enviados desde el formulario de registro
            String nombre = ctx.formParam("name");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            // Crear un nuevo usuario con rol de USUARIO_REGULAR
            Usuario usuario = new Usuario(username, nombre, password, Roles.USUARIO_REGULAR);
            gestionUsuario.crear(usuario);

            // Puedes redirigir al login o a otra página una vez registrado
            ctx.redirect("/login");
        });


    }
}