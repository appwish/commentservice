-- Type: ItemType

-- DROP TYPE comments."ItemType";

CREATE TYPE comments."itemtypes" AS ENUM
    ('wish');

ALTER TYPE comments."ItemType"
    OWNER TO postgres;


-- Table: comments.comments

-- DROP TABLE comments.comments;

CREATE TABLE comments.comments
(
    "itemType" comments.itemtypes,
    "itemID" integer NOT NULL,
    "parentID" integer,
    "userID" integer,
    id integer NOT NULL DEFAULT nextval('comments.comments_id_seq'::regclass),
    message text COLLATE pg_catalog."default",
    "createdAt" time without time zone NOT NULL DEFAULT now(),
    "updatedAt" time without time zone DEFAULT now(),
    CONSTRAINT comments_id_pkey PRIMARY KEY (id),
    CONSTRAINT comments_fkey_parent_id FOREIGN KEY ("parentID")
        REFERENCES comments.comments (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
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


-- Table: comments.comments_hierarchy

-- DROP TABLE comments.comments_hierarchy;

CREATE TABLE comments.comments_hierarchy
(
    id integer NOT NULL DEFAULT nextval('comments.comments_hierarchy_id_seq'::regclass),
    "parentID" integer NOT NULL,
    "childID" integer NOT NULL,
    depth integer,
    CONSTRAINT comments_hierarchy_pkey_id PRIMARY KEY (id),
    CONSTRAINT comments_hierarchy_fkey_child_id FOREIGN KEY ("childID")
        REFERENCES comments.comments (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT comments_hierarchy_fkey_parent_id FOREIGN KEY ("parentID")
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

-- Index: fki_comments_hierarchy_fkey_child_id

-- DROP INDEX comments.fki_comments_hierarchy_fkey_child_id;

CREATE INDEX fki_comments_hierarchy_fkey_child_id
    ON comments.comments_hierarchy USING btree
    ("childID")
    TABLESPACE pg_default;

-- Index: fki_comments_hierarchy_fkey_parent_id

-- DROP INDEX comments.fki_comments_hierarchy_fkey_parent_id;

CREATE INDEX fki_comments_hierarchy_fkey_parent_id
    ON comments.comments_hierarchy USING btree
    ("parentID")
    TABLESPACE pg_default;