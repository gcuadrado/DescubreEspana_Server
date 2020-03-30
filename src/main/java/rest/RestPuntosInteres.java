package rest;

import modelo.dto.PuntoInteresDtoGetDetalle;
import modelo.dto.PuntoInteresDtoGetMaestro;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import servicios.ServiciosPuntoInteres;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addPuntoInteres(FormDataMultiPart multiPart) throws IOException {

      InputStream inputStream=  ((BodyPartEntity)multiPart.getField("image").getEntity()).getInputStream();
        FileOutputStream fos=new FileOutputStream("C:\\Tools\\payara5\\glassfish\\domains\\domain1\\docroot\\uploads\\images.jpeg");
        byte[] bytes = new byte[1024];
        int read;
        while ((read = inputStream.read(bytes)) != -1) {
            fos.write(bytes, 0, read);
        }
       fos.close();
        return Response.ok().build();
    }
}
