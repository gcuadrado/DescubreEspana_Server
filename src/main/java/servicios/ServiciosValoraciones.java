package servicios;

import dao.ValoracionDao;
import modelo.ServerException;
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


    public List<ValoracionDto> getAll() throws ServerException {
        return valoracionDao.getAll().stream().map(valoracion -> modelMapper.map(valoracion, ValoracionDto.class)).collect(Collectors.toList());
    }

    public ValoracionDto save(ValoracionDto valoracion) throws ServerException {
        String erroresValidacion = validacion.validarObjeto(valoracion);
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
}