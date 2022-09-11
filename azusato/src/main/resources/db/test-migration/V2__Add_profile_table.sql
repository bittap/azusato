-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS profile CASCADE;

create table profile (
    user_no BIGINT not null AUTO_INCREMENT COMMENT 'fk of user table',
	image_path VARCHAR(100) null  COMMENT 'プロフィール写真のファイルパス',
	FOREIGN KEY (user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)
COMMENT='プロフィール';