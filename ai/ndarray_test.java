///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.16.3.Final}@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS ai.djl:bom:${djl.version:0.21.0}@pom
//DEPS ai.djl:api
//DEPS ai.djl.mxnet:mxnet-engine
//FILES application.properties

import picocli.CommandLine;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.mxnet.engine.MxNDManager;

/*  Reference:
 *      - https://d2l.djl.ai/chapter_preliminaries/ndarray.html
 *      - https://towardsdatascience.com/ndarray-a-java-based-n-dim-array-toolkit-60b4035b10b8
 *      
 *  An ndarray represents a (possibly multi-dimensional) array of numerical values. 
 *  With one axis, an ndarray corresponds (in math) to a vector. 
 *  With two axes, an ndarray corresponds to a matrix. 
 *  Arrays with more than two axes do not have special mathematical names—we simply call them tensors.
 */

@CommandLine.Command
public class ndarray_test implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The ndarray to print", defaultValue = "World!")
    String name;

    @Inject
    CommandLine.IFactory factory;
    

    private final NDArrayService ndarrayService;

    public ndarray_test(NDArrayService ndarrayService) {
        this.ndarrayService = ndarrayService;
    }

    @Override
    public void run() {
        for(int x=0; x < 1; x++){
            ndarrayService.onesTest();
            ndarrayService.arangeTest();
        }
    }

}

@Dependent
class  NDArrayService {
    public static final Logger log = Logger.getLogger("NDArrayService");

    void onesTest() {

        // NDManager helps manage the memory usage of the NDArrays. It creates them and helps clear them as well. 
        // Once you finish using an NDManager, it will clear all of the NDArrays that were created within it’s scope as well.
        // NDManager helps the overall system utilize memory efficiently by tracking the NDArray usage.
        // Wrap NDManager with try blocks so all ndarrays will be closed in time
        try(NDManager ndManager = NDManager.newBaseManager()){

            log.infov("NDManager class implementation = {0}", ndManager.getClass().getName());

            // ones is an operation to generate N-dim array filled with 1
            NDArray ndArray = ndManager.ones(new Shape(2,3));

            ((MxNDManager)ndManager).debugDump(5);

            log.infov("\nPre-transpose:  ndArray of size {0} = \n{1}", ndArray.size() , ndArray.toDebugString(true));

            // In linear algebra, the transpose of a matrix is an operator which flips a matrix over its diagonal; that is, it switches the row and column indices of the matrix A by producing another matrix
            ndArray = ndArray.transpose().addi(10);

            log.infov("\nPost-transpose: ndArray of size {0} = \n{1}", ndArray.size() , ndArray.toDebugString(true));
        }
    }

    void arangeTest() {
        try(NDManager ndManager = NDManager.newBaseManager()){
            ((MxNDManager)ndManager).debugDump(5);
            NDArray ndArray = ndManager.arange(5,14);

            ndArray = ndArray.get(ndArray.gte(10));

            log.infov("\nfiltered ndArray of size {0} = \n{1}", ndArray.size() , ndArray.toDebugString(true));

        }
    }
}
