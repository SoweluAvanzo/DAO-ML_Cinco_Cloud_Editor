create table GitInformationDB (
    id bigint not null,
    type varchar(255) not null,
    repositoryUrl varchar(255) not null,
    username varchar(255),
    password varchar(255),
    branch varchar(255),
    genSubdirectory varchar(255),
    project_id bigint,
    primary key (id)
);

alter table GitInformationDB
    add constraint FK__GitInformationDB__ProjectDB
    foreign key (project_id)
    references ProjectDB;

alter table ProjectDB
    add column gitInformation_ID bigint;

alter table ProjectDB
    add constraint FK__ProjectDB__GitInformationDB
    foreign key (gitInformation_id)
    references GitInformationDB;