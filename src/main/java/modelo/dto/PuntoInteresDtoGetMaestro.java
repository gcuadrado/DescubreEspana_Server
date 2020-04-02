package modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoInteresDtoGetMaestro {
    private int idPuntoInteres;
    private String nombre;
    private Double latitud;
    private Double longitud;
    private Boolean activado;
}
