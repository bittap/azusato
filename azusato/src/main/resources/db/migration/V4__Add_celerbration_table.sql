-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celerbration CASCADE;

create table celerbration (
    no INT not null AUTO_INCREMENT COMMENT '番号',
    title VARCHAR(50) not null COMMENT 'タイトル',
	content LONGTEXT not null COMMENT '内容(htmlタグ)',
	read_count INT not null default 0 COMMENT '読んだ数',
	create_datetime TIMESTAMP not null,
	create_user_no INT not null,
	update_datetime TIMESTAMP not null,
	update_user_no INT not null,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no),
	FOREIGN KEY (create_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	FOREIGN KEY (update_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='お祝い';