* ENCODING = UTF-8
* load TEXT_CLOB text.txt

* prepare TEXT_CLOB
insert into blob_test (clob_value) values (?);