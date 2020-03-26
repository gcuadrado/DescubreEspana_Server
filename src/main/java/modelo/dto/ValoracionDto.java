package modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionDto {
    private int idValoracion;
    private Integer puntuacion;
    private String comentario;
    private UsuarioDtoGet usuarioByIdUsuario;
    private int idPuntoInteres;
}
