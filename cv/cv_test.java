///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.16.3.Final}@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS ai.djl:bom:${djl.version:0.21.0}@pom
//DEPS ai.djl.opencv:opencv
//FILES application.properties

import picocli.CommandLine;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/* Reference:
 *  https://opencv-java-tutorials.readthedocs.io/en/latest/02-first-java-application-with-opencv.html
 *
 * Pre-reqs:
 *  # dnf install opencv-java -y
 * 
 * Execution:
 *  $ jbang -Djava.library.path=/usr/lib/java cv_test.java
 */

@CommandLine.Command
public class cv_test implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The cv to print", defaultValue = "World!")
    String name;

    @Inject
    CommandLine.IFactory factory;
    

    private final CVService cvService;

    public cv_test(CVService cvService) {
        this.cvService = cvService;
    }

    @Override
    public void run() {
        for(int x=0; x < 1; x++){
            cvService.simple();
        }
    }

}

@Dependent
class  CVService {
    public static final Logger log = Logger.getLogger("CVService");

    void simple() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());
    }
}
