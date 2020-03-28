package dao;

import modelo.ServerException;
import modelo.dto.ValoracionDto;
import modelo.entity.ValoracionEntity;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.modelmapper.ModelMapper;
import utils.HibernateUtil;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValoracionDao {
    private Session session;
    @Inject
    private ModelMapper modelMapper;

    public List<ValoracionEntity> getAll(int id) {
        List<ValoracionEntity> valoracionEntities = new ArrayList<>();
        try {
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from ValoracionEntity v where v.puntoInteresByIdPuntoInteres.id=:id");
            query.setParameter("id", id);
            valoracionEntities = query.list();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
        } finally {
            session.close();
        }
        return valoracionEntities;
    }

    public ValoracionDto save(ValoracionEntity valoracionEntity) throws ServerException {
        ValoracionDto valoracionDto = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(valoracionEntity);

            //Se hace la media de todas las valoraciones de ese punto de interés y se actualiza su puntuación
            Query query = session.createQuery("update PuntoInteresEntity p set p.puntuacion=(select avg(v.puntuacion) from ValoracionEntity v where v.puntoInteresByIdPuntoInteres.id=:idPoi) where p.id=:idPoi");
            query.setParameter("idPoi", valoracionEntity.getPuntoInteresByIdPuntoInteres().getIdPuntoInteres());
            query.executeUpdate();
            session.getTransaction().commit();

            valoracionDto = modelMapper.map(valoracionEntity, ValoracionDto.class);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            if (e instanceof ConstraintViolationException) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ya has publicado una valoración para este punto de interés");
            } else {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Ha habido un error al acceder a la base de datos");
            }
        } finally {
            session.close();
        }

        return valoracionDto;
    }
}
