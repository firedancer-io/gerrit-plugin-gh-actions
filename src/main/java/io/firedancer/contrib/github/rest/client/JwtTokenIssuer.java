/*
 * Copyright (C) 2016 - 2020 Spotify AB
 * Copyright (C) 2022 Jump Crypto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.firedancer.contrib.github.rest.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;

/** Issues JWTs locally using a private key acquired from GitHub Apps settings. */
public class JwtTokenIssuer {

  private static final long TOKEN_TTL_MS = 600_000;

  private final Algorithm signingKey;
  private final int appId;

  private JwtTokenIssuer(int appId, PrivateKey signingKey) {
    this.appId = appId;
    this.signingKey = Algorithm.RSA256((RSAKey) signingKey);
  }

  /**
   * Instantiates a new Jwt token issuer.
   *
   * @param privateKey the private key to use
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws InvalidKeySpecException the invalid key spec exception
   */
  public static JwtTokenIssuer fromPrivateKey(int appId, byte[] privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeySpec keySpec =
        PKCS1PEMKey.loadKeySpec(privateKey).orElseGet(() -> new PKCS8EncodedKeySpec(privateKey));

    KeyFactory kf = KeyFactory.getInstance("RSA");
    PrivateKey signingKey = kf.generatePrivate(keySpec);
    return new JwtTokenIssuer(appId, signingKey);
  }

  /**
   * Generates a JWT token for the given app ID.
   *
   * @return Signed JWT
   */
  public IssuedToken issueJwt() {
    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(TOKEN_TTL_MS);

    String jwt =
        JWT.create()
            .withIssuer(String.valueOf(appId))
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .sign(signingKey);

    return new IssuedToken(jwt, expiresAt);
  }
}
