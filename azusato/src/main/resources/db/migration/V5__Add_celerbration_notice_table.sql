-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration_notice CASCADE;

create table celebration_notice (
    celerbration_no INT not null COMMENT 'celerbrationテーブルのFK',
    user_no INT not null COMMENT 'userテーブルのFK',
	PRIMARY KEY (celerbration_no,user_no),
	FOREIGN KEY (celerbration_no)
		REFERENCES celebration (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	FOREIGN KEY (user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='まだ確認していない人に対して通知のために(お祝い)';