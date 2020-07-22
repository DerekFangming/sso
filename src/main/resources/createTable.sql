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

create table sso_client_details (
	client_id text primary key,
	client_secret text,
	scope text,
	authorized_grant_types text,
	redirect_uri text,
	access_token_validity_seconds integer,
    refresh_token_validity_seconds integer
);