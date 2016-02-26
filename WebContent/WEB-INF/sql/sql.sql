insert into userinfo("user_id", "user_pw") values ('admin', 'admin');

select * from userInfo;

select * from chatInfo;

select * from productlist;

select * from productreply;

select * from following;
select * from follower;

select * from test;

select * from kkma;
select * from kkma where _index2 = '247';
select count(*) from kkma;

insert into kkma (_index) values ('179');
alter table kkma add '1' varchar(300) default(0);

alter table following add 'yys' varchar(300) default(0);

select count(*) from information_schema.columns where table_name = 'test';

select column_name from information_schema.columns where table_name = 'kkma';
select column_name from information_schema.columns where table_name = ?


select user_id, index from userinfo;

select * from productlist where index between 0 and 10
select * from productlist where index between 11 and 20


select * from productlist join userInfo on productlist.email = userInfo.user_id;



select pi_164 from test;

alter table test add user_name varchar(300);

alter table userhistory add product_index varchar(300);

alter table test drop p8;



insert into test (user_name) values ('yys2');

update test set p1 = '1' where user_name = 'yys';

select user_name from test where pi_164 = '1';

select * from test where user_name = 'yys' and user_name = 'CROSS';





delete from userinfo where user_id = 'sik@gmail.com';

delete from productlist;

delete from productreply;

delete from chatinfo where index = 3;

delete from chatinfo;

delete from userinfo;

delete from chatinfo;

delete from test;

delete from test where p4 = '0';

delete from kkma;





create table arabic(
	word varchar(60) not null,
	arabic_id serial not null primary key
);

load DATA local infile 'arabicwords.txt' into table arabic lines terminated by '\n' set arabic_id = null;