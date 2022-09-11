-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS user CASCADE;

create table user (
    no BIGINT not null AUTO_INCREMENT COMMENT '番号',
    id VARCHAR(50) not null COMMENT 'ユーザID',
    name VARCHAR(10) null COMMENT 'ユーザID',
    password varchar(200) null COMMENT 'パスワード',
	user_type VARCHAR(10) not null COMMENT 'admin(管理者),nonmember(非ログイン),kakao(カカオログイン),line(Lineログイン)',
	create_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	update_datetime TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no),
	CONSTRAINT UC_id UNIQUE (id)
)
COMMENT='ユーザ';