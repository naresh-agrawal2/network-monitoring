package com.dish.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "dish_ge_user_info")
public class User extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", nullable = false, insertable = false, updatable = false)
    private Role role;

    @Column(name = "roleId", nullable = false)
    private Integer roleId;

    private Boolean status;

    @Column(nullable = false)
    private Boolean isAuthenticated;

    @Column
    private LocalDate isAuthenticatedDate;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "BucketId" , insertable = false, updatable = false)
//    private Bucket bucket;
    
//    @Column(name = "BucketId")
//    private Integer BucketId;

    @Column(name = "otp")
    private String otp;

/*    public static User convertToUser(RegisterUserForm registerUserForm) {
        var user = new User();
        user.setUserName(registerUserForm.getUserName());
        user.setEmail(registerUserForm.getEmail());
        user.setStatus(registerUserForm.getStatus()); 
        user.setRoleId(registerUserForm.getRoleId());
        user.setBucketId(registerUserForm.getBucketId());  
        user.setIsAuthenticated(false);
        return user;
    }*/
}
