create table ProjectDB_members (
    ProjectDB_id bigint not null,
    UserDB_id bigint not null
);

alter table if exists ProjectDB_members
    add constraint FK__ProjectDB_members__UserDB
    foreign key (UserDB_id)
    references UserDB;

alter table if exists ProjectDB_members
    add constraint FK__ProjectDB_members__ProjectDB
    foreign key (ProjectDB_id)
    references ProjectDB;

update ProjectDB set owner_UserDB_id = null
    where organization_OrganizationDB_id is not null;

alter table SettingsDB
    drop column globallyCreateOrganizations;

delete
    from UserDB_systemRoles
    where systemRoles = 'ORGANIZATION_MANAGER';

alter table WorkspaceImageDB
    add column uuid uuid not null,
    drop column user_id,
    drop column name,
    drop column imagename;
