alter table SettingsDB add column createDefaultProjects boolean;
update SettingsDB set createDefaultProjects = false;
alter table SettingsDB alter column createDefaultProjects set default false;
