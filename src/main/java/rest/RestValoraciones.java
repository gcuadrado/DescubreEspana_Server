package rest;

import modelo.dto.ValoracionDto;
import servicios.ServiciosValoraciones;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("valoraciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestValoraciones {
    @Inject
    private ServiciosValoraciones serviciosValoraciones;
    @GET
    public Response getValoraciones() {
        Response response;
        List<ValoracionDto> usuarios = serviciosValoraciones.getAll();
        response = Response.ok(usuarios).build();
        return response;
    }

    @POST
    public Response guardarValoracion(ValoracionDto valoracionDto) {
        Response response;
        ValoracionDto valoracionDtoInsertado = serviciosValoraciones.save(valoracionDto);
        response = Response.status(Response.Status.CREATED).entity(valoracionDtoInsertado).build();
        return response;
    }
}
