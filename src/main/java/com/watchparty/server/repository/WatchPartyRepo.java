package com.watchparty.server.repository;

import com.watchparty.server.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WatchPartyRepo extends JpaRepository<Room , Long> {
}
