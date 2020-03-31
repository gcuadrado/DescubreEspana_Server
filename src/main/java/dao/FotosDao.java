package dao;

import modelo.ServerException;
import modelo.entity.FotoPuntoInteresEntity;
import org.hibernate.Session;
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
            if(session.getTransaction()!=null){
                session.getTransaction().rollback();
            }
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR,"Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return  foto;
    }
}
