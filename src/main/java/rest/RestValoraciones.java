package rest;

import modelo.dto.UsuarioDtoGet;
import modelo.dto.ValoracionDto;
import servicios.ServiciosValoraciones;
import utils.Constantes;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("valoraciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestValoraciones {
    @Inject
    private ServiciosValoraciones serviciosValoraciones;
    @Context
    private HttpServletRequest httpServletRequest;


    @GET
    public Response getValoraciones(@QueryParam("poi_id") int id) {
        Response response;
        List<ValoracionDto> usuarios = serviciosValoraciones.getAll(id);
        response = Response.ok(usuarios).build();
        return response;
    }

    @Privado
    @POST
    public Response guardarValoracion(ValoracionDto valoracionDto) {
        Response response;
        UsuarioDtoGet usuarioDtoGet= (UsuarioDtoGet) httpServletRequest.getAttribute(Constantes.CURRENT_USER);
        ValoracionDto valoracionDtoInsertado = serviciosValoraciones.save(valoracionDto,usuarioDtoGet);
        response = Response.status(Response.Status.CREATED).entity(valoracionDtoInsertado).build();
        return response;
    }

    @Privado
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response borrarValoracion(@QueryParam("id") String id) {
        Response response;
        UsuarioDtoGet usuarioDtoGet= (UsuarioDtoGet) httpServletRequest.getAttribute(Constantes.CURRENT_USER);
        serviciosValoraciones.delete(id,usuarioDtoGet);
        response = Response.ok(Constantes.OK).build();
        return response;
    }
}
