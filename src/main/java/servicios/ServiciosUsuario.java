package servicios;

import dao.UserDao;
import modelo.ServerException;
import modelo.dto.UsuarioDtoGet;
import modelo.dto.UsuarioDtoPost;
import modelo.entity.UsuarioEntity;
import utils.PasswordHash;
import utils.Validacion;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiciosUsuario {
    @Inject
    private UserDao userDao;
    @Inject
    private PasswordHash passwordHash;
    @Inject
    private Validacion validacion;


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


   /* public int update(UsuarioDtoPost newUsuario) throws ServerException {
        int modified = Constantes.FAIL;
        String password = null;
        String erroresValidacion = validacion.validarObjeto(newUsuario);
        if (erroresValidacion.length() == 0) {
            try {
                password = passwordHash.createHash(newUsuario.getPassword());
                newUsuario.setPassword(password);
                modified = userDao.update(newUsuario);
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
        return modified;
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

    public List<UsuarioDTO> getAll() throws ServerException {
        return userDao.getAll().stream()
                .map(usuario -> usuarioConverter.converterUserUserDTO(usuario)).collect(Collectors.toList());

    }

    public UsuarioDTO get(String userID) throws ServerException {
        UsuarioDTO usuario = null;
        Integer id = Ints.tryParse(userID);
        if (id != null) {
            usuario = usuarioConverter.converterUserUserDTO(userDao.getUsuario(id));
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "El ID no es un número");
        }
        return usuario;
    }

    public UsuarioDTO checkCredenciales(UsuarioDtoPost usuario) throws ServerException {
        UsuarioDTO usuarioDTO = null;
        String erroresValidacion = validacion.validarObjeto(usuario);
        if (erroresValidacion.length() == 0) {
            try {
                String correctHash = userDao.getCorrectHash(usuario.getEmail());
                if (!correctHash.isEmpty() && passwordHash.validatePassword(usuario.getPassword(), correctHash)) {
                    if (userDao.comprobarCuentaActivada(usuario.getEmail())) {
                        usuarioDTO=usuarioConverter.converterUserUserDTO(userDao.getUsuarioByEmail(usuario.getEmail()));
                        if(usuarioDTO==null){
                            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
                        }

                    } else {
                        throw new ServerException(HttpURLConnection.HTTP_FORBIDDEN, "La cuenta no está activada");
                    }
                } else {
                    throw new ServerException(HttpURLConnection.HTTP_UNAUTHORIZED, "La contraseña no es correcta");
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

        return usuarioDTO;
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

    public String getPublicKey(String userId){
        Integer id = Ints.tryParse(userId);
        if (id != null) {
           return userDao.getPublicKey(id);
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "El ID no es un número");
        }
    }*/


}
