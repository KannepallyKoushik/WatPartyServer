package com.watchparty.server.controller;

import com.watchparty.server.config.KafkaTopicConfig;
import com.watchparty.server.model.AddMediaObj;
import com.watchparty.server.model.JoinRoomObj;
import com.watchparty.server.model.PublishObject;
import com.watchparty.server.model.Room;
import com.watchparty.server.repository.WatchPartyRepo;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class WatchPartyController {
    RestTemplate restTemplate = new RestTemplate();
    String authUrl = "http://localhost:8080/api/user/verifyToken";

    @Autowired
    WatchPartyRepo watchPartyRepo;
    @Autowired
    KafkaTemplate<String , PublishObject> kafkaTemplate;

    KafkaTopicConfig kafkaConfigObj = new KafkaTopicConfig();
    private static KafkaAdmin kafkaAdmin = new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092"));

    public String checkAuthorisedUser(String bearerToken){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", bearerToken);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<String> response = new RestTemplate().exchange(authUrl, HttpMethod.GET, entity, String.class);
            return response.getBody();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public Boolean authorise(String bearerToken){
//        String response = checkAuthorisedUser(bearerToken);
//        if(response.equals("true")){
//            return true;
//        }else{
//            return false;
//        }
        return true;
    }

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
    public ResponseEntity<List<Room>> getAllRooms(HttpServletRequest request){
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorise(authorizationHeader)){
                return ResponseEntity.ok().body(watchPartyRepo.findAll());
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createRoom")
    public ResponseEntity<Room> createRoom(@RequestBody Room room,HttpServletRequest request) {
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorise(authorizationHeader)){
                Room _room = watchPartyRepo.save(new Room(room.getRoomTitle(),room.getMediaPath()));
                kafkaAdmin.createOrModifyTopics(TopicBuilder.name(_room.getRoomTopicId()).build());
                return new ResponseEntity<>(_room, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/joinRoom")
    public ResponseEntity<Map<String ,String>> joinRoom(@RequestBody JoinRoomObj obj,HttpServletRequest request) {
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorise(authorizationHeader)){
                Map<String , String> response = new HashMap<>();
                Room room = watchPartyRepo.findByRoomTopicId(obj.getRoomTopicId());
                if(room != null){
                    response.put("mediaPath",room.getMediaPath());
                    return new ResponseEntity<>(response, HttpStatus.CREATED);
                }else{
                    response.put("message","Theatre Room Does not Exist");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/addMedia")
    public ResponseEntity<Map<String ,String>> addMedia(@RequestBody AddMediaObj mediaObj,HttpServletRequest request){
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorise(authorizationHeader)){
                Map<String , String> response = new HashMap<>();
                Room room = watchPartyRepo.findByRoomTopicId(mediaObj.getRoomTopicId());
                if (room != null) {
                    room.setMediaPath(mediaObj.getMediaPath());
                    room.setLastUpdatedTime(LocalDateTime.now());
                    watchPartyRepo.save(room);
                } else {
                    response.put("message","Theatre Room Does not Exist");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                response.put("message","Succesfully Updated");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/publishEvent")
    public ResponseEntity<Map<String ,String>> publishEvent(@RequestBody PublishObject publishObject,HttpServletRequest request){
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
