-- Service Type
insert into comments.appwish_service_type (id, "serviceType") values (1, 'wish');

-- Comments sample
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (1, 0, 'blank', NULL, 1, 1);
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (2, 1, 'comment 2', 1, 1, 1);
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (3, 1, 'comment 3', 1, 1, 1);
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (4, 2, 'comment 4', 2, 1, 1);
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (5, 3, 'comment 5', 2, 1, 1);
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (6, 2, 'comment 6', 4, 1, 1);
insert into comments.comments (id, "userID", message, "parentID", "serviceTypeID", "typeID") values (7, 4, 'comment 7', 5, 1, 1);

-- Comments Hierarchy
-- Making all the comments as its parent
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (1, 1, 1, 0);
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (2, 2, 2, 0);
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (3, 3, 3, 0);
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (4, 4, 4, 0);
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (5, 5, 5, 0);
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (6, 6, 6, 0);
insert into comments.comments_hierarchy (id, "parentID", "childID", depth) values (7, 7, 7, 0);

-- relationship between the comments

-- comment 6 relationship
INSERT INTO comments.comments_hierarchy("parentID", "childID", depth)
SELECT p."parentID", c."childID", p.depth+c.depth+1
FROM comments.comments_hierarchy p, comments.comments_hierarchy c
WHERE p."childID" = 1 AND c."parentID" = 2;

-- comment 6 relationship
INSERT INTO comments.comments_hierarchy("parentID", "childID", depth)
SELECT p."parentID", c."childID", p.depth+c.depth+1
FROM comments.comments_hierarchy p, comments.comments_hierarchy c
WHERE p."childID" = 1 AND c."parentID" = 3;

-- comment 7 relationship
INSERT INTO comments.comments_hierarchy("parentID", "childID", depth)
SELECT p."parentID", c."childID", p.depth+c.depth+1
FROM comments.comments_hierarchy p, comments.comments_hierarchy c
WHERE p."childID" = 2 AND c."parentID" = 4;

-- comment 8 relationship
INSERT INTO comments.comments_hierarchy("parentID", "childID", depth)
SELECT p."parentID", c."childID", p.depth+c.depth+1
FROM comments.comments_hierarchy p, comments.comments_hierarchy c
WHERE p."childID" = 2 AND c."parentID" = 5;

-- comment 9 relationship
INSERT INTO comments.comments_hierarchy("parentID", "childID", depth)
SELECT p."parentID", c."childID", p.depth+c.depth+1
FROM comments.comments_hierarchy p, comments.comments_hierarchy c
WHERE p."childID" = 4 AND c."parentID" = 6;

-- comment 9 relationship
INSERT INTO comments.comments_hierarchy("parentID", "childID", depth)
SELECT p."parentID", c."childID", p.depth+c.depth+1
FROM comments.comments_hierarchy p, comments.comments_hierarchy c
WHERE p."childID" = 5 AND c."parentID" = 7;

-- retrieving the particular comment id [1]
SELECT c."userID", c.message, c."parentID" as pd, m."childID", m.depth
FROM comments.comments_hierarchy m JOIN comments.comments c
ON (m."childID" = c.id)
WHERE m."parentID" = 1 and c."typeID"=1 ORDER BY pd ASC, m."childID" ASC;
