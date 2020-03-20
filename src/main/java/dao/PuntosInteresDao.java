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

    public List<PuntoInteresEntity> getAll(){
        List<PuntoInteresEntity> puntoInteresEntities = new ArrayList<>();
        try {
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from PuntoInteresEntity");
            puntoInteresEntities = query.list();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR,"Ha habido un error al acceder a la base de datos");
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
            query.setParameter("id",id);
            puntoInteresEntity = (PuntoInteresEntity) query.uniqueResult();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR,"Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return puntoInteresEntity;
    }
}
