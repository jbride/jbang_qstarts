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
public class hellomutiny_multi implements Runnable{

    @CommandLine.Parameters(index = "0", description = "Delay in milliseconds between ticks", defaultValue = "0")
    int delay;

    @CommandLine.Parameters(index = "1", description = "# of ticks", defaultValue = "0")
    int totalTicks;

    private final MutinyMultiResource multiService;

    public hellomutiny_multi(MutinyMultiResource multiService) { 
        this.multiService= multiService;
    }

    @Override
    public void run() {
        
        multiService.testSimpleTake();
        //multiService.observeRangeEventsAsync( delay, totalTicks );
        multiService.observeTickEventsAsync(delay, totalTicks);
        System.out.println("Finished run");
    }
}

@Dependent
class MutinyMultiResource {

    private AtomicInteger counter = new AtomicInteger(0);

    public void testSimpleTake() {
        List<Integer> list = Multi.createFrom().range(1, 5).select().first(1).collect().asList().await().indefinitely();
        System.out.println("testSimpleTake list size = "+list.size());
    }

    public void observeRangeEventsAsync(long delay, int totalTicks){
        Multi.createFrom().range(0, totalTicks)
    .       onSubscription().invoke(sub -> System.out.println("Received subscription: " + sub))
            .onRequest().invoke(req -> System.out.println("Got a request: " + req))
            .onItem().transform(i -> i * 100)
            .subscribe().with(
                i -> System.out.println("i: " + i)
            );
    }
 
    public void observeTickEventsAsync(long delay, int totalTicks) {
        
        Multi<Long> multi = Multi.createFrom().ticks().every(Duration.ofMillis(delay))

            /*
             * emitOn() is a Mutiny operator that determines which thread the Subscriber receives the events on. 
             * Multiple threads are typicall required when implementating back-pressure use-cases.
             * In a single thread approach, blocking the thread would block the source, which may have dramatic consequences.
             */
            //.emitOn(Infrastructure.getDefaultExecutor())
        
            
            // The call function has signature of: java.util.function.Function
            // It expects a callback returning a Uni to subscribe to. 
            // When the callback emits an Item the original event with the original Item is dispatched downstream. 
            // The emitted Item is only taken into account to continue with the processing.
            .onSubscription().call( (sub) -> {     

                System.out.println("Just received a subscription");
                return Uni.createFrom().nullItem();
            })

            .onOverflow().drop().call( (i) -> {
                // TO-DO:  Determine why this is always invoked with every item
                // System.out.println("dropped item: "+i);
                return Uni.createFrom().nullItem();
            })
            .onItem().transform( (i) -> {
                return i*100;
            })
            .onFailure().call( (fail) -> {
                System.out.println("Failure called: "+fail.getMessage());
                return Uni.createFrom().voidItem();
            })
            .onCompletion().call( () -> {
                System.out.println("Completion called");
                return Uni.createFrom().voidItem();
            })
            .onCancellation().call(  () -> {
                System.out.println("Just cancelled");
                return Uni.createFrom().item("value");
            })
            .onTermination().call( () -> {
                System.out.println("Just Terminated");
                return Uni.createFrom().voidItem();
            });

            // Multis are lazy by nature. To trigger the computation, you must subscribe.
            Cancellable cObj = multi
                //.select().first(totalTicks)
                .subscribe()
                .with( (i) -> {
                    counter.incrementAndGet();
                    System.out.println("call: "+i);
                });

            while(true){
                System.out.println("counter = "+counter.get());
                if(counter.get() == totalTicks){
                    cObj.cancel();
                    break;
                }else {
                    try { Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
    }

}
