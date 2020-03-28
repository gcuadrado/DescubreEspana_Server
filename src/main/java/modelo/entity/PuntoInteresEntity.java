package modelo.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "punto_interes", schema = "descubre_espana")
public class PuntoInteresEntity {
    private int idPuntoInteres;
    private String nombre;
    private String resumen;
    private String infoDetallada;
    private String fechaInicio;
    private String direccion;
    private String horario;
    private Double coste;
    private Boolean accesibilidad;
    private Double puntuacion;
    private String categoria;
    private Double latitud;
    private Double longitud;
    private String enlaceInfo;
    private String contacto;
    private Collection<FotoPuntoInteresEntity> fotoPuntoInteresByIdPuntoInteres;
    private Collection<ValoracionEntity> valoraciones;
    private UsuarioEntity usuarioByIdUsuario;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_punto_interes")
    public int getIdPuntoInteres() {
        return idPuntoInteres;
    }

    public void setIdPuntoInteres(int idPuntoInteres) {
        this.idPuntoInteres = idPuntoInteres;
    }

    @Basic
    @Column(name = "nombre")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "resumen")
    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    @Basic
    @Column(name = "info_detallada")
    public String getInfoDetallada() {
        return infoDetallada;
    }

    public void setInfoDetallada(String infoDetallada) {
        this.infoDetallada = infoDetallada;
    }

    @Basic
    @Column(name = "fecha_inicio")
    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    @Basic
    @Column(name = "direccion")
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Basic
    @Column(name = "horario")
    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Basic
    @Column(name = "coste")
    public Double getCoste() {
        return coste;
    }

    public void setCoste(Double coste) {
        this.coste = coste;
    }

    @Basic
    @Column(name = "accesibilidad")
    public Boolean getAccesibilidad() {
        return accesibilidad;
    }

    public void setAccesibilidad(Boolean accesibilidad) {
        this.accesibilidad = accesibilidad;
    }

    @Basic
    @Column(name = "puntuacion")
    public Double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Double puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Basic
    @Column(name = "categoria")
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Basic
    @Column(name = "latitud")
    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    @Basic
    @Column(name = "longitud")
    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    @Basic
    @Column(name = "enlace_info")
    public String getEnlaceInfo() {
        return enlaceInfo;
    }

    public void setEnlaceInfo(String enlaceInfo) {
        this.enlaceInfo = enlaceInfo;
    }

    @Basic
    @Column(name = "contacto")
    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuntoInteresEntity that = (PuntoInteresEntity) o;
        return idPuntoInteres == that.idPuntoInteres &&
                Objects.equals(nombre, that.nombre) &&
                Objects.equals(resumen, that.resumen) &&
                Objects.equals(infoDetallada, that.infoDetallada) &&
                Objects.equals(fechaInicio, that.fechaInicio) &&
                Objects.equals(direccion, that.direccion) &&
                Objects.equals(horario, that.horario) &&
                Objects.equals(coste, that.coste) &&
                Objects.equals(accesibilidad, that.accesibilidad) &&
                Objects.equals(puntuacion, that.puntuacion) &&
                Objects.equals(categoria, that.categoria) &&
                Objects.equals(latitud, that.latitud) &&
                Objects.equals(longitud, that.longitud) &&
                Objects.equals(enlaceInfo, that.enlaceInfo) &&
                Objects.equals(contacto, that.contacto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPuntoInteres, nombre, resumen, infoDetallada, fechaInicio, direccion, horario, coste, accesibilidad, puntuacion, categoria, latitud, longitud, enlaceInfo, contacto);
    }

    @OneToMany(mappedBy = "puntoInteresByIdPuntoInteres")
    public Collection<FotoPuntoInteresEntity> getFotoPuntoInteresByIdPuntoInteres() {
        return fotoPuntoInteresByIdPuntoInteres;
    }

    public void setFotoPuntoInteresByIdPuntoInteres(Collection<FotoPuntoInteresEntity> fotoPuntoInteresByIdPuntoInteres) {
        this.fotoPuntoInteresByIdPuntoInteres = fotoPuntoInteresByIdPuntoInteres;
    }

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    public UsuarioEntity getUsuarioByIdUsuario() {
        return usuarioByIdUsuario;
    }

    public void setUsuarioByIdUsuario(UsuarioEntity usuarioByIdUsuario) {
        this.usuarioByIdUsuario = usuarioByIdUsuario;
    }

    @OneToMany(mappedBy = "puntoInteresByIdPuntoInteres")
    public Collection<ValoracionEntity> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(Collection<ValoracionEntity> valoraciones) {
        this.valoraciones = valoraciones;
    }
}
