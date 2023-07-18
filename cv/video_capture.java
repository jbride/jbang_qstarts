///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus.platform:quarkus-bom:${quarkus.version:3.2.0.Final}@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS ai.djl:bom:${djl.version:0.22.1}@pom
//DEPS ai.djl.opencv:opencv
//FILES application.properties

import picocli.CommandLine;

import java.util.Arrays;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import nu.pattern.OpenCV;

import com.sun.security.auth.module.UnixSystem;

/* 
 * Pre-reqs:
 *  $ sudo dnf install opencv-java -y
 *  $ sudo usermod -a -G video $USER
 * 
 * Execution:
 *  $ jbang video_capture.java
 */

@CommandLine.Command
public class video_capture implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The video capture device id", defaultValue = "0")
    int videoCaptureDevice;

    @Inject
    CommandLine.IFactory factory;
    

    private final CVService cvService;

    public video_capture(CVService cvService) {
        this.cvService = cvService;
    }

    @Override
    public void run() {
        cvService.capture(this.videoCaptureDevice);
    }

}

@Dependent
class  CVService {
    public static final Logger log = Logger.getLogger("CVService");

    void capture(int videoCaptureDevice) {

        // Enable web cam  (but don't start capturing images and executing object detection predictions on those images just yet)
        OpenCV.loadShared();
        VideoCapture vCapture = new VideoCapture(videoCaptureDevice);
        UnixSystem uSystem = new UnixSystem();
        long[] groups = uSystem.getGroups();
        if(!vCapture.isOpened()) {
            throw new RuntimeException("Unable to access video capture device w/ id = " + videoCaptureDevice + " and groups: "+Arrays.toString(groups));
        }
        else
            log.infov("Now capturing video on device {0} with OS groups {1}", videoCaptureDevice, Arrays.toString(groups));

        Mat unboxedMat = new Mat();
        boolean captured = vCapture.read(unboxedMat);
        log.infov("just captured = "+captured);
    }
}
