-- 結婚式参加テーブルにカラムを追加
ALTER TABLE wedding_attender ADD COLUMN attender_number TINYINT default 1 NOT NULL;
ALTER TABLE wedding_attender MODIFY attender_number TINYINT COMMENT '参加者数';