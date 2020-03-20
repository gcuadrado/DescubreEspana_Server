package rest;

import modelo.dto.PuntoInteresDtoGetDetalle;
import modelo.dto.PuntoInteresDtoGetMaestro;
import servicios.ServiciosPuntoInteres;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("puntos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestPuntosInteres {
    @Inject
    private ServiciosPuntoInteres serviciosPuntoInteres;

    @GET
    public Response getPuntos() {
        Response response;
        List<PuntoInteresDtoGetMaestro> usuarios = serviciosPuntoInteres.getAll();
        response = Response.ok(usuarios).build();
        return response;
    }

    @GET
    @Path("/{id}")
    public Response getPuntoInteres(@PathParam("id") String id) {
        Response response;
        PuntoInteresDtoGetDetalle puntoInteresDtoGetDetalle = serviciosPuntoInteres.get(id);
        response = Response.ok(puntoInteresDtoGetDetalle).build();
        return response;
    }
}
