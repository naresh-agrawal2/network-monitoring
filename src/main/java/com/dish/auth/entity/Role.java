package com.dish.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "dish_ge_role")
public class Role extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer role_id;

    private String roleName;

    private String roleDescription;

    private String featuresIds;
    
//    @Transient
//    private Set<Integer> permissionIds = new HashSet<>();

}
