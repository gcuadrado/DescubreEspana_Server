package servicios;

import com.google.common.primitives.Ints;
import dao.PuntosInteresDao;
import modelo.ServerException;
import modelo.dto.PuntoInteresDtoGetDetalle;
import modelo.dto.PuntoInteresDtoGetMaestro;
import modelo.dto.UsuarioDtoGet;
import modelo.entity.PuntoInteresEntity;
import modelo.entity.UsuarioEntity;
import org.modelmapper.ModelMapper;
import utils.ValidacionTool;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class ServiciosPuntoInteres {
    @Inject
    private PuntosInteresDao puntosInteresDao;
    @Inject
    private ModelMapper modelMapper;
    @Inject
    private ValidacionTool validacionTool;
    @Inject
    private ServiciosFotos serviciosFotos;

    public List<PuntoInteresDtoGetMaestro> getAll() throws ServerException {
        return puntosInteresDao.getAll().stream().map(poi -> modelMapper.map(poi, PuntoInteresDtoGetMaestro.class)).collect(Collectors.toList());
    }

    public PuntoInteresDtoGetDetalle get(String idPunto) throws ServerException {
        PuntoInteresDtoGetDetalle puntoInteresDtoGetDetalle = null;
        Integer id = Ints.tryParse(idPunto);
        if (id != null) {
            puntoInteresDtoGetDetalle = modelMapper.map(puntosInteresDao.get(id), PuntoInteresDtoGetDetalle.class);
        } else {
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "El ID no es un número");
        }
        return puntoInteresDtoGetDetalle;
    }

    public PuntoInteresDtoGetDetalle insert(PuntoInteresDtoGetDetalle poi, UsuarioDtoGet usuarioDtoGet) {
        String erroresValidacion = validacionTool.validarObjeto(poi);
        if (erroresValidacion.length() == 0) {
            PuntoInteresEntity poiEntity = modelMapper.map(poi, PuntoInteresEntity.class);
            //Le añadimos el usuario que esta loggeado en el servidor
            poiEntity.setUsuarioByIdUsuario(modelMapper.map(usuarioDtoGet, UsuarioEntity.class));
            //Fijamos la activacion el false
            poiEntity.setActivado(false);
            return modelMapper.map(puntosInteresDao.save(poiEntity), PuntoInteresDtoGetDetalle.class);
        } else {
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, erroresValidacion);
        }
    }

    public List<PuntoInteresDtoGetMaestro> getAllSinActivar() {
        return puntosInteresDao.getAllSinActivar().stream().map(poi -> modelMapper.map(poi, PuntoInteresDtoGetMaestro.class)).collect(Collectors.toList());
    }

    public boolean activar(int id) {
        return puntosInteresDao.activar(id);
    }

    public boolean borrarPunto(int id){
        boolean borrado=false;
        if(puntosInteresDao.delete(id)){
            serviciosFotos.borrarDirectorioFotosPoi(id);
            borrado=true;
        }
        return true;
    }
}
