package com.dish.auth.respository;

import com.dish.auth.entity.AuthEntity;
import com.dish.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Page<User> findByUserNameContainingIgnoreCase(String userName, PageRequest pageRequest);
//
//	@Modifying
//	@Transactional
//	@Query("UPDATE User u SET u.BucketId = NULL WHERE u.BucketId = :BucketId")
//	int updateBucketIdToNull(@Param("BucketId") Integer id);


	@Query(value = "SELECT * FROM dish.public.dish_ge_user_info usr WHERE usr.email = :email", nativeQuery = true)
	Optional<User> fetchRoleId(@Param("email") String email);

	@Transactional
	@Modifying
	@Query(value = "UPDATE dish.public.dish_ge_user_info SET otp = :otp WHERE email = :email", nativeQuery = true)
	void updateOtp(@Param("otp") String otp, @Param("email") String email);

	@Transactional
	@Modifying
	@Query(value = "UPDATE dish.public.dish_ge_user_info SET is_authenticated = :is_authenticated, is_authenticated_date = :is_authenticated_date WHERE email = :email", nativeQuery = true)
	void updateIsAuth(@Param("is_authenticated") Boolean is_authenticated, @Param("is_authenticated_date") LocalDateTime is_authenticated_date, @Param("email") String email);

	@Query(value = "SELECT otp FROM dish.public.dish_ge_user_info usr WHERE usr.email = :email", nativeQuery = true)
	String fetchOTP(@Param("email") String email);

}
