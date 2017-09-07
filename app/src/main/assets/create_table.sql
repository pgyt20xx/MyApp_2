CREATE TABLE IF NOT EXISTS CONTENTS(id integer primary key, category_name text primary key not null, contents_title text not null, contents text);
CREATE TABLE IF NOT EXISTS CATEGORY(id integer primary key, category_name text primary key not null);/
INSERT INTO CATEGORY VALUES(0,"CLIPBOARD");
