/*
 * Initial script to create the database schema for Cinco Cloud
 */

create sequence hibernate_sequence start 1 increment 1;

create table BaseFileDB (
    id bigint not null,
    contentType varchar(255),
    fileExtension varchar(255),
    filename varchar(255),
    "path" varchar(255),
    primary key (id)
);

create table OrganizationAccessRightVectorDB (
    id bigint not null,
    organization_id bigint,
    user_id bigint,
    primary key (id)
);

create table OrganizationAccessRightVectorDB_accessRights (
    OrganizationAccessRightVectorDB_id bigint not null,
    accessRights varchar(255)
);

create table OrganizationDB (
    id bigint not null,
    description varchar(255),
    name varchar(255),
    style_id bigint,
    primary key (id)
);

create table OrganizationDB_members (
    OrganizationDB_id bigint not null,
    UserDB_id bigint not null
);

create table OrganizationDB_owners (
    OrganizationDB_id bigint not null,
    UserDB_id bigint not null
);

create table OrganizationDB_ProjectDB (
    OrganizationDB_id bigint not null,
    projects_id bigint not null
);

create table ProjectDB (
    id bigint not null,
    description varchar(255),
    name varchar(255),
    type varchar(255),
    image_id bigint,
    organization_OrganizationDB_id bigint,
    owner_UserDB_id bigint,
    template_id bigint,
    primary key (id)
);

create table SettingsDB (
    id bigint not null,
    globallyCreateOrganizations boolean not null,
    style_id bigint,
    primary key (id)
);

create table StyleDB (
    id bigint not null,
    bodyBgColor varchar(255),
    bodyTextColor varchar(255),
    navBgColor varchar(255),
    navTextColor varchar(255),
    primaryBgColor varchar(255),
    primaryTextColor varchar(255),
    logo_id bigint,
    profilePicture_id bigint,
    primary key (id)
);

create table UserDB (
    id bigint not null,
    activationKey varchar(255),
    email varchar(255),
    isActivated boolean not null,
    password varchar(255),
    username varchar(255),
    profilePicture_id bigint,
    primary key (id)
);

create table UserDB_systemRoles (
    UserDB_id bigint not null,
    systemRoles varchar(255)
);

create table WorkspaceImageBuildJobDB (
    id bigint not null,
    finishedAt timestamp,
    startedAt timestamp,
    status varchar(255),
    project_UserDB_id bigint,
    primary key (id)
);

create table WorkspaceImageDB (
    id bigint not null,
    createdAt timestamp,
    imageName varchar(255),
    imageVersion varchar(255),
    name varchar(255),
    published boolean not null,
    updatedAt timestamp,
    project_id bigint,
    user_id bigint,
    primary key (id)
);

create table StopProjectPodsTaskDB (
    id bigint not null,
    createdAt timestamp,
    projectId bigint,
    primary key (id)
);

alter table if exists OrganizationDB_ProjectDB
    add constraint UK__OrganizationDB_ProjectDB__projects_id unique (projects_id);

alter table if exists OrganizationAccessRightVectorDB
    add constraint FK__OrganizationAccessRightVectorDB__OrganizationDB
    foreign key (organization_id)
    references OrganizationDB;

alter table if exists OrganizationAccessRightVectorDB
    add constraint FK__OrganizationAccessRightVectorDB__UserDB
    foreign key (user_id)
    references UserDB;

alter table if exists OrganizationAccessRightVectorDB_accessRights
    add constraint FK__OrgAccessRightVectorDB_accessRights__OrgAccessRightVectorDB
    foreign key (OrganizationAccessRightVectorDB_id)
    references OrganizationAccessRightVectorDB;

alter table if exists OrganizationDB
    add constraint FK__OrganizationDB__StyleDB
    foreign key (style_id)
    references StyleDB;

alter table if exists OrganizationDB_members
    add constraint FK__OrganizationDB_members__UserDB
    foreign key (UserDB_id)
    references UserDB;

alter table if exists OrganizationDB_members
    add constraint FK__OrganizationDB_members__OrganizationDB
    foreign key (OrganizationDB_id)
    references OrganizationDB;

alter table if exists OrganizationDB_owners
    add constraint FK__OrganizationDB_owners__UserDB
    foreign key (UserDB_id) 
    references UserDB;

alter table if exists OrganizationDB_owners
    add constraint FK__OrganizationDB_owners__OrganizationDB
    foreign key (OrganizationDB_id)
    references OrganizationDB;

alter table if exists OrganizationDB_ProjectDB
    add constraint FK__OrganizationDB_ProjectDB__ProjectDB
    foreign key (projects_id)
    references ProjectDB;

alter table if exists OrganizationDB_ProjectDB
    add constraint FK__OrganizationDB_ProjectDB__OrganizationDB
    foreign key (OrganizationDB_id)
    references OrganizationDB;

alter table if exists ProjectDB
    add constraint FK__ProjectDB_image_id__WorkspaceImageDB
    foreign key (image_id)
    references WorkspaceImageDB;

alter table if exists ProjectDB
    add constraint FK__ProjectDB__OrganizationDB
    foreign key (organization_OrganizationDB_id)
    references OrganizationDB;

alter table if exists ProjectDB
    add constraint FK__ProjectDB__UserDB
    foreign key (owner_UserDB_id)
    references UserDB;

alter table if exists ProjectDB
    add constraint FK__ProjectDB_template_id__WorkspaceImageDB
    foreign key (template_id)
    references WorkspaceImageDB;

alter table if exists SettingsDB
    add constraint FK__SettingsDB__StyleDB
    foreign key (style_id)
    references StyleDB;

alter table if exists StyleDB
    add constraint FK__StyleDB_logo_id__BaseFileDB
    foreign key (logo_id)
    references BaseFileDB;

alter table if exists StyleDB
    add constraint FK__StyleDB_profilePicture_id__BaseFileDB
    foreign key (profilePicture_id)
    references BaseFileDB;

alter table if exists UserDB
    add constraint FK__UserDB__BaseFileDB
    foreign key (profilePicture_id)
    references BaseFileDB;

alter table if exists UserDB_systemRoles
    add constraint FK__UserDB_systemRoles__UserDB
    foreign key (UserDB_id)
    references UserDB;

alter table if exists WorkspaceImageBuildJobDB
    add constraint FK__WorkspaceImageBuildJobDB__ProjectDB
    foreign key (project_UserDB_id)
    references ProjectDB;

alter table if exists WorkspaceImageDB
    add constraint FK__WorkspaceImageDB__ProjectDB
    foreign key (project_id)
    references ProjectDB;

alter table if exists WorkspaceImageDB
    add constraint FK__WorkspaceImageDB__UserDB
    foreign key (user_id) 
    references UserDB;
