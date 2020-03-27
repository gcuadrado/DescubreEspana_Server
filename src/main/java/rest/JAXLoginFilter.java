package rest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import modelo.ApiError;
import modelo.dto.UsuarioDtoGet;
import seguridad.AlmacenarClavesServidor;
import utils.Constantes;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.PublicKey;


@Provider
@Privado
public class JAXLoginFilter implements ContainerRequestFilter {


    @Context
    private HttpServletRequest httpServletRequest;
    @Context
    private ServletContext context;
    @Inject
    private AlmacenarClavesServidor almacenarClavesServidor;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String jwsString = httpServletRequest.getHeader("token");
        Jws<Claims> jws = null;
        PublicKey publicKey = almacenarClavesServidor.leerClavePublicaDeFichero(context.getResourceAsStream("/WEB-INF/jwt.publica"));
        try {
            jws = Jwts.parserBuilder() // (1)
                    .deserializeJsonWith(new JacksonDeserializer(Maps.of("user", UsuarioDtoGet.class).build())) // <-----
                    .setSigningKey(publicKey) // (2)
                    .build()
                    .parseClaimsJws(jwsString); // (3)

            UsuarioDtoGet userLogged = jws.getBody().get("user", UsuarioDtoGet.class);
            if (userLogged != null) {
                httpServletRequest.setAttribute(Constantes.CURRENT_USER, userLogged);
            } else {
                containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(new ApiError(HttpURLConnection.HTTP_FORBIDDEN, "No se encuentra el usuario")).build());
            }

            // we can safely trust the JWT
        } catch (Exception ex) {       // (4)
            containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(new ApiError(HttpURLConnection.HTTP_FORBIDDEN, ex.getMessage())).build());
        }

    }
}
