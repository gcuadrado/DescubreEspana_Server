package servlets;

import servicios.ServiciosUsuario;
import utils.Constantes;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ActivacionServlet", urlPatterns = {"/activacion"})
public class ActivacionServlet extends HttpServlet {
    @Inject
    private ServiciosUsuario serviciosUsuario;

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter(Constantes.EMAIL_PARAMETER);
      /*  String codigoActivacion = request.getParameter(Constantes.CODIGO_ACTIVACION_PARAMETER);
        try {
            int result = serviciosUsuario.activarCuenta(email, codigoActivacion);
            if (result == Constantes.SUCCESS) {
                request.getRequestDispatcher("/cuenta-activada.html").forward(request, response);
            } else if (result == Constantes.TIEMPO_ACTIVACION_EXPIRADO) {
                request.setAttribute("emailValidacion", email);
                request.getRequestDispatcher("/error-timeout.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/error-activacion.html").forward(request, response);
            }
        } catch (ServerException se) {
            request.getRequestDispatcher("/error-activacion.html").forward(request, response);
        }*/
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
