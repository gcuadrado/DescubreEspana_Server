package servicios;

import com.google.common.io.Files;
import config.Configuration;
import dao.FotosDao;
import dao.PuntosInteresDao;
import modelo.ServerException;
import modelo.dto.FotoPuntoInteresDtoGet;
import modelo.entity.FotoPuntoInteresEntity;
import modelo.entity.PuntoInteresEntity;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.imgscalr.Scalr;
import org.modelmapper.ModelMapper;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServiciosFotos {
    @Inject
    private FotosDao fotosDao;
    @Inject
    private PuntosInteresDao puntosInteresDao;
    @Inject
    private ModelMapper modelMapper;

    public List<FotoPuntoInteresDtoGet> insertFoto(List<FormDataBodyPart> imagenes, String folderUUID, int poiId) {

        List<FotoPuntoInteresEntity> fotos = IntStream.range(0, imagenes.size()).mapToObj(i -> {
            //Obtenemos el path relativo de cada foto
            String path = "uploads/" + folderUUID + "/" + UUID.randomUUID() + "." + Files.getFileExtension(imagenes.get(i).getContentDisposition().getFileName());
            //Guardamos en el disco duro del servidor los archivos
            try {
                InputStream inputStream = ((BodyPartEntity) imagenes.get(i).getEntity()).getInputStream();
                guardarImagenEnDisco(path, inputStream);
            } catch (Exception e) {
                throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Error al guardar las imágenes");
            }
            //Generamos la entidad que se guardará en BBDD
            FotoPuntoInteresEntity foto = new FotoPuntoInteresEntity();
            //Convertimos el path al formato linux, debe ser así para acceder desde cliente
            foto.setPath(FilenameUtils.separatorsToUnix(path));
            PuntoInteresEntity p = new PuntoInteresEntity();
            p.setIdPuntoInteres(poiId);
            foto.setPuntoInteresByIdPuntoInteres(p);
            return foto;
        }).collect(Collectors.toList());

        //Devolvemos las fotos ya insertadas como DTO
        return fotos.stream().map(foto -> {
            FotoPuntoInteresEntity fotoInsertada = fotosDao.insert(foto);
            return modelMapper.map(fotoInsertada, FotoPuntoInteresDtoGet.class);
        }).collect(Collectors.toList());

    }

    private void guardarImagenEnDisco(String path, InputStream inputStream) throws IOException {
        //Convertimos el path relativo de la imagen a la notación del S.O donde esté corriendo el servidor
        path = FilenameUtils.separatorsToSystem(path);
        //Creamos el archivo, uniendo el directorio preconfigurado donde se deben almacenar
        //las imágenes con el path relativo de la misma
        File file = new File(System.getProperty("catalina.base")+ File.separator + path);
        //Si las carpetas padre del archivo aún no existen, se crean
        if (file.getParent() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //Escribimos el archivo
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int read;
        while ((read = inputStream.read(bytes)) != -1) {
            fos.write(bytes, 0, read);
        }
        fos.close();
    }

    public void guardarImagenPrincipalEnDisco(String path, InputStream inputStream) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);
        BufferedImage outPutImage = Scalr.resize(inputImage, 150);
        path = FilenameUtils.separatorsToSystem(path);
        File file = new File(System.getProperty("catalina.base")+File.separator + path);

        if (file.getParent() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        //Se redimensiona la imagen a 150X150px, menos espacio, menos tiempo de carga
        Thumbnails.of(inputImage)
                .size(150, 150)
                .outputFormat(FilenameUtils.getExtension(path))
                .toFile(file);

        /*ImageIO.write(outPutImage,FilenameUtils.getExtension(path),file);*/
    }

    public boolean borrarDirectorioFotosPoi(int id) {
        boolean borradas = false;
        try {
            String uuidFolder = puntosInteresDao.get(id).getUuidFolderFilename();
            FileUtils.deleteDirectory(new File(System.getProperty("catalina.base")+File.separator+"uploads"+File.separator + uuidFolder));
            borradas = true;
        } catch (Exception e) {
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Error al eliminar las imágenes del disco duro");
        }
        return borradas;
    }

    public boolean delete(int id) {
        boolean borrado=false;
        FotoPuntoInteresEntity foto = fotosDao.get(id);
        File file = new File(Configuration.getInstance().getUploadsDirectory() + FilenameUtils.separatorsToSystem(foto.getPath()));
        if (file.delete()) {
            fotosDao.delete(id);
            borrado=true;
        } else {
            throw new ServerException(HttpURLConnection.HTTP_INTERNAL_ERROR, "No se ha podido borrar la imagen del disco duro");
        }
        return borrado;
    }


}
