-- DROP SCHEMA public;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

-- DROP SEQUENCE public.account_roles_id_seq;

CREATE SEQUENCE public.account_roles_id_seq
    MINVALUE 0
    NO MAXVALUE
    START 0
	NO CYCLE;
-- DROP SEQUENCE public.role_permissions_id_seq;

CREATE SEQUENCE public.role_permissions_id_seq
    MINVALUE 0
    NO MAXVALUE
    START 0
	NO CYCLE;-- public.event_consumption definition

-- Drop table

-- DROP TABLE public.event_consumption;

CREATE TABLE public.event_consumption (
                                          id uuid NOT NULL,
                                          consumed_at timestamptz(6) NOT NULL,
                                          consumer varchar(255) NOT NULL,
                                          event_id varchar(255) NOT NULL,
                                          CONSTRAINT event_consumption_pkey null,
                                          CONSTRAINT uk_event_consumer null
);


-- public.event_publication definition

-- Drop table

-- DROP TABLE public.event_publication;

CREATE TABLE public.event_publication (
                                          completion_attempts int4 NOT NULL,
                                          completion_date timestamptz(6) NULL,
                                          last_resubmission_date timestamptz(6) NULL,
                                          publication_date timestamptz(6) NOT NULL,
                                          id uuid NOT NULL,
                                          event_type varchar(255) NOT NULL,
                                          listener_id text NOT NULL,
                                          serialized_event text NOT NULL,
                                          status varchar(255) NULL,
                                          CONSTRAINT event_publication_pkey PRIMARY KEY (id),
                                          CONSTRAINT event_publication_status_check CHECK (((status)::text = ANY ((ARRAY['PUBLISHED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'RESUBMITTED'::character varying])::text[])))
);

-- DROP SCHEMA notification;

CREATE SCHEMA notification AUTHORIZATION root;
-- notification.notification_messages definition

-- Drop table

-- DROP TABLE notification.notification_messages;

CREATE TABLE notification.notification_messages (
                                                    created_at timestamptz(6) NULL,
                                                    sent_at timestamptz(6) NULL,
                                                    id uuid NOT NULL,
                                                    template_id uuid NULL,
                                                    channel varchar(20) NOT NULL,
                                                    status varchar(20) NOT NULL,
                                                    reference_type varchar(50) NULL,
                                                    recipient varchar(320) NOT NULL,
                                                    "content" text NOT NULL,
                                                    reference_id varchar(255) NULL,
                                                    subject text NULL,
                                                    variables jsonb NULL,
                                                    CONSTRAINT notification_messages_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_notification_message_created_at ON notification.notification_messages USING btree (created_at);
CREATE INDEX idx_notification_message_reference ON notification.notification_messages USING btree (reference_type, reference_id);
CREATE INDEX idx_notification_message_status ON notification.notification_messages USING btree (status);


-- notification.notification_templates definition

-- Drop table

-- DROP TABLE notification.notification_templates;

CREATE TABLE notification.notification_templates (
                                                     active bool DEFAULT false NOT NULL,
                                                     created_at timestamptz(6) NOT NULL,
                                                     updated_at timestamptz(6) NOT NULL,
                                                     id uuid NOT NULL,
                                                     channel varchar(20) NOT NULL,
                                                     code varchar(100) NOT NULL,
                                                     created_by varchar(100) NULL,
                                                     updated_by varchar(100) NULL,
                                                     body_template text NOT NULL,
                                                     subject_template text NULL,
                                                     variables jsonb NULL,
                                                     CONSTRAINT notification_templates_code_key UNIQUE (code),
                                                     CONSTRAINT notification_templates_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_notification_template_code ON notification.notification_templates USING btree (code);

-- DROP SCHEMA "identity";

CREATE SCHEMA "identity" AUTHORIZATION root;

-- DROP SEQUENCE "identity".role_permissions_id_seq;

CREATE SEQUENCE "identity".role_permissions_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE "identity".user_roles_id_seq;

CREATE SEQUENCE "identity".user_roles_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;-- "identity".permissions definition

-- Drop table

-- DROP TABLE "identity".permissions;

CREATE TABLE "identity".permissions (
                                        id uuid NOT NULL,
                                        "name" varchar(100) NOT NULL,
                                        description varchar(255) NULL,
                                        CONSTRAINT permissions_name_key UNIQUE (name),
                                        CONSTRAINT permissions_pkey PRIMARY KEY (id)
);


-- "identity".roles definition

-- Drop table

-- DROP TABLE "identity".roles;

CREATE TABLE "identity".roles (
                                  id uuid NOT NULL,
                                  "name" varchar(255) NOT NULL,
                                  CONSTRAINT roles_name_key UNIQUE (name),
                                  CONSTRAINT roles_pkey PRIMARY KEY (id)
);


-- "identity".users definition

-- Drop table

-- DROP TABLE "identity".users;

CREATE TABLE "identity".users (
                                  status int4 NOT NULL,
                                  created_at timestamptz(6) NOT NULL,
                                  deleted_at timestamptz(6) NULL,
                                  updated_at timestamptz(6) NOT NULL,
                                  id uuid NOT NULL,
                                  created_by varchar(100) NULL,
                                  deleted_by varchar(100) NULL,
                                  email varchar(100) NOT NULL,
                                  updated_by varchar(100) NULL,
                                  username varchar(100) NOT NULL,
                                  "password" varchar(255) NOT NULL,
                                  CONSTRAINT users_email_key UNIQUE (email),
                                  CONSTRAINT users_pkey PRIMARY KEY (id),
                                  CONSTRAINT users_status_check CHECK ((status = ANY (ARRAY[0, 1, 2, 3, 4]))),
                                  CONSTRAINT users_username_key UNIQUE (username)
);


-- "identity".role_permissions definition

-- Drop table

-- DROP TABLE "identity".role_permissions;

CREATE TABLE "identity".role_permissions (
                                             granted_at timestamptz(6) NOT NULL,
                                             id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                             permission_id uuid NOT NULL,
                                             role_id uuid NOT NULL,
                                             granted_by varchar(255) NULL,
                                             CONSTRAINT role_permissions_pkey PRIMARY KEY (id),
                                             CONSTRAINT uq_role_permission UNIQUE (role_id, permission_id),
                                             CONSTRAINT fkegdk29eiy7mdtefy5c7eirr6e FOREIGN KEY (permission_id) REFERENCES "identity".permissions(id),
                                             CONSTRAINT fkn5fotdgk8d1xvo8nav9uv3muc FOREIGN KEY (role_id) REFERENCES "identity".roles(id)
);


-- "identity".user_roles definition

-- Drop table

-- DROP TABLE "identity".user_roles;

CREATE TABLE "identity".user_roles (
                                       assigned_at timestamptz(6) NOT NULL,
                                       id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                       role_id uuid NOT NULL,
                                       user_id uuid NOT NULL,
                                       assigned_by varchar(255) NULL,
                                       CONSTRAINT uq_user_role UNIQUE (user_id, role_id),
                                       CONSTRAINT user_roles_pkey PRIMARY KEY (id),
                                       CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES "identity".roles(id),
                                       CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES "identity".users(id)
);