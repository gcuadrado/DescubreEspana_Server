package modelo.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "valoracion", schema = "descubre_espana")
public class ValoracionEntity {
    private int idValoracion;
    private Integer puntuacion;
    private String comentario;
    private UsuarioEntity usuarioByIdUsuario;
    private PuntoInteresEntity puntoInteresByIdPuntoInteres;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    public int getIdValoracion() {
        return idValoracion;
    }

    public void setIdValoracion(int idValoracion) {
        this.idValoracion = idValoracion;
    }

    @Basic
    @Column(name = "puntuacion")
    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Basic
    @Column(name = "comentario")
    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValoracionEntity that = (ValoracionEntity) o;
        return idValoracion == that.idValoracion &&
                Objects.equals(puntuacion, that.puntuacion) &&
                Objects.equals(comentario, that.comentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idValoracion, puntuacion, comentario);
    }

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    public UsuarioEntity getUsuarioByIdUsuario() {
        return usuarioByIdUsuario;
    }

    public void setUsuarioByIdUsuario(UsuarioEntity usuarioByIdUsuario) {
        this.usuarioByIdUsuario = usuarioByIdUsuario;
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
