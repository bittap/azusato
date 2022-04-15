-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration_reply CASCADE;

create table celebration_reply (
    celebration_no INT not null COMMENT 'お祝いテーブルのFK',
    content VARCHAR(500) not null COMMENT '内容',
	create_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	update_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	create_user_no INT not null,
	update_user_no INT not null,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (celebration_no),
	FOREIGN KEY (create_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	FOREIGN KEY (update_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='お祝い書き込み';