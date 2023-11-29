///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus.platform:quarkus-bom:3.2.0.Final@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS ai.djl:bom:${djl.version:0.22.1}@pom
//DEPS ai.djl.opencv:opencv
//FILES application.properties

import picocli.CommandLine;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import nu.pattern.OpenCV;

import org.jboss.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.highgui.HighGui;

/* 
 * Pre-reqs:
 *  $ sudo dnf install opencv-java gstreamer1-plugin-libav -y
 *  $ sudo usermod -a -G video $USER
 * 
 * Execution:
 *  $ jbang -Djava.library.path=/usr/lib/java video_processing.java VH-Panama.mp4
 */

@CommandLine.Command
public class video_processing implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The video file", defaultValue = "VH-Panama.avi")
    String testVideoFile;

    @Inject
    CommandLine.IFactory factory;
    
    private final CVService cvService;

    public video_processing(CVService cvService) {
        this.cvService = cvService;
    }

    @Override
    public void run() {
        cvService.capture(this.testVideoFile);
    }
}

@Dependent
class  CVService {
    public static final Logger log = Logger.getLogger("CVService");

    void capture(String testVideoFile) {

        log.info("Working Directory = " + System.getProperty("user.dir"));

        // Not actually needed
        // Just ensure opencv-java gstreamer1-plugin-libav packages are installed and "java.library.path" includes path to those installed C++ libraries
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        OpenCV.loadShared();
        VideoCapture vCapture = new VideoCapture(testVideoFile, Videoio.CAP_ANY);

        //Create new MAT object
        Mat frame = new Mat();

        while (vCapture.read(frame)) {
            HighGui.imshow("Video", frame);
            HighGui.waitKey(25); // Delay between frames (adjust as needed)
        }

        // Release resources and close the video file
        vCapture.release();
        HighGui.destroyAllWindows();
    }
}
