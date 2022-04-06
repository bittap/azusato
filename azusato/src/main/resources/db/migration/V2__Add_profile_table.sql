-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS profile CASCADE;

create table profile (
    no INT not null AUTO_INCREMENT COMMENT '番号',
    image_base64 MEDIUMTEXT not null COMMENT 'プロフィール写真のbase64今はbase64を使い、後はストレージに保存する予定',
	image_type VARCHAR(10) not null  COMMENT 'プロフィール写真のbase64のファイル拡張子',
	create_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	update_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no)
)
COMMENT='プロフィール';