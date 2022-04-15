-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration_reply_notice CASCADE;

create table celebration_reply_notice (
    celebration_reply_no INT not null COMMENT 'celerbration_replyテーブルのFK',
    user_no INT not null COMMENT 'userテーブルのFK',
	PRIMARY KEY (celebration_reply_no,user_no),
	FOREIGN KEY (celebration_reply_no)
		REFERENCES celebration_reply (celebration_no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	FOREIGN KEY (user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='まだ確認していない人に対して通知のために(お祝い書き込み)';