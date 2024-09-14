package com.dish.auth.respository;

import com.dish.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {


	@Query(value = "SELECT * FROM dish.public.dish_ge_role role WHERE role.role_id = :role_id", nativeQuery = true)
	Optional<Role> fetchRoleDetails(@Param("role_id") Integer role_id);


/*	@Query(value = "SELECT role.role_name, role.role_description, role.features FROM dish.public.roles_master role WHERE role.role_id = :roleId", nativeQuery = true)
	Optional<Role> fetchRoleDetails(@Param("roleId") Integer roleId);*/


/*
	@Query("SELECT role_name, role_description, features FROM dish.public.roles_master WHERE role_id = :roleId", nativeQuery = true)
	Optional<Role> fetchRoleDetails(@Param("roleId") Integer roleId);
*/


}
