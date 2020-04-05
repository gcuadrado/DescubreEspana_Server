package rest;

import servicios.ServiciosFotos;
import utils.Constantes;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("fotos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestFotos {
    @Inject
    private ServiciosFotos serviciosFotos;

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Privado
    @AdminOnly
    @Path("/{id}")
    public Response borrarFoto(@PathParam("id") int id){
        Response response;
        serviciosFotos.delete(id);
        response = Response.ok(Constantes.OK).build();
        return response;
    }
}
