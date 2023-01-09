-- DO not modify create table if the app is relased.
DROP TABLE IF EXISTS wedding_attender  CASCADE;

create table wedding_attender (
  no bigint auto_increment not null comment '番号'
  , attend bit(1) not null comment '参加有無'
  , created_datetime datetime(6) not null comment '生成日時'
  , eatting bit(1) not null comment '食事有無'
  , name varchar(10) not null comment 'ネーム'
  , nationality enum('KOREA','JAPAN','ETC') not null comment '国籍'
  , remark varchar(1000) comment '備考'
  , PRIMARY KEY (no)
)
COMMENT='結婚式参加';