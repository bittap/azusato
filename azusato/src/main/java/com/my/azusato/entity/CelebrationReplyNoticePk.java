package com.my.azusato.entity;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CelebrationReplyNoticePk implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "celebration_reply_no")
	private Long celebrationReplyNo;
	
	@Column(name = "user_no")
	private Long userNo;

}
