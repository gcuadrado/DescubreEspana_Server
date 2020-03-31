package rest;

import modelo.dto.PuntoInteresDtoGetDetalle;
import modelo.dto.PuntoInteresDtoGetMaestro;
import modelo.dto.UsuarioDtoGet;
import modelo.entity.FotoPuntoInteresEntity;
import modelo.entity.PuntoInteresEntity;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        PuntoInteresDtoGetDetalle finalPoi = poi;
       List<FotoPuntoInteresEntity> fotos= IntStream.range(0,imagenes.size()).mapToObj(i -> {
            InputStream inputStream = ((BodyPartEntity) imagenes.get(i).getEntity()).getInputStream();
            String path=File.separator+"uploads"+File.separator+String.valueOf(finalPoi.getIdPuntoInteres())+File.separator+i+".jpeg";
            try {
                File file = new File(System.getProperty("catalina.base") + File.separator + "docroot" + path);
                if(file.getParent()!=null && !file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int read;
                while ((read = inputStream.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }
                fos.close();
            }catch (Exception e){
                e.toString();
            }

            FotoPuntoInteresEntity foto=new FotoPuntoInteresEntity();
            foto.setPath(path);
            PuntoInteresEntity p=new PuntoInteresEntity();
            p.setIdPuntoInteres(finalPoi.getIdPuntoInteres());
            foto.setPuntoInteresByIdPuntoInteres(p);
            return foto;
        }).collect(Collectors.toList());




        return Response.status(Response.Status.CREATED).entity(poi).build();
    }
}
