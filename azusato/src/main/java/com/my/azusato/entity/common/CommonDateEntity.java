package com.my.azusato.entity.common;

import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonDateEntity {

	private LocalDateTime createDatetime;
	
	private LocalDateTime updateDatetime;
}
