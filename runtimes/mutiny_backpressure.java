///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.16.3.Final}@pom
//DEPS io.quarkus.arc:arc
//FILES application.properties

import java.time.Duration;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.subscription.Cancellable;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;


/*
 * Reference:
 *  https://quarkus.io/blog/mutiny-back-pressure/                                                                       :   Mutiny Back Pressure; Oct 2020
 *  https://javadoc.io/doc/io.smallrye.reactive/mutiny/latest/io.smallrye.mutiny/io/smallrye/mutiny/Multi.html          :   Mutiny Multi javadocs
 *  https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html                                   :   java function javadocs
 */
public class mutiny_backpressure {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String... args) throws NumberFormatException {

    }
}
