package servlets;

import modelo.dto.UsuarioDtoGet;
import modelo.dto.UsuarioDtoPost;
import org.apache.commons.mail.EmailException;
import servicios.ServiciosUsuario;
import utils.MandarMail;
import utils.Utils;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "Registro")
public class Registro extends HttpServlet {
    @Inject
    private Jsonb jsonb;
    @Inject
    private ServiciosUsuario serviciosUsuario;
    @Inject
    private MandarMail mandarMail;

    private void processRequest(HttpServletRequest request, HttpServletResponse response){
        String codgiActivacion = Utils.randomBytes();
        UsuarioDtoPost usuario=null;
        String respuesta;
        try {
            jsonb.fromJson(request.getReader(), UsuarioDtoPost.class);
            UsuarioDtoGet usuarioDtoGet = serviciosUsuario.save(usuario, codgiActivacion);
            mandarMail.mandarMail(usuario.getEmail(), "Pincha para activar tu cuenta: <a href=\"http://localhost:8080/servidorGestionUsuarios/activacion?email=" + usuario.getEmail() + "&codigo_activacion=" + codgiActivacion + "\">aquí</a>", "Activación usuario");
            response = Response.status(Response.Status.CREATED).entity(usuarioDtoGet).build();
            respuesta=jsonb.toJson(usuarioDtoGet);
            response.se
        } catch (EmailException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiError(HttpURLConnection.HTTP_INTERNAL_ERROR, "No se ha podido enviar el email")).build();
        } catch (Exception e){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error al leer el usuario")).build();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }
}
