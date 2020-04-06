package rest;

import modelo.dto.FotoPuntoInteresDtoGet;
import modelo.dto.PuntoInteresDtoGetDetalle;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import servicios.ServiciosFotos;
import utils.Constantes;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("fotos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestFotos {
    @Inject
    private ServiciosFotos serviciosFotos;
    @Inject
    private Jsonb jsonb;

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

    @Privado
    @AdminOnly
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addFoto(FormDataMultiPart multiPart) throws IOException {
        //Cogemos el poi desde el form
        PuntoInteresDtoGetDetalle poi = jsonb.fromJson(((BodyPartEntity) multiPart.getField("data").getEntity()).getInputStream(), PuntoInteresDtoGetDetalle.class);
        //Cogemos la lista de imágenes desde el form
        List<FormDataBodyPart> imagenes = multiPart.getFields("image");
        //Insertamos el punto y sus imágenes
        List<FotoPuntoInteresDtoGet> fotos = serviciosFotos.insertFoto(imagenes, poi.getUuidFolderFilename(), poi.getIdPuntoInteres());
        //Devolvemos el punto ya insertado
        return Response.status(Response.Status.CREATED).entity(fotos).build();
    }
}
