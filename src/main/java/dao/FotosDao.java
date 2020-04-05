package dao;

import modelo.ServerException;
import modelo.entity.FotoPuntoInteresEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FotosDao {
    private Session session;

    public FotoPuntoInteresEntity insert(FotoPuntoInteresEntity foto) {
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(foto);
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
        return foto;
    }

    public FotoPuntoInteresEntity get(int id) {
        FotoPuntoInteresEntity foto = null;
        try {
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from FotoPuntoInteresEntity  f where f.id=:id");
            query.setParameter("id", id);
            foto = (FotoPuntoInteresEntity) query.uniqueResult();
            if(foto==null){
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
        return foto;
    }

    public boolean delete(int id) {
        boolean borrado = false;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("delete from FotoPuntoInteresEntity f where f.id=:id");
            query.setParameter("id", id);
            if (query.executeUpdate() > 0) {
                borrado = true;
            } else {
                throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "No se encuentra este id en la Base de datos");
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
