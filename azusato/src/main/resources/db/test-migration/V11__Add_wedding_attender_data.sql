delete from wedding_attender;
-- default
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark,attender_number) values 
    (1,'2023/01/09 12:35:31.570',1,'name','KOREA','remark',10);
-- 国籍
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark) values 
    (1,'2023/01/09 12:35:31.570',1,'name','JAPAN','remark');
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark) values 
    (1,'2023/01/09 12:35:31.570',1,'name','ETC','remark');
-- attend
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark) values 
    (0,'2023/01/09 12:35:31.570',1,'name','ETC','remark');
-- eatting
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark) values 
    (1,'2023/01/09 12:35:31.570',0,'name','ETC','remark');
-- division
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark) values 
    (1,'2023/05/01 12:35:31.570',0,'name','ETC','remark');
-- remark
insert into wedding_attender(attend,created_datetime,eatting,name,nationality,remark) values 
    (1,'2023/01/09 12:35:31.570',1,'name','KOREA',null);