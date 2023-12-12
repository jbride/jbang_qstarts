///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.bouncycastle:bcpkix-jdk18on:1.76


// Edit:  jbang edit --open=code hello_bouncy.java
// Execute: jbang ./hello_bouncy <keystore and private key password>

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class hello_bouncy {

    private static final String CRYPTO_POLICY="crypto.policy";
    private static final String UNLIMITED="unlimited";
    private static final String X509_CERT_FILE_PATH="/tmp/crypto/ratwater.cer";
    private static final String PKCS12="PKCS12";
    private static final String KEYSTORE_PATH = "/tmp/crypto/ratwater.p12";
    private static final String KEY_NAME="ratwater";

    public static void main(String[] args) throws Exception {

        // adds BC to the end of the precedence list
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(MessageDigest.getInstance("SHA1").getProvider().getName());
        System.out.println(MessageDigest.getInstance("SHA1", "BC").getProvider().getName());

        cryptoJurisdiction();

        String password = args[0];
        certsAndKeystores(password);

    }

    private static void cryptoJurisdiction() throws NoSuchAlgorithmException {

        // https://www.baeldung.com/java-bouncy-castle#setup-unlimited-strength-jurisdiction-policy-files
        // $JAVA_HOME/conf/security/java.security
        int maxKeySize = Cipher.getMaxAllowedKeyLength("AES"); 
        System.out.println("initial maxKeySize = "+maxKeySize+" : crypto.policy = "+Security.getProperty(CRYPTO_POLICY));
        if(!UNLIMITED.equals(Security.getProperty(CRYPTO_POLICY))) {
            Security.setProperty(CRYPTO_POLICY, UNLIMITED);
            System.out.println("Unlimited Strength Jurisdiction maxKeySize = "+maxKeySize);
        }
    }

    /*  Pre-Req:  Generate a SHA256 / RSA based digital cert and load into pkcs12 based keystore:
    
        export C_DIR=/tmp/crypto \
          && mkdir -p $C_DIR \
          && openssl genrsa -out $C_DIR/private-key.pem 2048 \
          && openssl req -new -sha256 -key $C_DIR/private-key.pem -out $C_DIR/certificate-signed-request.csr \
          && openssl req -x509 -sha256 -days 365 -key $C_DIR/private-key.pem -in $C_DIR/certificate-signed-request.csr -out $C_DIR/ratwater.cer \
          && openssl pkcs12 -export -name ratwater -out $C_DIR/ratwater.p12 -inkey $C_DIR/private-key.pem -in $C_DIR/ratwater.cer
     
     */
    private static void certsAndKeystores(String password) throws CertificateException, NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        CertificateFactory certFactory= CertificateFactory.getInstance("X.509", "BC");
 
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(new FileInputStream(X509_CERT_FILE_PATH));
        certificate.checkValidity();
        System.out.println("X500 cert sig algorithm: "+certificate.getSigAlgName());
        System.out.println("X500 Subject Principal:  "+certificate.getSubjectX500Principal());

        KeyStore keystore = KeyStore.getInstance(PKCS12);
        keystore.load(new FileInputStream(KEYSTORE_PATH), password.toCharArray());
        PrivateKey privKey = (PrivateKey)keystore.getKey(KEY_NAME, password.toCharArray());
        System.out.println("privKey algorithm= "+privKey.getAlgorithm()+" : privKey format= "+privKey.getFormat());
    }

}
