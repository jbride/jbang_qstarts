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
import org.opencv.videoio.VideoCapture;

import nu.pattern.OpenCV;

/* 
 * Pre-reqs:
 *  # dnf install opencv-java -y
 * 
 * Execution:
 *  $ jbang -Djava.library.path=/usr/lib/java video_capture.java
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
        if(!vCapture.isOpened())
            throw new RuntimeException("Unable to access video capture device w/ id = " + videoCaptureDevice);
        else
            log.infov("Now capturing video on device {0}", videoCaptureDevice);

        Mat unboxedMat = new Mat();
        boolean captured = vCapture.read(unboxedMat);
        log.infov("just captured = "+captured);
    }
}