package com.lblocki.privatecommunicatorserver.web;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
@RequiredArgsConstructor
public class TestWebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final String destination = "/topic/messages";
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private Future<?> submittedTask;

    @MessageMapping("/start")
    public void startTask(){
        if ( submittedTask != null ){
            simpMessagingTemplate.convertAndSend(destination, "Task already started");
            return;
        }
        simpMessagingTemplate.convertAndSend(destination, "Started task");
        submittedTask = executorService.submit(() -> {
            while(true){
                simpMessagingTemplate.convertAndSend(destination, LocalDateTime.now().toString() +": doing some work");
                Thread.sleep(10000);
            }
        });
    }

    @MessageMapping("/stop")
    public void stopTask(){
        if ( submittedTask == null ){
            simpMessagingTemplate.convertAndSend(destination, "Task not running");
            return;
        }
        try {
            submittedTask.cancel(true);
        }catch (Exception ex){
            ex.printStackTrace();
            simpMessagingTemplate.convertAndSend(destination,  "Error occurred while stopping task due to: " + ex.getMessage());
            return;
        }
        simpMessagingTemplate.convertAndSend(destination, "Stopped task");
    }

}
