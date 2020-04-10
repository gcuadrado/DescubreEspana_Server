package config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class Configuration {
    private static Configuration config;

    private Configuration() {

    }

    public static Configuration getInstance() {

        return config;
    }

    public static Configuration getInstance(InputStream file) {
        if (config == null) {
            Yaml yaml = new Yaml();
            config = (Configuration) yaml.loadAs(
                    file,
                    Configuration.class);

        }
        return config;
    }

    private String urlDB;
    private String username;
    private String password;
    private String driver;
    private int tiempoActivacion;
    private String email;
    private String emailPass;
    private String smtpHost;
    private int smtpPort;
    private String uploadsDirectory;

    public String getUrlDB() {
        return urlDB;
    }

    public void setUrlDB(String urlDB) {
        this.urlDB = urlDB;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getTiempoActivacion() {
        return tiempoActivacion;
    }

    public void setTiempoActivacion(int tiempoActivacion) {
        this.tiempoActivacion = tiempoActivacion;
    }

    public String getEmail() {
        return email;
    }

    public String getUploadsDirectory() {
        return uploadsDirectory;
    }

    public void setUploadsDirectory(String uploadsDirectory) {
        this.uploadsDirectory = uploadsDirectory;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPass() {
        return emailPass;
    }

    public void setEmailPass(String emailPass) {
        this.emailPass = emailPass;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }
}
