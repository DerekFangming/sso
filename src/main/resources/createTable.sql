create table sso_users (
	id serial primary key,
	username text unique not null,
	password text,
	role text,
	confirmed boolean not null,
	confirm_code text,
	password_reset_code text,
	created_at timestamp without time zone not null
);