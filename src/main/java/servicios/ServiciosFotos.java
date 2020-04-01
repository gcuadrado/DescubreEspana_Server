package servicios;

import com.google.common.io.Files;
import dao.FotosDao;
import modelo.ServerException;
import modelo.dto.FotoPuntoInteresDtoGet;
import modelo.entity.FotoPuntoInteresEntity;
import modelo.entity.PuntoInteresEntity;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServiciosFotos {
    @Inject
    private FotosDao fotosDao;
    @Inject
    private ModelMapper modelMapper;

    public List<FotoPuntoInteresDtoGet> insertFoto(List<FormDataBodyPart> imagenes, int poiId) {

        List<FotoPuntoInteresEntity> fotos = IntStream.range(0, imagenes.size()).mapToObj(i -> {
            //Obtenemos el path relativo de cada foto
            String path = File.separator + "uploads" + File.separator + String.valueOf(poiId) + File.separator + i + "." + Files.getFileExtension(imagenes.get(i).getContentDisposition().getFileName());
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
        File file = new File(System.getProperty("catalina.base") + File.separator + "docroot" + path);
        if (file.getParent() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int read;
        while ((read = inputStream.read(bytes)) != -1) {
            fos.write(bytes, 0, read);
        }
        fos.close();
    }
}
