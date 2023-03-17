alter table SettingsDB
    add column autoActivateUsers boolean not null default false;

alter table SettingsDB
    add column sendMails boolean not null default true;

alter table UserDB
    add column isDeactivatedByAdmin boolean not null default false;