-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration_notice CASCADE;

create table celebration_notice (
    celebration_no BIGINT not null COMMENT 'celerbrationテーブルのFK',
    user_no BIGINT not null COMMENT 'userテーブルのFK',
	PRIMARY KEY (celebration_no,user_no),
	FOREIGN KEY (celebration_no)
		REFERENCES celebration (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)
COMMENT='まだ確認していない人に対して通知のために(お祝い)';