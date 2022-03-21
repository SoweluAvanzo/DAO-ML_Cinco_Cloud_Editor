create extension citext;

alter table OrganizationDB
    alter column name type citext;

alter table UserDB
    alter column username type citext,
    alter column email type citext;

alter table OrganizationDB
    add unique (name);

alter table UserDB
    add unique (username),
    add unique (email);
