/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grpc.service;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;

import io.grpc.stub.StreamObserver;

/**
 *
 * @author Eder_Crespo
 */
public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {

        StreamObserver requestObserver = new StreamObserver<GreetEveryoneRequest>() {

            @Override
            public void onNext(GreetEveryoneRequest value) {

                final String responseMessage = "Hello ".concat(value.getGreeting().getFirstName());

                final GreetEveryoneResponse response = GreetEveryoneResponse.newBuilder()
                        .setResult(responseMessage)
                        .build();

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable thrwbl) {
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestObserver;

    }

    // unary server method
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        final Greeting greeting = request.getGreeting();

        final String firstName = greeting.getFirstName();

        final String result = "Hello ".concat(firstName);

        final GreetResponse reponse = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        // send the response
        responseObserver.onNext(reponse);

        // complete the RPC call
        responseObserver.onCompleted();
    }

}
