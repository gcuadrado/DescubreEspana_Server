package utils;

import java.io.File;

public class Constantes {
    public static final String EMAIL_PARAMETER = "email";
    public static final String OK = "ok";
    public static final int FAIL = -1;
    public static final String CODIGO_ACTIVACION_PARAMETER = "codigo_activacion";
    public static final int SUCCESS = 1;
    public static final int TIEMPO_ACTIVACION_EXPIRADO = 2;
    public static final String CURRENT_USER = "currentUser";
    public static final int ADMIN_USER=2;
    public static final int STANDARD_USER=1;
    public static final String PATH_DOCROOT = System.getProperty("catalina.base") + File.separator + "docroot";
    public static final String PATH_POI_FOLDER=PATH_DOCROOT+ File.separator + "uploads" + File.separator;
}
