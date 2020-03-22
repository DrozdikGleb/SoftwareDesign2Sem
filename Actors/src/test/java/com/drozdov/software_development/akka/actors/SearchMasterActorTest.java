package com.drozdov.software_development.akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;
import com.drozdov.software_development.akka.search_api.MasterResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SearchMasterActorTest {
    static ActorSystem system;
    static int id = 0;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    private MasterResponse createRequest(String searchRequest) throws ExecutionException, InterruptedException {
        ActorRef parent = system.actorOf(Props.create(SearchMasterActor.class), "master" + id++);

        final Object result = PatternsCS.ask(parent, searchRequest, Timeout.apply(15, TimeUnit.SECONDS)).toCompletableFuture().join();
        return ((MasterResponse) result);
    }

    @Test
    public void testSuccessTest() throws ExecutionException, InterruptedException {
        String timeoutRequest = "goodRequest";
        MasterResponse masterResponse = createRequest(timeoutRequest);
        Assert.assertEquals(15, masterResponse.getItems().size());
    }

    @Test
    public void testWithTimeout() throws ExecutionException, InterruptedException {
        String timeoutRequest = "testTimeout";
        MasterResponse masterResponse = createRequest(timeoutRequest);
        Assert.assertEquals("Waiting for response from search aggregators more than 5 seconds", masterResponse.getItems().get(0));
    }
}
