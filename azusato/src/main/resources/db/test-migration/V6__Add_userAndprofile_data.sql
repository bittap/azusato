delete from user;

insert into user(no,id,name,password,user_type,create_datetime,update_datetime,delete_flag) values 
    (1,'unique1','varchar2','{bcrypt}$2a$10$l6xjkReOd8LlLB7v3z7fB.dZKCGP36sYI0JWBzebvXX0vOCvKh.rG','admin','2022-01-02 03:04:05','2022-06-07 08:09:10','0');

insert into profile(user_no,image_path)  values (1,'varchar1');

insert into user(no,id,name,password,user_type,create_datetime,update_datetime,delete_flag) values 
    (2,'unique2','varchar2','{bcrypt}$2a$10$l6xjkReOd8LlLB7v3z7fB.dZKCGP36sYI0JWBzebvXX0vOCvKh.rG','nonmember','2022-01-02 03:04:05','2022-06-07 08:09:10','0');

insert into profile(user_no,image_path)  values (2,'varchar1');

insert into user(no,id,name,password,user_type,create_datetime,update_datetime,delete_flag) values 
    (3,'unique3','varchar2','{bcrypt}$2a$10$l6xjkReOd8LlLB7v3z7fB.dZKCGP36sYI0JWBzebvXX0vOCvKh.rG','kakao','2022-01-02 03:04:05','2022-06-07 08:09:10','0');

insert into profile(user_no,image_path)  values (3,'varchar1');

insert into user(no,id,name,password,user_type,create_datetime,update_datetime,delete_flag) values 
    (4,'unique4','varchar2','{bcrypt}$2a$10$l6xjkReOd8LlLB7v3z7fB.dZKCGP36sYI0JWBzebvXX0vOCvKh.rG','line','2022-01-02 03:04:05','2022-06-07 08:09:10','0');

insert into profile(user_no,image_path)  values (4,'varchar1');

insert into user(no,id,name,password,user_type,create_datetime,update_datetime,delete_flag) values 
    (5,'unique5','varchar2','{bcrypt}$2a$10$l6xjkReOd8LlLB7v3z7fB.dZKCGP36sYI0JWBzebvXX0vOCvKh.rG','admin','2022-01-02 03:04:05','2022-06-07 08:09:10','0');

insert into profile(user_no,image_path)  values (5,'varchar1');
