package com.watchparty.server.controller;

import com.watchparty.server.model.Room;
import com.watchparty.server.repository.WatchPartyRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class WatchPartyController {
    public List<Room> allRooms = new ArrayList<Room>();
    @Bean
    public void getAllRooms(){
        List<Room> rooms = allRooms;
        for (Room room : WatchPartyRepo.findAll()) {
            allRooms.add(room);
        }
    }

}
