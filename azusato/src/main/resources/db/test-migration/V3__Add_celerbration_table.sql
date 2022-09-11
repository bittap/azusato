-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration CASCADE;

create table celebration (
    no BIGINT not null AUTO_INCREMENT COMMENT '番号',
    title VARCHAR(50) not null COMMENT 'タイトル',
	content_path VARCHAR(100) null  COMMENT '内容(htmlタグ)のパス',
	read_count INT not null default 0 COMMENT '読んだ数',
	create_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	update_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	create_user_no BIGINT not null,
	update_user_no BIGINT not null,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no),
	FOREIGN KEY (create_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (update_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='お祝い';