///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.smallrye.reactive:mutiny:2.0.0

import java.util.List;
import java.util.function.*;
import io.smallrye.mutiny.Uni;

public class hellomutiny_uni {

    public static Uni<String> callMeFunction(String x){
        System.out.println("You just called me with: "+x);
        return Uni.createFrom().item("jeff");
    }

    public static Uni<String> callMeSupplier(){
        System.out.println("You just called me");
        return Uni.createFrom().item("jeff");
    }

    public static void callMeConsumer(String x){
        System.out.println("You just called me as a consumer with "+x);
    }

    public static void main(String... args) {
        Iterable<String> list = List.of("s1", "s2", "s3");
        System.out.println(list);

        // Basic Subscribe
        Uni.createFrom().item(() -> 1)
            .subscribe().with((i) -> System.out.println(i));


        // Transform
        Uni.createFrom().item("hello")
            .onItem().transform(h -> {
                return "changing "+h+" to goodbye";
            })
            .subscribe().with(item -> System.out.println(item));

        // Transform Int
        Uni.createFrom().item( () -> 1 )
            .onItem().transform( (i) -> {return i+2;})
            .subscribe().with(i -> System.out.println("transform int = "+i));

        // Async call
        Uni.createFrom().item("asyncCall")
            .onItem()
            .call(i -> callMeFunction(i))
            .call(i -> callMeSupplier())
            .invoke(i -> callMeConsumer(i))
            .subscribe().with(i -> System.out.println(i));
    }
}
