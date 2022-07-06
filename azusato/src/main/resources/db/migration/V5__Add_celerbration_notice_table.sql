-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS celebration_notice CASCADE;

create table celebration_notice (
    no BIGINT not null AUTO_INCREMENT COMMENT '番号',
	-- ManyToOne
    celebration_no BIGINT not null COMMENT 'celerbrationテーブルのFK',
	-- ManyToOne
	celebration_reply_no BIGINT null COMMENT 'celerbration_replyテーブルのFK',
	readed BOOLEAN not null default 0 COMMENT '既読フラグ 既読すると : ture, 既読していない場合 : false',
	create_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	update_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	read_datetime TIMESTAMP COMMENT '既読した日時',
	target_user_no BIGINT not null COMMENT '通知対象ユーザ',
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no),
	FOREIGN KEY (celebration_no)
		REFERENCES celebration (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (celebration_reply_no)
		REFERENCES celebration_reply (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (target_user_no)
		REFERENCES user (no)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)
COMMENT='まだ確認していない人に対して通知のために';