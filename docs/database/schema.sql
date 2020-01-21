-- Table: comments.appwish_service_type

-- DROP TABLE comments.appwish_service_type;

CREATE TABLE comments.appwish_service_type
(
    id integer NOT NULL DEFAULT nextval('comments.appwish_services_id_seq'::regclass),
    "serviceType" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT appwish_services_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE comments.appwish_service_type
    OWNER to postgres;



-- Table: comments.comments

-- DROP TABLE comments.comments;

CREATE TABLE comments.comments
(
    id integer NOT NULL DEFAULT nextval('comments.comments_id_seq'::regclass),
    "userID" integer NOT NULL,
    message text COLLATE pg_catalog."default" NOT NULL,
    "parentID" integer,
    "createdAt" date NOT NULL DEFAULT now(),
    "updatedAt" date NOT NULL DEFAULT now(),
    "serviceTypeID" integer,
    "typeID" integer,
    CONSTRAINT comments_id_pkey PRIMARY KEY (id),
    CONSTRAINT comments_fkey_parent_id FOREIGN KEY ("parentID")
        REFERENCES comments.comments (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT comments_fkey_service_type_id FOREIGN KEY ("serviceTypeID")
        REFERENCES comments.appwish_service_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE comments.comments
    OWNER to postgres;

-- Index: fki_comments_fkey_parent_id

-- DROP INDEX comments.fki_comments_fkey_parent_id;

CREATE INDEX fki_comments_fkey_parent_id
    ON comments.comments USING btree
    ("parentID")
    TABLESPACE pg_default;

-- Index: fki_comments_fkey_service_type_id

-- DROP INDEX comments.fki_comments_fkey_service_type_id;

CREATE INDEX fki_comments_fkey_service_type_id
    ON comments.comments USING btree
    ("serviceTypeID")
    TABLESPACE pg_default;


-- Table: comments.comments_hierarchy

-- DROP TABLE comments.comments_hierarchy;

CREATE TABLE comments.comments_hierarchy
(
    id integer NOT NULL DEFAULT nextval('comments.comments_closure_mapping_id_seq'::regclass),
    "parentID" integer NOT NULL,
    "childID" integer NOT NULL,
    depth integer NOT NULL,
    CONSTRAINT comments_closure_mapping_pkey_id PRIMARY KEY (id),
    CONSTRAINT comments_closure_mapping_fkey_child_id FOREIGN KEY ("childID")
        REFERENCES comments.comments (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT comments_closure_mapping_fkey_parent_id FOREIGN KEY ("parentID")
        REFERENCES comments.comments (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE comments.comments_hierarchy
    OWNER to postgres;

-- Index: fki_comments_closure_mapping_fkey_child_id

-- DROP INDEX comments.fki_comments_closure_mapping_fkey_child_id;

CREATE INDEX fki_comments_closure_mapping_fkey_child_id
    ON comments.comments_hierarchy USING btree
    ("childID")
    TABLESPACE pg_default;

-- Index: fki_comments_closure_mapping_fkey_parent_id

-- DROP INDEX comments.fki_comments_closure_mapping_fkey_parent_id;

CREATE INDEX fki_comments_closure_mapping_fkey_parent_id
    ON comments.comments_hierarchy USING btree
    ("parentID")
    TABLESPACE pg_default;