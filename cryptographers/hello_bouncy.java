///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.bouncycastle:bcpkix-jdk18on:1.76


// jbang edit --open=code hello_bouncy.java

import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class hello_bouncy {

    public static void main(String[] args) throws Exception {
        // adds BC to the end of the precedence list
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(MessageDigest.getInstance("SHA1").getProvider().getName());
        System.out.println(MessageDigest.getInstance("SHA1", "BC").getProvider().getName());
    }

}
