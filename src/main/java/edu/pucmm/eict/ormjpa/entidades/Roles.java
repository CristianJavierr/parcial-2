package edu.pucmm.eict.ormjpa.entidades;

import io.javalin.security.RouteRole;

public enum Roles implements RouteRole {
    USUARIO_REGULAR,
    ADMIN,


}
