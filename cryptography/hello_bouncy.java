///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.bouncycastle:bcpkix-jdk18on:1.76


// jbang edit --open=code hello_bouncy.java

import java.security.MessageDigest;
import java.security.Security;
import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class hello_bouncy {

    private static final String CRYPTO_POLICY="crypto.policy";
    private static final String UNLIMITED="unlimited";

    public static void main(String[] args) throws Exception {

        // adds BC to the end of the precedence list
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(MessageDigest.getInstance("SHA1").getProvider().getName());
        System.out.println(MessageDigest.getInstance("SHA1", "BC").getProvider().getName());


        // https://www.baeldung.com/java-bouncy-castle#setup-unlimited-strength-jurisdiction-policy-files
        // $JAVA_HOME/conf/security/java.security
        int maxKeySize = Cipher.getMaxAllowedKeyLength("AES"); 
        System.out.println("initial maxKeySize = "+maxKeySize+" : crypto.policy = "+Security.getProperty(CRYPTO_POLICY));
        if(!UNLIMITED.equals(Security.getProperty(CRYPTO_POLICY))) {
            Security.setProperty(CRYPTO_POLICY, UNLIMITED);
            System.out.println("Unlimited Strength Jurisdiction maxKeySize = "+maxKeySize);
        }
    }

}
