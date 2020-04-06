package utils;

import modelo.dto.FotoPuntoInteresDtoGet;
import modelo.dto.UsuarioDtoGet;
import modelo.dto.ValoracionDto;
import modelo.entity.FotoPuntoInteresEntity;
import modelo.entity.PuntoInteresEntity;
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
    public ModelMapper producesModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        Converter<ValoracionEntity, ValoracionDto> valoracionDtoConverter = new Converter<ValoracionEntity, ValoracionDto>() {
            @Override
            public ValoracionDto convert(MappingContext<ValoracionEntity, ValoracionDto> context) {
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
        Converter<FotoPuntoInteresEntity, FotoPuntoInteresDtoGet> fotoDtoConverter = new Converter<FotoPuntoInteresEntity, FotoPuntoInteresDtoGet>() {
            @Override
            public FotoPuntoInteresDtoGet convert(MappingContext<FotoPuntoInteresEntity, FotoPuntoInteresDtoGet> context) {
                FotoPuntoInteresEntity fe = context.getSource();
                FotoPuntoInteresDtoGet fDto = FotoPuntoInteresDtoGet.builder()
                        .idFoto(fe.getIdFoto())
                        .path(fe.getPath())
                        .poiId(fe.getPuntoInteresByIdPuntoInteres().getIdPuntoInteres())
                        .build();
                return fDto;
            }
        };
        Converter<FotoPuntoInteresDtoGet, FotoPuntoInteresEntity> fotoEntityConverter = new Converter<FotoPuntoInteresDtoGet, FotoPuntoInteresEntity>() {
            @Override
            public FotoPuntoInteresEntity convert(MappingContext<FotoPuntoInteresDtoGet, FotoPuntoInteresEntity> mappingContext) {
                FotoPuntoInteresDtoGet fDto = mappingContext.getSource();
                FotoPuntoInteresEntity fe = new FotoPuntoInteresEntity();
                fe.setIdFoto(fDto.getIdFoto());
                fe.setPath(fDto.getPath());
                PuntoInteresEntity p = new PuntoInteresEntity();
                p.setIdPuntoInteres(fDto.getPoiId());
                fe.setPuntoInteresByIdPuntoInteres(p);
                return fe;
            }
        };
        modelMapper.addConverter(valoracionDtoConverter);
        modelMapper.addConverter(fotoDtoConverter);
        modelMapper.addConverter(fotoEntityConverter);
        return modelMapper;
    }

    @Produces
    @Singleton
    public Jsonb producesJsonb() {
        return JsonbBuilder.create();
    }


}
