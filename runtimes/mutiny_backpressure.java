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
import io.smallrye.mutiny.subscription.BackPressureStrategy;
import io.smallrye.mutiny.subscription.Cancellable;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.Dependent;

import picocli.CommandLine;

/*
 * Reference:
 *  https://quarkus.io/blog/mutiny-back-pressure/                                                                       :   Mutiny Back-Pressure; Oct 2020
 *  https://javadoc.io/doc/io.smallrye.reactive/mutiny/latest/io.smallrye.mutiny/io/smallrye/mutiny/Multi.html          :   Mutiny Multi javadocs
 *  https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html                                   :   java function javadocs
 *
 * Notes:
 *  Mutiny implements the Reactive Streams protocol for you. 
 *  In other words, when using Multi, you are using a Publisher following the Reactive Streams protocol. 
 *  All the subscription handshakes and requests negotiations are done for you.
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
        multiService.backPressureStrategyLatest(totalTicks);
    }
}

@Dependent
class MutinyMultiResource {

    private AtomicInteger counter = new AtomicInteger(0);

    public void backPressureStrategyLatest(int totalTicks) {

        Multi.createFrom().<Integer> emitter(e -> {
                for(int c=0; c < totalTicks; c++){
                    e.emit(c);
                }
            }, BackPressureStrategy.LATEST)
            .onSubscription().call( (s) -> {
                System.out.println("Subscription received");
                return Uni.createFrom().nullItem();
            })
            .onRequest().call( (l) -> {
                System.out.println("requested: "+l);
                return Uni.createFrom().nullItem();
            })
            .subscribe().with(
                sub -> {
                    sub.request(2);
                },
                item -> System.out.println("item: "+item),
                fail -> System.out.println("fail: "+fail),
                () -> System.out.println("Completion"));
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
                .with(
                    sub -> System.out.println("sub = "+sub), 
                    item -> {
                        counter.incrementAndGet();
                        System.out.println("call: "+item);
                    }, 
                    fail -> System.out.println("fail: "+fail), 
                    () -> System.out.println("complete")
                );

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
