-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS profile CASCADE;

create table profile (
    user_no BIGINT not null AUTO_INCREMENT COMMENT 'fk of user table',
    image_base64 MEDIUMTEXT null COMMENT 'プロフィール写真のbase64今はbase64を使い、後はストレージに保存する予定',
	image_type VARCHAR(10) null  COMMENT 'プロフィール写真のbase64のファイル拡張子',
	FOREIGN KEY (user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)
COMMENT='プロフィール';