package modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ServerException extends RuntimeException {
    private int errorCode;
    @NonNull
    private String message;
}
