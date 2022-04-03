-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS user_celerbration_reply_unread CASCADE;

create table user_celerbration_reply_unread (
    celerbration_reply_no INT not null COMMENT 'celerbration_replyテーブルのFK',
    user_no INT not null COMMENT 'userテーブルのFK',
	PRIMARY KEY (celerbration_reply_no,user_no),
	FOREIGN KEY (celerbration_reply_no)
		REFERENCES celerbration_reply (celerbration_no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	FOREIGN KEY (user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
)
COMMENT='まだ確認していない人に対して通知のために(お祝い書き込み)';