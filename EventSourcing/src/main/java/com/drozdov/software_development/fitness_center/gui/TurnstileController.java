package com.drozdov.software_development.fitness_center.gui;

import com.drozdov.software_development.fitness_center.coreapi.EnterClientCommand;
import com.drozdov.software_development.fitness_center.coreapi.ExitClientCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/turnstile")
@RestController
public class TurnstileController {
    private final CommandGateway commandGateway;

    public TurnstileController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @GetMapping("/enter/{subscriptionId}")
    public CompletableFuture<String> enter(@PathVariable("subscriptionId") UUID subscriptionId) {
        return commandGateway.send(new EnterClientCommand(subscriptionId)).thenApply(v ->
                String.format("Client with subscriptionId %s has entered", subscriptionId)
        );
    }

    @GetMapping("/exit/{subscriptionId}")
    public CompletableFuture<String> exit(@PathVariable("subscriptionId") UUID subscriptionId) {
        return commandGateway.send(new ExitClientCommand(subscriptionId)).thenApply(v ->
                String.format("Client with subscriptionId %s has exited", subscriptionId)
        );
    }
}
