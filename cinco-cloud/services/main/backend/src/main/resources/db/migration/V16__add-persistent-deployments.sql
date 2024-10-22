alter table SettingsDB
    add column persistentDeployments boolean not null default false;