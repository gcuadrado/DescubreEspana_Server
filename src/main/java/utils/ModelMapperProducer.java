package utils;

import modelo.dto.UsuarioDtoGet;
import modelo.dto.ValoracionDto;
import modelo.entity.ValoracionEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;


@RequestScoped
public class ModelMapperProducer {

  @Produces
  @Singleton
  public ModelMapper producesModelMapper()
  {
    ModelMapper modelMapper=new ModelMapper();
    Converter<ValoracionEntity, ValoracionDto> valoracionDtoConverter = new Converter<ValoracionEntity, ValoracionDto>()
    {
      public ValoracionDto convert(MappingContext<ValoracionEntity, ValoracionDto> context)
      {
        ValoracionEntity ve = context.getSource();
        ValoracionDto vdto = new ValoracionDto();
        vdto.setIdValoracion(ve.getIdValoracion());
        vdto.setComentario(ve.getComentario());
        vdto.setPuntuacion(ve.getPuntuacion());
        vdto.setUsuarioByIdUsuario(modelMapper.map(ve.getUsuarioByIdUsuario(), UsuarioDtoGet.class));
        vdto.setIdPuntoInteres(ve.getPuntoInteresByIdPuntoInteres().getIdPuntoInteres());
        return vdto;
      }
    };
    modelMapper.addConverter(valoracionDtoConverter);
    return modelMapper;
  }

  @Produces
  @Singleton
  public Jsonb producesJsonb()
  {
    return JsonbBuilder.create();
  }




}
