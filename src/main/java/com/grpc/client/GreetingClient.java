/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grpc.client;

//import grpc.DummyServiceGrpc;
import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eder_Crespo
 */
public class GreetingClient extends ClientChannelService {

    public static void main(String[] args) {
        new GreetingClient();
    }

    @Override
    protected void performRequest(ManagedChannel channel) {
        performBiDirectionalOperation(channel);
    }
    
    private void performBiDirectionalOperation(ManagedChannel channel) {
        
        final GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        
        final CountDownLatch latch = new CountDownLatch(1);
        
        final StreamObserver<GreetEveryoneRequest> request = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {

            @Override
            public void onNext(GreetEveryoneResponse v) {
                System.out.println("Response from server ".concat(v.getResult()));
            }

            @Override
            public void onError(Throwable thrwbl) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data!");
                latch.countDown();
            }
        });
        
        Arrays.asList("John", "Mary", "Peter").forEach(name -> request.onNext(createRequest(name)));
        
        request.onCompleted();
        
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }    
    }
    
    private GreetEveryoneRequest createRequest(String name) {
        return GreetEveryoneRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName(name)
                        .build())
                .build();
    }
    
    private void performUnaryOperation(ManagedChannel channel) {

        System.out.println("I am a client");

        System.out.println("Creating a stub");
        // sync call
        final GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        final Greeting greeting = Greeting.newBuilder()
                .setFirstName("Jonny")
                .setSecondName("Walker")
                .build();

        final GreetRequest request = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        final GreetResponse response = greetClient.greet(request);

        System.out.println(response.getResult());
    }

}
