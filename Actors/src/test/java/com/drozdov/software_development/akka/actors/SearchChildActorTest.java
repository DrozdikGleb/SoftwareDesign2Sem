package com.drozdov.software_development.akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.drozdov.software_development.akka.search_api.SearchAggregator;
import com.drozdov.software_development.akka.search_api.SearchClientStub;
import com.drozdov.software_development.akka.search_api.SearchResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

public class SearchChildActorTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testIt() {
        new TestKit(system) {
            {
                final Props props = Props.create(SearchChildActor.class, new SearchClientStub(SearchAggregator.GOOGLE));
                final ActorRef subject = system.actorOf(props);

                within(Duration.ofSeconds(3),
                        () -> {
                            subject.tell("hello", getRef());
                            expectMsgAnyClassOf(SearchResponse.class);
                            return null;
                        });
            }
        };
    }
}
