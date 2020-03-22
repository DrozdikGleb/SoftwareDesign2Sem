package com.drozdov.software_development.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.drozdov.software_development.akka.actors.SearchMasterActor;
import com.drozdov.software_development.akka.search_api.MasterResponse;
import com.drozdov.software_development.akka.search_api.SearchResponse;
import scala.concurrent.Future;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MySystem");
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("Введите запрос \n");
            String searchRequest = in.nextLine();
            if (searchRequest.equals("exit")) {
                break;
            }

            ActorRef parent = system.actorOf(Props.create(SearchMasterActor.class), "master");

            ClassTag<MasterResponse> tag = ClassTag$.MODULE$.apply(MasterResponse.class);
            Future<MasterResponse> fut = Patterns.ask(parent, searchRequest, Timeout.apply(15, TimeUnit.SECONDS)).mapTo(tag);

            fut.onComplete(new OnComplete<MasterResponse>() {
                @Override
                public void onComplete(Throwable failure, MasterResponse masterResponse) {
                    if (masterResponse.isFailed()) {
                        System.out.println(masterResponse.getItems().get(0));
                        System.exit(0);
                    } else {
                        masterResponse.getItems().forEach(System.out::println);
                    }
                }

            }, system.dispatcher());
        }
    }
}
