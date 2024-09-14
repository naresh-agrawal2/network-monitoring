package com.dish.auth.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dish.auth.entity.AuthEntity;



@Repository
@Transactional
public interface AuthRepository extends JpaRepository<AuthEntity, Long>{

	Optional<AuthEntity> findByEmail(String email);

	void deleteByEmail(String email);


	@Transactional
	@Modifying
	@Query(value = "UPDATE dish.public.auth SET token = :token, last_login_time = NOW() WHERE email = :email", nativeQuery = true)
	void updateToken(@Param("token") String token, @Param("email") String email);


}
