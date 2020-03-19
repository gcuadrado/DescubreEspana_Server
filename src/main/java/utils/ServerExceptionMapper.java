package utils;

import modelo.ApiError;
import modelo.ServerException;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ServerExceptionMapper implements ExceptionMapper<ServerException> {

@Inject
private ModelMapper modelMapper;

  public Response toResponse(ServerException exception) {
    ApiError apiError = new ApiError(exception.getErrorCode(),exception.getMessage());
    return Response.status(exception.getErrorCode()).entity(apiError).type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  public ApiError converterServerExceptionApiError(ServerException se) {
    return modelMapper.map(se, ApiError.class);
  }

}
