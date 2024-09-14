package com.dish.auth.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "auth", uniqueConstraints = { @UniqueConstraint(columnNames = { "email"}) })
public class AuthEntity extends Auditable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "auth_id")
	private long authId;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;

}