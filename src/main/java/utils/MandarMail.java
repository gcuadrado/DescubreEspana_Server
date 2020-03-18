/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;


import config.Configuration;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 *
 * @author oscar
 */
public class MandarMail {

    
    public void mandarMail(String to, String msg, String subject) throws EmailException {

            Email email = new SimpleEmail();
            email.setHostName(Configuration.getInstance().getSmtpHost());
            email.setSmtpPort(Configuration.getInstance().getSmtpPort());
            email.setAuthentication(Configuration.getInstance().getEmail(), Configuration.getInstance().getEmailPass());
            //email.setSSLOnConnect(true);
            email.setStartTLSEnabled(true);
            email.setFrom(Configuration.getInstance().getEmail());
            email.setSubject(subject);
            email.setContent(msg,"text/html");
            email.addTo(to);

            email.send();

    }

}