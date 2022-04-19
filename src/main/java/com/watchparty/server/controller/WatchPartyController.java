package com.watchparty.server.controller;

import com.watchparty.server.config.KafkaTopicConfig;
import com.watchparty.server.model.PublishObject;
import com.watchparty.server.model.Room;
import com.watchparty.server.repository.WatchPartyRepo;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class WatchPartyController {
    @Autowired
    WatchPartyRepo watchPartyRepo;
    @Autowired
    KafkaTemplate<String , PublishObject> kafkaTemplate;

    KafkaTopicConfig kafkaConfigObj = new KafkaTopicConfig();
    private static KafkaAdmin kafkaAdmin = new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092"));

    @Bean
    public void createTopicforAllRoomId(){
        try{
            for(Room room: watchPartyRepo.findAll() ){
                kafkaAdmin.createOrModifyTopics(TopicBuilder.name(room.getRoomTopicId()).build());
            }
        }catch(Exception e){
            System.out.println("Exception occured while creating topics forall Rooms");
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/getAllRooms")
    public ResponseEntity<List<Room>> getAllRooms(){
        try{
            return ResponseEntity.ok().body(watchPartyRepo.findAll());
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createRoom")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        try{
            Room _room = watchPartyRepo.save(new Room(room.getRoomTitle(),room.getMediaPath()));
            kafkaAdmin.createOrModifyTopics(TopicBuilder.name(_room.getRoomTopicId()).build());
            return new ResponseEntity<>(_room, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/publishEvent")
    public ResponseEntity<Map<String ,String>> publishEvent(@RequestBody PublishObject publishObject){
        try{
            System.out.println(publishObject.getRoomTopicId());
            Map<String , String> response = new HashMap<>();
            kafkaTemplate.send(publishObject.getRoomTopicId(), publishObject);
            System.out.println("Sent message to broker");
            response.put("message", "successfully published the event");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
