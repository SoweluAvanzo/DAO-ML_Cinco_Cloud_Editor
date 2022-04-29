alter table OrganizationDB drop constraint FK__OrganizationDB__StyleDB;
alter table OrganizationDB drop column style_id;
alter table OrganizationDB add column logo_id bigint;
alter table OrganizationDB
    add constraint FK__OrganizationDB_logo_id__BaseFileDB
        foreign key (logo_id)
            references BaseFileDB;

alter table SettingsDB drop constraint FK__SettingsDB__StyleDB;
alter table SettingsDB drop column style_id;
drop table StyleDB;
