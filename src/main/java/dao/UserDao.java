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
            if (e instanceof ConstraintViolationException) {
                throw new ServerException(HttpURLConnection.HTTP_CONFLICT, "Ya existe un usuario con este email");
            } else {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            }
        } finally {
            session.close();
        }

        return usuarioDtoGet;
    }


    public boolean delete(int userID) throws ServerException {
        boolean success = false;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("delete from UsuarioEntity u where u.idUsuario=:id");
            query.setParameter("id", userID);
            int result = query.executeUpdate();
            session.getTransaction().commit();
            if (result > 0) {
                success = true;
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if(session.getTransaction()!=null){
                session.getTransaction().rollback();
            }
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
        }finally {
            session.close();
        }
        return success;
    }


    /*
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

*/
         public UsuarioEntity getUserByEmail(String email) throws ServerException {
             UsuarioEntity usuarioEntity=null;
             try {
                 session=HibernateUtil.getSession();
                 session.beginTransaction();
                 Query query = session.createQuery("from UsuarioEntity u where u.email=:email");
                 query.setParameter("email",email);
                usuarioEntity= (UsuarioEntity)query.uniqueResult();
             } catch (Exception e) {
                 Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, e);
                 throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha ocurrido un error en nuestra base de datos");
             }
             return usuarioEntity;
         }
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
                if (Duration.between(usuarioEntity.getFechaRegistro(), LocalDateTime.now()).toMinutes() < tiempoActivacion) {
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
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (!(e instanceof ServerException)) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            } else {
                throw e;
            }
        } finally {
            session.close();
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
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (!(e instanceof ServerException)) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            } else {
                throw e;
            }
        } finally {
            session.close();
        }
        return success;
    }

    public boolean updateCodigoTimestamp(String email, String codigoActivacion) throws ServerException {
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
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (!(e instanceof ServerException)) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            } else {
                throw e;
            }
        } finally {
            session.close();
        }
        return success;
    }
}
