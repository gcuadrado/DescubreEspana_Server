package modelo.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "foto_punto_interes", schema = "descubre_espana")
public class FotoPuntoInteresEntity {
    private int idFoto;
    private String path;
    private PuntoInteresEntity puntoInteresByIdPuntoInteres;

    @Id
    @Column(name = "id_foto")
    public int getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(int idFoto) {
        this.idFoto = idFoto;
    }

    @Basic
    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FotoPuntoInteresEntity that = (FotoPuntoInteresEntity) o;
        return idFoto == that.idFoto &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFoto, path);
    }

    @ManyToOne
    @JoinColumn(name = "id_punto_interes", referencedColumnName = "id_punto_interes")
    public PuntoInteresEntity getPuntoInteresByIdPuntoInteres() {
        return puntoInteresByIdPuntoInteres;
    }

    public void setPuntoInteresByIdPuntoInteres(PuntoInteresEntity puntoInteresByIdPuntoInteres) {
        this.puntoInteresByIdPuntoInteres = puntoInteresByIdPuntoInteres;
    }
}
