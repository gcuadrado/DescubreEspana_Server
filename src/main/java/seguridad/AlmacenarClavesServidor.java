package seguridad;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlmacenarClavesServidor {

    public PublicKey leerClavePublicaDeFichero(InputStream stream) {
        PublicKey clavePublica2 = null;
        try {
            KeyFactory keyFactoryRSA = KeyFactory.getInstance("RSA");
            byte[] bufferPub = new byte[5000];
            int chars = stream.read(bufferPub, 0, 5000);
            stream.close();

            byte[] bufferPub2 = new byte[chars];
            System.arraycopy(bufferPub, 0, bufferPub2, 0, chars);

            // 4.2 Recuperar clave publica desde datos codificados en formato X509
            X509EncodedKeySpec clavePublicaSpec = new X509EncodedKeySpec(bufferPub);
            clavePublica2 = keyFactoryRSA.generatePublic(clavePublicaSpec);
        } catch (Exception e) {
            Logger.getLogger("Almacenarclaves").log(Level.SEVERE, null, e);
        }
        return clavePublica2;
    }

    public PrivateKey leerClavePrivadaDeFichero(InputStream stream) {
        try {
            PrivateKey clavePrivada2 = null;
            // 2.1 Leer datos binarios PKCS8
            byte[] bufferPriv = new byte[5000];
            int chars = stream.read(bufferPriv, 0, 5000);
            stream.close();

            byte[] bufferPriv2 = new byte[chars];
            System.arraycopy(bufferPriv, 0, bufferPriv2, 0, chars);

            // 2.2 Recuperar clave privada desde datos codificados en formato PKCS8
            PKCS8EncodedKeySpec clavePrivadaSpec = new PKCS8EncodedKeySpec(bufferPriv2);
            /**
             * * Crear KeyFactory (depende del provider) usado para las
             * transformaciones de claves
             */
            KeyFactory keyFactoryRSA = KeyFactory.getInstance("RSA");

            clavePrivada2 = keyFactoryRSA.generatePrivate(clavePrivadaSpec);
            return clavePrivada2;
        } catch (Exception e) {
            Logger.getLogger("Almacenarclaves").log(Level.SEVERE, null, e);
            return null;
        }
    }
}