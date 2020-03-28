package servicios;

import com.google.common.primitives.Ints;
import dao.ValoracionDao;
import modelo.ServerException;
import modelo.dto.UsuarioDtoGet;
import modelo.dto.ValoracionDto;
import modelo.entity.PuntoInteresEntity;
import modelo.entity.UsuarioEntity;
import modelo.entity.ValoracionEntity;
import org.modelmapper.ModelMapper;
import utils.ValidacionTool;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class ServiciosValoraciones {
    @Inject
    private ValidacionTool validacion;
    @Inject
    private ValoracionDao valoracionDao;
    @Inject
    private ModelMapper modelMapper;


    public List<ValoracionDto> getAll(int id) throws ServerException {
        return valoracionDao.getAll(id).stream().map(valoracion -> modelMapper.map(valoracion, ValoracionDto.class)).collect(Collectors.toList());
    }

    public ValoracionDto save(ValoracionDto valoracion, UsuarioDtoGet usuarioDtoGet) throws ServerException {
        String erroresValidacion = validacion.validarObjeto(valoracion);
        //Se añade el usuario actual loggeado en servidor, por si hackean el cliente
        valoracion.setUsuarioByIdUsuario(usuarioDtoGet);
        ValoracionDto valoracionInsertada = null;
        if (erroresValidacion.length() == 0) {

            ValoracionEntity valoracionEntity = new ValoracionEntity();
            valoracionEntity.setComentario(valoracion.getComentario());
            valoracionEntity.setPuntuacion(valoracion.getPuntuacion());
            valoracionEntity.setUsuarioByIdUsuario(modelMapper.map(valoracion.getUsuarioByIdUsuario(), UsuarioEntity.class));
            PuntoInteresEntity puntoInteresEntity = new PuntoInteresEntity();
            puntoInteresEntity.setIdPuntoInteres(valoracion.getIdPuntoInteres());
            valoracionEntity.setPuntoInteresByIdPuntoInteres(puntoInteresEntity);
            valoracionInsertada = valoracionDao.save(valoracionEntity);

        } else {
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, erroresValidacion);
        }
        return valoracionInsertada;
    }

    public boolean delete(String valoracionId, UsuarioDtoGet usuarioDtoGet) throws ServerException {
        boolean borrado = false;
        Integer id = Ints.tryParse(valoracionId);
        if (id != null) {
            borrado = valoracionDao.delete(id,usuarioDtoGet.getIdUsuario());
        }else{
            throw new ServerException(HttpURLConnection.HTTP_BAD_REQUEST, "El ID no es un número");
        }
        return borrado;
    }
}
