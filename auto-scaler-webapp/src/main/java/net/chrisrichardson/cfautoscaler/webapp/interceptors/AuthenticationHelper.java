package net.chrisrichardson.cfautoscaler.webapp.interceptors;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class AuthenticationHelper {
  static boolean authenticate(String credentials, String userId, String password) throws UnsupportedEncodingException {
    if (credentials != null) {
      String[] tokens = credentials.split(" +");
      if (tokens.length == 2 && tokens[0].equals("Basic")) {
        String encodedCredentials = Base64.encodeBase64String((userId + ":" + password).getBytes(Charset.defaultCharset()));
        return encodedCredentials.equals(tokens[1]);
      }
    }
    return false;
  }
}
