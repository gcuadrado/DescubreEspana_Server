package rest;

import modelo.dto.PuntoInteresDtoGetDetalle;
import modelo.dto.PuntoInteresDtoGetMaestro;
import modelo.dto.UsuarioDtoGet;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import servicios.ServiciosFotos;
import servicios.ServiciosPuntoInteres;
import utils.Constantes;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("puntos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestPuntosInteres {
    @Inject
    private ServiciosPuntoInteres serviciosPuntoInteres;
    @Inject
    private ServiciosFotos serviciosFotos;
    @Context
    private ServletContext servletContext;
    @Inject
    private Jsonb jsonb;
    @Context
    private HttpServletRequest httpServletRequest;


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

    @Privado
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addPuntoInteres(FormDataMultiPart multiPart) throws IOException {
        UsuarioDtoGet usuarioDtoGet= (UsuarioDtoGet) httpServletRequest.getAttribute(Constantes.CURRENT_USER);
        PuntoInteresDtoGetDetalle poi = jsonb.fromJson(((BodyPartEntity) multiPart.getField("data").getEntity()).getInputStream(), PuntoInteresDtoGetDetalle.class);

        poi=serviciosPuntoInteres.insert(poi,usuarioDtoGet);
        List<FormDataBodyPart> imagenes=multiPart.getFields("image");
        serviciosFotos.insertFoto(imagenes,poi.getIdPuntoInteres());





        return Response.status(Response.Status.CREATED).entity(poi).build();
    }
}
