package servicios;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import dao.UserDao;
import modelo.ServerException;
import modelo.dto.UsuarioDtoGet;
import modelo.dto.UsuarioDtoPost;
import modelo.entity.UsuarioEntity;
import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import utils.Constantes;
import utils.PasswordHash;
import utils.ValidacionTool;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiciosUsuario {
    @Inject
    private UserDao userDao;
    @Inject
    private PasswordHash passwordHash;
    @Inject
    private ValidacionTool validacion;
    @Inject
    private ModelMapper modelMapper;


    public UsuarioDtoGet save(UsuarioDtoPost usuario, String codigoActivacion) throws ServerException {
        String erroresValidacion = validacion.validarObjeto(usuario);
        UsuarioDtoGet usuarioRegistrado=null;
        if (erroresValidacion.length() == 0) {
            try {
               String password = passwordHash.createHash(usuario.getPassword());

                UsuarioEntity usuarioEntity=new UsuarioEntity();
                usuarioEntity.setEmail(usuario.getEmail());
                usuarioEntity.setPassword(password);
                usuarioEntity.setCodigoActivacion(codigoActivacion);
                usuarioEntity.setActivado(false);
                usuarioEntity.setTipoUsuario(Constantes.STANDARD_USER);
                usuarioEntity.setFechaRegistro(LocalDateTime.now());

                usuarioRegistrado = userDao.save(usuarioEntity, codigoActivacion);

            } catch (NoSuchAlgorithmException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
            } catch (InvalidKeySpecException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
            }
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, erroresValidacion);
        }
        return usuarioRegistrado;
    }



    public boolean delete(String userID) throws ServerException {
        boolean borrado = false;
        Integer id = Ints.tryParse(userID);
        if (id != null) {
            borrado = userDao.delete(id);
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "El ID no es un número");
        }
        return borrado;
    }



    public UsuarioDtoGet checkCredenciales(UsuarioDtoPost usuario) throws ServerException {
        UsuarioDtoGet usuarioDtoGet = null;
        String erroresValidacion = validacion.validarObjeto(usuario);
        if (erroresValidacion.length() == 0) {
            try {
                UsuarioEntity usuarioEntity = userDao.getUserByEmail(usuario.getEmail());
                if(usuarioEntity!=null){
                    if(passwordHash.validatePassword(usuario.getPassword(), usuarioEntity.getPassword())){
                        if(usuarioEntity.getActivado()){
                            usuarioDtoGet=modelMapper.map(usuarioEntity,UsuarioDtoGet.class);
                        }else{
                            throw new ServerException(HttpURLConnection.HTTP_FORBIDDEN, "La cuenta no está activada");
                        }
                    }else{
                        throw new ServerException(HttpURLConnection.HTTP_UNAUTHORIZED, "La contraseña no es correcta");
                    }
                }else{
                    throw new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "El usuario o la contraseña son incorrectos");
                }
            } catch (NoSuchAlgorithmException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
            } catch (InvalidKeySpecException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
            }
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, erroresValidacion);
        }

        return usuarioDtoGet;
    }


    public int activarCuenta(String email, String codigoActivacion) throws ServerException {
        int result;
        if(!Strings.isNullOrEmpty(email) && EmailValidator.getInstance().isValid(email)){
            result=userDao.activarCuenta(email, codigoActivacion);
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No es un email válido");
        }
        return result;
    }


    public String reestablecerPassword(String email) throws ServerException {
        Faker faker = new Faker();
        String newPassword = faker.pokemon().name();
        if(!Strings.isNullOrEmpty(email) && EmailValidator.getInstance().isValid(email)) {
            try {
                userDao.reestablecerPassword(email, passwordHash.createHash(newPassword));
            } catch (NoSuchAlgorithmException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
            } catch (InvalidKeySpecException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
            }
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No es un email válido");
        }
        return newPassword;
    }


    public boolean updateCodigoTimestamp(String codigoActivacion, String email) throws ServerException {
        boolean result;
        if(!Strings.isNullOrEmpty(email) && EmailValidator.getInstance().isValid(email)){
            result=userDao.updateCodigoTimestamp(email, codigoActivacion);
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No es un email válido");
        }
        return result;
    }




}
