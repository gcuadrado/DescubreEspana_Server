package rest;

import modelo.dto.PuntoInteresDtoGetDetalle;
import modelo.dto.PuntoInteresDtoGetMaestro;
import modelo.dto.UsuarioDtoGet;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
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
    @Path("/cercanos")
    public Response getPuntosCercanos(@QueryParam("latitud") double latitud,@QueryParam("longitud") double longitud) {
        Response response;
        List<PuntoInteresDtoGetMaestro> usuarios = serviciosPuntoInteres.getAllCercanos(latitud,longitud);
        response = Response.ok(usuarios).build();
        return response;
    }

    @GET
    @Privado
    @AdminOnly
    @Path("/administrar")
    public Response getPuntosSinActivar() {
        Response response;
        List<PuntoInteresDtoGetMaestro> usuarios = serviciosPuntoInteres.getAllSinActivar();
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

    @PUT
    @Privado
    @AdminOnly
    @Path("/administrar")
    public Response actualizarPunto(PuntoInteresDtoGetDetalle puntoInteresDtoGetDetalle) {
        Response response;
        serviciosPuntoInteres.updatePoi(puntoInteresDtoGetDetalle);
        response = Response.ok(Constantes.OK).build();
        return response;
    }

    @PUT
    @Privado
    @AdminOnly
    @Path("/administrar/{id}")
    public Response activarPunto(@PathParam("id") int id) {
        Response response;
         serviciosPuntoInteres.activar(id);
        response = Response.ok(Constantes.OK).build();
        return response;
    }

    @DELETE
    @Privado
    @AdminOnly
    @Path("/administrar/{id}")
    public Response borrarPunto(@PathParam("id") int id) {
        Response response;
        serviciosPuntoInteres.borrarPunto(id);
        response = Response.ok(Constantes.OK).build();
        return response;
    }


    @Privado
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addPuntoInteres(FormDataMultiPart multiPart) throws IOException {
        //Cogemos el usuario actual en sesión
        UsuarioDtoGet usuarioDtoGet = (UsuarioDtoGet) httpServletRequest.getAttribute(Constantes.CURRENT_USER);
        //Cogemos el poi desde el form
        PuntoInteresDtoGetDetalle poi = jsonb.fromJson(((BodyPartEntity) multiPart.getField("data").getEntity()).getInputStream(), PuntoInteresDtoGetDetalle.class);
        //Cogemos la imagen principal desde el form
        FormDataBodyPart imagenPrincipal=multiPart.getField("imagenPrincipal");
        //Cogemos la lista de imágenes desde el form
        List<FormDataBodyPart> imagenes = multiPart.getFields("image");
        //Insertamos el punto y sus imágenes
        poi = serviciosPuntoInteres.insert(poi, usuarioDtoGet, imagenPrincipal, imagenes);
        //Devolvemos el punto ya insertado
        return Response.status(Response.Status.CREATED).entity(poi).build();
    }
}
