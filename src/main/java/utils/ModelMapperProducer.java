package utils;

import org.modelmapper.ModelMapper;

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
    return new ModelMapper();
  }

  @Produces
  @Singleton
  public Jsonb producesJsonb()
  {
    return JsonbBuilder.create();
  }

  @Produces
  @Singleton
  public Validacion producesValidacion(){return new Validacion();}



}
