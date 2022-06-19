-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration_notice CASCADE;

create table celebration_notice (
    no BIGINT not null AUTO_INCREMENT COMMENT '番号',
	-- ManyToOne
    celebration_no BIGINT not null COMMENT 'celerbrationテーブルのFK',
	-- ManyToOne
	celebration_reply_no BIGINT null COMMENT 'celerbration_replyテーブルのFK',
	readed BOOLEAN not null default 0 COMMENT '読んでたら : ture, 読んでなかったら : false',
	create_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	update_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	create_user_no BIGINT not null,
	update_user_no BIGINT not null,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no),
	FOREIGN KEY (celebration_no)
		REFERENCES celebration (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (celebration_reply_no)
		REFERENCES celebration_reply (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (create_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (update_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='まだ確認していない人に対して通知のために';