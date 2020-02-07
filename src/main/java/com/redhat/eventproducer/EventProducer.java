package com.redhat.eventproducer;

import java.util.*;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;

@Path("/events")
@ApplicationScoped
public class EventProducer {

    @POST
    @Path("/txn-event/{custId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void postCase(String json,String customerId) {

        try {
           CustomerEvents customerEvents = new CustomerEvents();
            customerEvents.setCustId(customerId);
            customerEvents.setEvent(json);
            events.add(customerEvents);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final Logger LOG = Logger.getLogger(EventProducer.class);

    private List<CustomerEvents> events = new ArrayList<>();



    @Outgoing("event-input-stream")
    public Flowable<KafkaMessage<String, String>> generate() {
        List<KafkaMessage<String, String>> jsonVal = events.stream()
                .map(s -> KafkaMessage.of(
                        s.getCustId(),
                        s.getEvent()))
                .collect(Collectors.toList());

        return Flowable.fromIterable(jsonVal);


    }




}