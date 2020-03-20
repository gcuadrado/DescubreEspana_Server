package servlets;

import modelo.ApiError;
import modelo.ServerException;
import org.apache.commons.mail.EmailException;
import servicios.ServiciosUsuario;
import utils.Constantes;
import utils.MandarMail;
import utils.ServerExceptionMapper;
import utils.Utils;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ReenvioActivacion", urlPatterns = {"/reactivar"})
public class ReenvioActivacion extends HttpServlet {
    @Inject
    private ServiciosUsuario serviciosUsuario;
    @Inject
    private MandarMail mandarMail;
    @Inject
    private Jsonb jsonb;
    @Inject
    private ServerExceptionMapper serverExceptionMapper;


    private void processrequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter(Constantes.EMAIL_PARAMETER);
        String respuesta;
            String newCode = Utils.randomBytes();
            try {
                serviciosUsuario.updateCodigoTimestamp(newCode, email);
                String baseUrl=request.getRequestURL().toString().substring(0,request.getRequestURL().indexOf(request.getServletPath()));
                mandarMail.mandarMail(email, "Pincha para activar tu cuenta: <a href=\""+baseUrl+"/activacion"+"?email=" + email + "&codigo_activacion=" + newCode + "\">aquí</a>", "Activación usuario");
                respuesta = Constantes.OK;
            } catch (EmailException e) {
                Logger.getLogger(getServletName()).log(Level.SEVERE, null, e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                respuesta = jsonb.toJson(new ApiError(HttpURLConnection.HTTP_INTERNAL_ERROR, "No hemos podido enviar el email"));
            } catch (ServerException se) {
                response.setStatus(se.getErrorCode());
                respuesta = jsonb.toJson(serverExceptionMapper.converterServerExceptionApiError(se));
            }
        response.getWriter().println(respuesta);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processrequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processrequest(req, resp);
    }


}
