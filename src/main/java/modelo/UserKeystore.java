package modelo;

import lombok.Builder;
import lombok.Data;
import modelo.dto.UsuarioDtoGet;
@Data
@Builder
public class UserKeystore {
    private UsuarioDtoGet usuarioDtoGet;
    private String keystore;
}
