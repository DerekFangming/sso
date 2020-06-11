create table sso_users (
	id serial primary key,
	username text unique not null,
	password text,
	role text,
	confirm_code text,
	confirmed boolean not null,
	created_at timestamp without time zone not null
);