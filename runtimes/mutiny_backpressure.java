///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.16.3.Final}@pom
//DEPS io.quarkus.arc:arc
//DEPS io.quarkus:quarkus-picocli
//FILES application.properties

import java.time.Duration;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.subscription.Cancellable;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.Dependent;

import picocli.CommandLine;

/*
 * Reference:
 *  https://javadoc.io/doc/io.smallrye.reactive/mutiny/latest/io.smallrye.mutiny/io/smallrye/mutiny/Multi.html          :   Mutiny Multi javadocs
 *  https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html                                   :   java function javadocs
 */
@CommandLine.Command
public class mutiny_backpressure implements Runnable{

    @CommandLine.Parameters(index = "0", description = "Delay in milliseconds between ticks", defaultValue = "0")
    int delay;

    @CommandLine.Parameters(index = "1", description = "# of ticks", defaultValue = "0")
    int totalTicks;

    private final MutinyMultiResource multiService;

    public mutiny_backpressure(MutinyMultiResource multiService) { 
        this.multiService= multiService;
    }

    @Override
    public void run() {
        
    }
}

@Dependent
class MutinyMultiResource {

    private AtomicInteger counter = new AtomicInteger(0);

}
