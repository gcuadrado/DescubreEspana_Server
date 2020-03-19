package servicios;

import modelo.dto.UsuarioDtoPost;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiciosKeyStore {
    public String generarKeyStoreCliente(UsuarioDtoPost usuarioDtoPost, ServletContext context){
        String keyStoreString=null;
        try {
            CertAndKeyGen certGen = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
            // generate it with 2048 bits
            certGen.generate(2048);
            // prepare the validity of the certificate
            long validSecs = (long) 365 * 24 * 60 * 60; // valid for one year
            // add the certificate information, currently only valid for one year.

            //firmar x509 por servidor
            PrivateKey clavePrivadaServidor = getPrivateKeyFromFile(context);

            X509Certificate cert = certGen.getSelfCertificate(
                    // enter your details according to your application
                    new X500Name("CN="+usuarioDtoPost.getEmail()+",O=My Organisation,L=My City,C=DE"), validSecs);

            byte[] inCertBytes = cert.getTBSCertificate();
            X509CertInfo info = new X509CertInfo(inCertBytes);

            //La información del servidor, quién emite el certificado
            info.set(X509CertInfo.ISSUER, new X500Name("CN=SERVIDOR,O=My Organisation,L=My City,C=DE"));
            X509CertImpl certificadoCliente = new X509CertImpl(info);

            //Se firma el certificado con la clave privada del server, así luego podemos asegurarnos de que un certificado es válido
            certificadoCliente.sign(clavePrivadaServidor, cert.getSigAlgName());
            //Creamos una clave privada para el usuario
            PrivateKey clavePrivadaCliente = certGen.getPrivateKey();
            //Generamos un keystore que almacenará el certificado y la clave privada
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(null, null);
            ks.setCertificateEntry("publica", certificadoCliente);
            //Añadimos la clave privada, cifrada con la contraseña del usuario
            ks.setKeyEntry("privada", clavePrivadaCliente, usuarioDtoPost.getPassword().toCharArray(),
                    new Certificate[]{certificadoCliente});
            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            char[] password = usuarioDtoPost.getPassword().toCharArray();
            //Almacenamos el keystore en un array de bytes cifrado con la contraseña del usuario
            ks.store(fos, password);
            //Codificamos el array de bytes en base64 para poder enviarlo por HTTP
            keyStoreString=Base64.getUrlEncoder().encodeToString(fos.toByteArray());
            fos.close();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        return keyStoreString;
    }

    private PrivateKey getPrivateKeyFromFile(ServletContext context) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec clavePrivadaSpec = null;
        byte[] bufferPriv = new byte[5000];
        InputStream in = context.getResourceAsStream("/WEB-INF/jwt.privada");
        int chars;
        try {
            chars = in.read(bufferPriv, 0, 5000);
            in.close();

            byte[] bufferPriv2 = new byte[chars];
            System.arraycopy(bufferPriv, 0, bufferPriv2, 0, chars);

            // 2.2 Recuperar clave privada desde datos codificados en formato PKCS8
            clavePrivadaSpec = new PKCS8EncodedKeySpec(bufferPriv2);

        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        PrivateKey clavePrivadaServidor;


        KeyFactory keyFactoryRSA = null; // Hace uso del provider BC

        keyFactoryRSA = KeyFactory.getInstance("RSA");

        clavePrivadaServidor = keyFactoryRSA.generatePrivate(clavePrivadaSpec);
        return clavePrivadaServidor;
    }
}
