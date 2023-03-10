package com.ratwater.jbride;

import java.util.function.Predicate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.mutiny.Multi;


// https://smallrye.io/smallrye-mutiny/

@Path("/mutinyTest")
public class MutinyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        Predicate<String> endsWithA = t -> t.startsWith("T");
        boolean endsWithResult = endsWithA.equals("T");
        Predicate<String> startsWithA = t -> t.startsWith("A");
        boolean result = startsWithA.test("Jeff");


        Multi                                                                             // a Multi is an object that emits events (aka in Reactive lingo:  Publisher)
            .createFrom()
            .items(new House("\nwazee"), new House("\nben moore"), new House("\ncomo"))   // emit three items
            .subscribe().with(onItem -> System.out.println(onItem.getName()));            // items published to final consumer: subscriber

        Multi
            .createFrom()
            .items("\n\njeff", "\njessica")
            .subscribe().with(System.out::print);
        return "hello Mutiny";
    }
}

class House {
    String name;

    public House(String x) {
        name = x;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
