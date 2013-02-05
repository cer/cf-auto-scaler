package net.chrisrichardson.cfautoscaler.webapp.interceptors;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class AuthenticationHelperTest {

  private final String userId = "foo";
  private final String password = "bar";

  @Test
  public void authenticationShouldSucceed() throws UnsupportedEncodingException {
    String encodedCredentials = encodeCredentials();
    Assert.assertTrue(AuthenticationHelper.authenticate("Basic " + encodedCredentials, userId, password));
  }

  private String encodeCredentials() {
    return Base64.encodeBase64String((userId + ":" + password).getBytes(Charset.defaultCharset()));
  }

  @Test
  public void authenticationShouldFail_BadPassword() throws UnsupportedEncodingException {
    String encodedCredentials = encodeCredentials();
    Assert.assertFalse(AuthenticationHelper.authenticate("Basic " + encodedCredentials, userId, password + "x"));
  }

  @Test
  public void authenticationShouldFail_no_header() throws UnsupportedEncodingException {
    Assert.assertFalse(AuthenticationHelper.authenticate(null, userId, password + "x"));
  }
  @Test
  public void authenticationShouldFail_not_basic() throws UnsupportedEncodingException {
    Assert.assertFalse(AuthenticationHelper.authenticate("Digest XYZ", userId, password + "x"));
  }
}
