alter table WorkspaceImageDB add column featured boolean;
update WorkspaceImageDB set featured = false;
alter table WorkspaceImageDB alter column featured set default false;
alter table WorkspaceImageDB alter column featured set not null;
