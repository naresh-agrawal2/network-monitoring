package com.dish.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditorEntity {

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;

	@CreatedBy
	@Column(length = 50, nullable = false, updatable = false)
	private Integer createdBy;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedOn;

	@LastModifiedBy
	@Column(length = 50, nullable = false)
	private Integer updatedBy;

	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createdOn = now;
		this.updatedOn = now;

	}

	@PreUpdate
	public void preUpdate() {
		this.updatedOn = LocalDateTime.now();
	}

}
