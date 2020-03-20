/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import config.Configuration;
import modelo.ServerException;
import modelo.dto.UsuarioDtoGet;
import modelo.entity.UsuarioEntity;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.modelmapper.ModelMapper;
import utils.Constantes;
import utils.HibernateUtil;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dam2
 */
public class UserDao {
    private Session session;
    @Inject
    private ModelMapper modelMapper;

    public UsuarioDtoGet save(UsuarioEntity usuario, String codigoActivacion) throws ServerException {
        UsuarioDtoGet usuarioDtoGet = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(usuario);
            session.getTransaction().commit();

            usuarioDtoGet = modelMapper.map(usuario, UsuarioDtoGet.class);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ServerException(HttpURLConnection.HTTP_CONFLICT, "Ya existe un usuario con este email");
            } else {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            }
        } finally {
            session.close();
        }

        return usuarioDtoGet;
    }


    /* public boolean delete(int userID) throws ServerException {
         boolean success = false;
         JdbcTemplate jtm = new JdbcTemplate(
                 db.getDataSource());
         try {

             int result = jtm.update(SQLStatements.DELETE_USER, userID);
             if (result > 0) {
                 success = true;
             }
         } catch (DataAccessException e) {
             Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
             throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
         }
         return success;
     }



     public Usuario getUsuario(int id) throws ServerException {
         JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
         Usuario usuario = null;
         try {
             usuario = jtm.queryForObject(SQLStatements.SELECT_USER, BeanPropertyRowMapper.newInstance(Usuario.class), id);
         } catch (DataAccessException ex) {
             Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
             throw new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "No se ha encontrado ningún usuario con este ID");
         }
         return usuario;
     }


     public String getCorrectHash(String username) throws ServerException {
         String hash = "";
         JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
         try {
             hash = jtm.queryForObject(SQLStatements.GET_HASH_OF_USER, String.class, username);
         } catch (DataAccessException ex) {
             Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
             throw new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "El usuario o la contraseña son incorrectos");
         }
         return hash;
     }
 */
    public int activarCuenta(String email, String codigoActivacion) {
        int success = Constantes.FAIL;
        Integer tiempoActivacion = Configuration.getInstance().getTiempoActivacion();
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("from UsuarioEntity where email = :email and codigoActivacion=:codigo");
            query.setParameter("email", email);
            query.setParameter("codigo", codigoActivacion);
            UsuarioEntity usuarioEntity = (UsuarioEntity) query.uniqueResult();
            if (usuarioEntity != null) {
                if (Duration.between(usuarioEntity.getFechaRegistro(), LocalDateTime.now()).getSeconds() < tiempoActivacion) {
                    usuarioEntity.setActivado(true);
                    session.getTransaction().commit();
                    success = Constantes.SUCCESS;
                } else {
                    success = Constantes.TIEMPO_ACTIVACION_EXPIRADO;
                }
            } else {
                throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No hay ningún usuario con este email");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (!(e instanceof ServerException)) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            } else {
                throw e;
            }
        }
        return success;
    }


    public int reestablecerPassword(String email, String newPassword) {
        int success = Constantes.FAIL;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("from UsuarioEntity where email = :email");
            query.setParameter("email", email);
            UsuarioEntity usuarioEntity = (UsuarioEntity) query.uniqueResult();
            if (usuarioEntity != null) {
                usuarioEntity.setPassword(newPassword);
                session.save(usuarioEntity);
                session.getTransaction().commit();
            } else {
                throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No hay ningún usuario con este email");
            }

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (!(e instanceof ServerException)) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            } else {
                throw e;
            }
        }
        return success;
    }

    /*
        public boolean comprobarCuentaActivada(String email) throws ServerException {
            boolean acitvada = false;
            JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
            try {
                Integer activado = jtm.queryForObject(SQLStatements.COMPROBAR_CUENTA_ACTIVADA, Integer.class, email);
                if (activado == 1) {
                    acitvada = true;
                }
            } catch (DataAccessException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                throw new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "Esta dirección no existe en nuestra base de datos");
            }
            return acitvada;
        }

    */
    public boolean updateCodigoTimestamp(String codigoActivacion, String email) throws ServerException {
        boolean success = false;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("from UsuarioEntity where email = :email");
            query.setParameter("email", email);
            UsuarioEntity usuarioEntity = (UsuarioEntity) query.uniqueResult();
            if (usuarioEntity != null) {
                if (!usuarioEntity.getActivado()) {
                    usuarioEntity.setCodigoActivacion(codigoActivacion);
                    usuarioEntity.setFechaRegistro(LocalDateTime.now());
                    session.getTransaction().commit();
                } else {
                    throw new ServerException(HttpURLConnection.HTTP_CONFLICT, "Esta cuenta ya ha sido activada");
                }
            } else {
                throw new ServerException(HttpURLConnection.HTTP_CONFLICT, "No hay ninguna cuenta asociada a este email");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (!(e instanceof ServerException)) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            } else {
                throw e;
            }
        }
        return success;
    }

    /*

    public String getPublicKey(Integer id) {
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        String publicKey = null;
        try {
            publicKey = jtm.queryForObject(SQLStatements.SELECT_PUBLIC_KEY,String.class, id);
        } catch (DataAccessException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "No se ha encontrado ningún usuario con este ID");
        }
        return  publicKey;
    }*/
}
