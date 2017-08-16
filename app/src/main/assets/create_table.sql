CREATE TABLE IF NOT EXISTS CONTENTS(id integer primary key, category_name text not null, contents text not null);
CREATE TABLE IF NOT EXISTS CATEGORY(id integer primary key, category_name text not null);/
INSERT INTO CATEGORY VALUES(0,"MY_CLIP");
