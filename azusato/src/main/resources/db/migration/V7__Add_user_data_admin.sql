insert into user(id,name,password,user_type,create_datetime,update_datetime,delete_flag) values 
    ('azuchan7','山本あずさ','{bcrypt}$2a$10$BKeHGsHWkQkwl5I/QwmaFO09wU.nWTk6ivqN42QHdYO/ZdEIszZtO','admin',NOW(),NOW(),'0')
  , ('bittap','김태영','{bcrypt}$2a$10$1U9YQdFgEsjO3JJMHkIS3.z16nnzBUZqpLJEBr.qfegrC2NIW9qhC','admin',NOW(),NOW(),'0');

insert into profile(image_base64,image_type) values 
    (null,null)
  , (null,null);
