package edu.pucmm.eict.ormjpa.servicios;

import edu.pucmm.eict.ormjpa.entidades.Roles;
import edu.pucmm.eict.ormjpa.entidades.Usuario;
import jakarta.persistence.EntityManager;
import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by vacax on 07/06/17.
 */
public class BootStrapServices {

    private static BootStrapServices instancia;

    private BootStrapServices() {
    }


    public static BootStrapServices getInstancia(){
        if(instancia == null){
            instancia=new BootStrapServices();
        }
        return instancia;
    }

    public void startDb() {
        try {
            //Modo servidor H2.
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            //Abriendo el cliente web. El valor 0 representa puerto aleatorio.
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            //
            System.out.println("Status Web: "+status);
        }catch (SQLException ex){
            System.out.println("Problema con la base de datos: "+ex.getMessage());
        }
    }

    public void init(){
        startDb();
        crearUsuarioAdmin();
    }

    private void crearUsuarioAdmin() {
        GestionDb<Usuario> gestionDb = new GestionDb<>(Usuario.class);
        EntityManager em = gestionDb.getEntityManager();

        try {
            // Verificar si ya existe un usuario con el username "admin"
            Usuario admin = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", "admin")
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (admin == null) {
                // Crear el usuario admin si no existe
                em.getTransaction().begin();
                Usuario nuevoAdmin = new Usuario("admin", "admin", "admin", Roles.ADMIN);
                em.persist(nuevoAdmin);
                em.getTransaction().commit();
                System.out.println("Usuario admin creado exitosamente.");
            } else {
                System.out.println("El usuario admin ya existe.");
            }
        } finally {
            em.close();
        }
    }
}
