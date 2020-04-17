package dao;

import modelo.ServerException;
import modelo.entity.PuntoInteresEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PuntosInteresDao {
    private Session session;

    public List<PuntoInteresEntity> getAll() {
        List<PuntoInteresEntity> puntoInteresEntities = new ArrayList<>();
        try {
            session = HibernateUtil.getSession();
            //Cómo devolver los puntos ordenados por distancia al usuario
           /* Query query=session.createNativeQuery(" select * from punto_interes order by st_distance(POINT(latitud,longitud),POINT(40.328861116667696,-3.5165387596094604))",PuntoInteresEntity.class);
            puntoInteresEntities=(List<PuntoInteresEntity>)query.getResultList();*/
            Query query = session.createQuery("from PuntoInteresEntity p where p.activado=true");
            puntoInteresEntities = query.list();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return puntoInteresEntities;
    }


    public PuntoInteresEntity get(int id) throws ServerException {
        PuntoInteresEntity puntoInteresEntity = null;
        try {
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from PuntoInteresEntity where idPuntoInteres=:id");
            query.setParameter("id", id);
            puntoInteresEntity = (PuntoInteresEntity) query.uniqueResult();
            if (puntoInteresEntity == null) {
                throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No se encuentra este id en Base de datos");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (e instanceof ServerException) {
                throw e;
            } else {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            }
        } finally {
            session.close();
        }
        return puntoInteresEntity;
    }

    public PuntoInteresEntity save(PuntoInteresEntity poiEntity) {
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(poiEntity);
            session.getTransaction().commit();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return poiEntity;
    }

    public boolean update(PuntoInteresEntity poiEntity) {
        boolean updated = false;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.update(poiEntity);
            session.getTransaction().commit();
            updated = true;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return updated;
    }

    public List<PuntoInteresEntity> getAllSinActivar() {
        List<PuntoInteresEntity> puntoInteresEntities = new ArrayList<>();
        try {
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from PuntoInteresEntity p where p.activado=false");
            puntoInteresEntities = query.list();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return puntoInteresEntities;
    }

    public boolean activar(int id) {
        boolean activado = false;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("update PuntoInteresEntity p set p.activado=true where p.id=:id");
            query.setParameter("id", id);
            if (query.executeUpdate() > 0) {
                activado = true;
            } else {
                new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "No hay ningún punto con este id");
            }
            session.getTransaction().commit();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (e instanceof ServerException) {
                throw e;
            } else {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            }
        } finally {
            session.close();
        }
        return activado;
    }

    public boolean delete(int id) {
        boolean borrado = false;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query borrarValoraciones = session.createQuery("delete from ValoracionEntity v where v.puntoInteresByIdPuntoInteres.id=:id");
            borrarValoraciones.setParameter("id", id);
            borrarValoraciones.executeUpdate();

            Query borrarFotos = session.createQuery("delete from FotoPuntoInteresEntity f where f.puntoInteresByIdPuntoInteres.id=:id");
            borrarFotos.setParameter("id", id);
            borrarFotos.executeUpdate();

            Query borrarPunto = session.createQuery("delete from PuntoInteresEntity p where p.id=:id");
            borrarPunto.setParameter("id", id);
            if (borrarPunto.executeUpdate() > 0) {
                borrado = true;
            } else {
                new ServerException(HttpURLConnection.HTTP_NOT_FOUND, "No hay ningún punto con este id");
            }
            session.getTransaction().commit();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (e instanceof ServerException) {
                throw e;
            } else {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            }
        } finally {
            session.close();
        }
        return borrado;
    }
}
