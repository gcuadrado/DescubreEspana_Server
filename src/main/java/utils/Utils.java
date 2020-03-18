/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 *
 * @author oscar
 */
public class Utils {
    private Utils() {
    }

    public static String randomBytes()
    {
        SecureRandom sr = new SecureRandom();
        byte[] bits = new byte[32];
        sr.nextBytes(bits);
        return Base64.getUrlEncoder().encodeToString(bits);
    }


}
