package com.ultrasound.app.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.Id;

import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "roles")
public class Role {
    @Id
    private String id;
    private ERole name;
    public Role(ERole name) {
        this.name = name;
    }

}
