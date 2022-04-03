-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS user CASCADE;

create table user (
    no INT not null AUTO_INCREMENT COMMENT '番号',
    id VARCHAR(50) not null COMMENT 'ユーザID',
    password varchar(50) not null COMMENT 'パスワード',
	profile_no INT not null COMMENT 'プロフィール管理テーブルFK',
	TYPE ENUM('admin','nonmember','kakao','line') not null COMMENT 'admin(管理者),nonmember(非ログイン),kakao(カカオログイン),line(Lineログイン)',
	create_datetime TIMESTAMP not null,
	update_datetime TIMESTAMP not null,
	delete_flag BOOLEAN not null default 0 COMMENT '基本 false',
	PRIMARY KEY (no),
	CONSTRAINT UC_id UNIQUE (id)
/* 
The ordering problem, This is excuted V3__Alter_user_table.sql
	FOREIGN KEY (profile_no)
		...
*/
/*
 the fk for create_user, update_user is omitted
 refer to https://stackoverflow.com/questions/71723279/how-to-maintain-integrity-between-two-columns-on-a-table-in-mysql?noredirect=1#comment126753549_71723279
*/
)
COMMENT='ユーザ';