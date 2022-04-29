alter table ProjectDB add column logo_id bigint;
alter table ProjectDB
    add constraint FK__ProjectDB_logo_id__BaseFileDB
        foreign key (logo_id)
            references BaseFileDB;
