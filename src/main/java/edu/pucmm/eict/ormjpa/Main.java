package edu.pucmm.eict.ormjpa;

import edu.pucmm.eict.ormjpa.controladores.EncuestaController;
import edu.pucmm.eict.ormjpa.controladores.UserController;
import edu.pucmm.eict.ormjpa.servicios.BootStrapServices;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.security.RouteRole;
import edu.pucmm.eict.ormjpa.entidades.Usuario;
import edu.pucmm.eict.ormjpa.entidades.Roles;


public class Main {

    //indica el modo de operacion para la base de datos.
    private static String modoConexion = "";

    enum Rules implements RouteRole {
        ANONYMOUS,
        USER,
    }

    public static void main(String[] args) {
        String mensaje = "Software ORM - JPA";
        System.out.println(mensaje);
        if(args.length >= 1){
            modoConexion = args[0];
            System.out.println("Modo de Operacion: "+modoConexion);
        }

        //Iniciando la base de datos.
        if(modoConexion.isEmpty()) {
            BootStrapServices.getInstancia().init();
        }




        Javalin app = Javalin.create(config -> {


            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });



            config.fileRenderer(new JavalinThymeleaf());


        }).start(7000);

        new UserController().route(app);
        new EncuestaController().route(app);

        app.before(ctx -> {
            String path = ctx.path();

            // ✅ Permitir archivos del Service Worker y estáticos sin restricciones
            if (path.equals("/login") ||
                    path.equals("/Registro") ||
                    path.equals("/service-worker.js") ||
                    path.startsWith("/css/") ||
                    path.startsWith("/js/") ||
                    path.startsWith("/publico/") ||
                    path.startsWith("/static/") ||
                    path.equals("/manifest.json")) {
                return; // No bloqueamos estos archivos
            }

            // ✅ Validar sesión solo para rutas protegidas
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null) {
                ctx.redirect("/login");
            }
        });


    }

    /**
     * Metodo para indicar el puerto en Heroku
     * @return
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 7000; //Retorna el puerto por defecto en caso de no estar en Heroku.
    }

    /**
     * Nos
     * @return
     */
    public static String getModoConexion(){
        return modoConexion;
    }
}
