create table GraphModelTypeDB (
    id bigint not null,
    typeName text not null,
    fileExtension text not null,
    project_id bigint not null,
    primary key (id),
    foreign key (project_id) references ProjectDB
);
