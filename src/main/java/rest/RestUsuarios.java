package rest;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import modelo.ApiError;
import modelo.UserKeystore;
import modelo.dto.UsuarioDtoGet;
import modelo.dto.UsuarioDtoPost;
import org.apache.commons.mail.EmailException;
import seguridad.AlmacenarClavesServidor;
import servicios.ServiciosKeyStore;
import servicios.ServiciosUsuario;
import utils.MandarMail;
import utils.Utils;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.security.PrivateKey;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestUsuarios {
    @Inject
    private ServiciosUsuario serviciosUsuario;
    @Inject
    private MandarMail mandarMail;
    @Context
    ServletContext context;
    @Inject
    private ServiciosKeyStore serviciosKeyStore;
    @Inject
    private AlmacenarClavesServidor almacenarClavesServidor;


    @POST
    @Path("/login")
    public Response login(UsuarioDtoPost usuario, @Context HttpServletRequest request) {
        Response response;

        UsuarioDtoGet usuarioDtoGet = serviciosUsuario.checkCredenciales(usuario);
        PrivateKey privateKey = almacenarClavesServidor.leerClavePrivadaDeFichero(context.getResourceAsStream("/WEB-INF/jwt.privada"));
        String jws = Jwts.builder()
                .setIssuer("Servidor de usuarios")
                .setSubject("inicio de sesion")
                .claim("user", usuarioDtoGet)
                // NOW
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                // NEXT DAY
                .setExpiration(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault())
                        .toInstant()))
                .signWith(privateKey, SignatureAlgorithm.RS256
                )
                .compact();


        response = Response.ok(usuarioDtoGet).header("token", jws).build();

        return response;
    }

    @POST
    @Path("/registro")
    public Response registrarUsuario(UsuarioDtoPost usuario, @Context HttpServletRequest request) {
        Response response;
        String codgiActivacion = Utils.randomBytes();
        UsuarioDtoGet usuarioDtoGet = null;
        try {
            usuarioDtoGet = serviciosUsuario.save(usuario, codgiActivacion);
            String keystore = serviciosKeyStore.generarKeyStoreCliente(usuario, context);
            UserKeystore userKeystore = UserKeystore.builder()
                    .usuarioDtoGet(usuarioDtoGet)
                    .keystore(keystore)
                    .build();
            String baseUrl = request.getRequestURL().toString().substring(0, request.getRequestURL().indexOf(request.getServletPath()));
            mandarMail.mandarMail(usuario.getEmail(), "Pincha para activar tu cuenta: <a href=\"" + baseUrl + "/activacion" + "?email=" + usuario.getEmail() + "&codigo_activacion=" + codgiActivacion + "\">aquí</a>", "Activación usuario");
            response = Response.status(Response.Status.CREATED).entity(usuarioDtoGet).build();
        } catch (EmailException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (usuarioDtoGet != null) {
                serviciosUsuario.delete(String.valueOf(usuarioDtoGet.getIdUsuario()));
            }
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiError(HttpURLConnection.HTTP_INTERNAL_ERROR, "No se ha podido enviar el email")).build();
        }
        return response;
    }

    /*@GET
    @Privado
    public Response getUsuarios() {
        Response response;
        List<UsuarioDTO> usuarios = serviciosUsuario.getAll();
        response = Response.ok(usuarios).build();
        return response;
    }

    @GET
    @Privado
    @Path("/id")
    public Response getUsuario(@QueryParam("userId") String id) {
        Response response;
        UsuarioDTO u = serviciosUsuario.get(id);
        response = Response.ok(u).build();
        return response;
    }

    @DELETE
    @Privado
    @Produces(MediaType.TEXT_PLAIN)
    public Response borrarUsuario(@QueryParam("id") String id) {
        Response response;
        serviciosUsuario.delete(id);
        response = Response.ok(Constantes.OK).build();
        return response;
    }

    @PUT
    @Privado
    public Response actualizarUsuario(UsuarioDtoPost usuario) {
        Response response;
        serviciosUsuario.update(usuario);
        response = Response.ok(Constantes.OK).build();
        return response;
    }

    @GET
    @Path("/publickey/{id}")
    public Response getPublicKey(@PathParam("id") String id) {
        Response response;
        String publicKey = serviciosUsuario.getPublicKey(id);
        response = Response.ok(publicKey).build();
        return response;
    }*/
}
