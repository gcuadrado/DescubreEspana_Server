package rest;

import modelo.ApiError;
import modelo.dto.UsuarioDtoGet;
import seguridad.AlmacenarClavesServidor;
import utils.Constantes;

import javax.annotation.Priority;
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

@Provider
@Privado
@Priority(2)
public class JAXAdminFilter implements ContainerRequestFilter {


    @Context
    private HttpServletRequest httpServletRequest;
    @Context
    private ServletContext context;
    @Inject
    private AlmacenarClavesServidor almacenarClavesServidor;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        UsuarioDtoGet usuarioDtoGet = (UsuarioDtoGet) httpServletRequest.getAttribute(Constantes.CURRENT_USER);
        if (usuarioDtoGet == null || usuarioDtoGet.getTipoUsuario() != Constantes.ADMIN_USER) {
            containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(new ApiError(HttpURLConnection.HTTP_FORBIDDEN, "No autorizado")).build());
        }

    }
}
