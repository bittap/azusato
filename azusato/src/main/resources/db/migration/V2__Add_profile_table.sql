-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS profile CASCADE;

create table profile (
    no INT not null AUTO_INCREMENT COMMENT '番号',
    profile_image_base64 MEDIUMTEXT not null COMMENT 'プロフィール写真のbase64今はbase64を使い、後はストレージに保存する予定',
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
COMMENT='プロフィール';