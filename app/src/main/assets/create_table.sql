CREATE TABLE IF NOT EXISTS CONTENTS(id integer, category_name text, contents text, PRIMARY KEY(id, category_name));
CREATE TABLE IF NOT EXISTS CATEGORY(id integer, category_name text, PRIMARY KEY(id, category_name));/
INSERT INTO CATEGORY VALUES(1,"MY_CLIP");
