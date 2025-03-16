package edu.pucmm.eict.ormjpa.controladores;

import com.google.gson.Gson;
import edu.pucmm.eict.ormjpa.entidades.Encuesta;
import edu.pucmm.eict.ormjpa.entidades.Usuario;
import edu.pucmm.eict.ormjpa.entidades.Roles;
import edu.pucmm.eict.ormjpa.servicios.GestionDb;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncuestaController {

    private GestionDb<Encuesta> gestionEncuesta;

    public EncuestaController() {
        this.gestionEncuesta = new GestionDb<>(Encuesta.class);
    }

    public void route(Javalin app) {

        app.get("/Encuesta/Pendientes",ctx->{
            ctx.render("templates/Encuestas-pendientes.html");
        });

        // Ruta para mostrar el formulario de edición de encuestas
        app.get("/Encuesta/Pendientes/editar/{id}", ctx -> {

            ctx.render("/templates/editar-encuesta.html");
        });

        // Ruta principal para listar encuestas
        app.get("/", ctx -> {

            Usuario usuario = ctx.sessionAttribute("usuario");

            List<Encuesta> encuestas = gestionEncuesta.findAll();

            String encuestadosJson = new Gson().toJson(encuestas);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("encuestas", encuestas);
            modelo.put("usuario", usuario);
            modelo.put("encuestadosJson", encuestadosJson);
            ctx.render("/templates/listar-encuestas.html", modelo);

        });

        // Ruta para mostrar el formulario de creación/edición de encuestas
        app.get("/encuestas/crear", ctx -> {
            ctx.render("/templates/formulario-encuesta.html");
        });

        // Ruta para procesar el formulario de creación/edición de encuestas
        app.post("/encuestas/crear", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null) {
                ctx.redirect("/login");
                return;
            }

            String nombre = ctx.formParam("nombre");
            String sector = ctx.formParam("sector");
            String nivelEscolar = ctx.formParam("nivelEscolar");
            double latitud = Double.parseDouble(ctx.formParam("latitud"));
            double longitud = Double.parseDouble(ctx.formParam("longitud"));

            Encuesta encuesta = new Encuesta(nombre, sector, nivelEscolar, latitud, longitud, usuario);
            gestionEncuesta.crear(encuesta);
            ctx.redirect("/");
        });

        // Ruta para mostrar el formulario de edición de encuestas
        app.get("/encuestas/editar/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null || usuario.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            long id = Long.parseLong(ctx.pathParam("id"));
            Encuesta encuesta = gestionEncuesta.find(id);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("encuesta", encuesta);
            modelo.put("usuario", usuario);
            ctx.render("/templates/formulario-encuesta.html", modelo);
        });

        // Ruta para procesar la edición de encuestas
        app.post("/encuestas/editar/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null || usuario.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            long id = Long.parseLong(ctx.pathParam("id"));
            Encuesta encuesta = gestionEncuesta.find(id);
            encuesta.setNombre(ctx.formParam("nombre"));
            encuesta.setSector(ctx.formParam("sector"));
            encuesta.setNivelEscolar(ctx.formParam("nivelEscolar"));
            encuesta.setLatitud(Double.parseDouble(ctx.formParam("latitud")));
            encuesta.setLongitud(Double.parseDouble(ctx.formParam("longitud")));
            gestionEncuesta.editar(encuesta);
            ctx.redirect("/");
        });

        // Ruta para eliminar encuestas
        app.post("/encuestas/eliminar/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null || usuario.getRol() != Roles.ADMIN) {
                throw new UnauthorizedResponse("No tienes permiso para acceder a esta página.");
            }

            long id = Long.parseLong(ctx.pathParam("id"));
            gestionEncuesta.eliminar(id);
            ctx.redirect("/");
        });
    }


}
