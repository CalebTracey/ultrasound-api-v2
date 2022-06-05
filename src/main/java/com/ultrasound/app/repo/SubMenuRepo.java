package com.ultrasound.app.repo;

import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.model.user.ERole;
import com.ultrasound.app.model.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubMenuRepo extends MongoRepository<SubMenu, String> {
}
