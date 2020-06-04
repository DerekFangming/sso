create table sso_users (
	id serial primary key,
	username text unique not null,
	password text,
	type integer not null,
	access_token text,
	confirm_code text,
	confirmed boolean not null,
	created_at timestamp without time zone not null,
	name text,
	bio text,
	avatar text,
	enabled boolean not null,
	reputation integer not null
);