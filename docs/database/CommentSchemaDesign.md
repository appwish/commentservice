# Comments Schema Design

### Requirements:

- We should be able to store 'threaded comments' - users should be able to reply to a comment
- Design and propose a database schema that will do this efficiently (a tree structure?)
-https://github.com/appwish/commentservice/issues/6

## Proposal:

To make the comments service more generic and supported for all the services type that will be available 
on the platform. Planning for the following options

Let's take an example. When an wish id is provided, the below sample output is expected from our comment service

    wishID [1]
    |------- Comment 3
                |----- Comment 6
                        |----- Comment 9
                |----- Comment 7
                        |----- Comment 8
    |------- Comment 4
    
In the above example, for the wish id [1] there are 2 top level comments (3, 4) and each
comments has replays and they have some replays.

using the following blow approach, lets try to achieve this

### 1. Service Types:

Currently our platform supports multiple services. We will be maintaining 
an metadata table to identify each services by their own id. This helps us
to put service type code in the comments table, that it can support
for multiple types.

- id - Unique identifier for Service Type.
- serviceType - Name give to service. E.x: wish, feed, comment
- createdAt - Timestamp creation
- updatedAt - Timestamp latest update

Example:

id | serviceType | createdAt | updatedAt
--- | ------ | ---- | ----
1 | wish | "2020-01-18" | "2020-01-18"
1 | feed | "2020-01-18" | "2020-01-18"

### 2. Comments Tables:

Based on the requirement, the comments has to be stored in a way that they can fetched 
to represent them in a tree structure. To handle this, plan is to use Closure Table Pattern.

Closure table is a simple way of storing and querying hierarchical data in RDBMS databases.
Here the parent child relation can be maintained in a separate table with the depth to indicate
level. The following will be structure of comments table.

`IMPORTANT: Since every comment hirerarchy should be associated with a service type 
entry. Let make blank entry and parentID as NULL. And userID will be defaulted to 0`

- id - Unique identifier for comment. [PKey]
- userID - Id of the user posted the comment
- message - comment that has been posted
- parentID - Hold the parent of the comments, indicator of reply. [FKey]
[ If this value is NULL, that is the starting of the message. ]
- serviceTypeID - Id of the Service Type
- typeID - Actual ID of the Service entry in their corresponding DB.
- createdAt - Timestamp of comment creation
- updatedAt - Timestamp of comment latest update

id  | userID | message | parentID | serviceTypeID | typeID | createdAt | updatedAt
--- | ------ | ---- | ---- | ---- | ---- | ---- | ----
1  | 0 | blank | NULL | 1 | 1 | "2020-01-18" |	"2020-01-18"
3  | 1 | "My First comment" | 1 | 1 | 1 | "2020-01-18" |	"2020-01-18" 
4  | 1 | "My Second comment" | 1 | 1 | 1 | "2020-01-18" | "2020-01-18"
6  | 2 | "My First comment - Reply" | 3	| 1 | 1 | "2020-01-18" | "2020-01-18"
7  | 3 | "My First comment - Reply - user3" | 3 | 1 | 1 | "2020-01-18" | "2020-01-18"
8  | 2 | "MFC - Reply - user3 - Reply - u2" | 7 | 1 | 1 | "2020-01-18" | "2020-01-18"
9  | 4 | "MFC - Reply - u4" | 6 | 1 | 1 | "2020-01-18" | "2020-01-18"

With this design, every top level comments can be identified by their parentID which will 
be `NULL`. If comments has a parentID column associated with a integer value, 
then they are considered as replay. This can got to any deep.

### 3. Comments Hierarchy Mapping.

As the comments table has been explained in the top. It will be hard to generate the
tree structure.

For simpling that, the following mapping table will be created.

- id - Unique identifier for mapping. [Pkey]
- parentID - comment id [FKey]
- childID - comment id of the reply [FKey]
- depth - Hierarchy level from parent to child

id  | parentID | childID | depth 
--- | ------ | ---- | ---- 
| 1 | 1 | 1 | 0
| 13 | 3 | 3 | 0
| 14 | 4 | 4 | 0
| 15 | 6 | 6 | 0
| 16 | 7 | 7 | 0
| 17 | 8 | 8 | 0
| 18 | 3 | 6 | 1
| 19 | 3 | 7 | 1
| 20 | 7 | 8 | 1
| 21 | 3 | 8 | 2
| 22 | 9 | 9 | 0
| 23 | 6 | 9 | 1
| 24 | 3 | 9 | 2

In the table by default every entry in comments will be added and it will have its 
parent as itself. And when any entry is added as replay, the complete hierarchy will be added 
into the table, with corresponding depth.

In the above example if we take the parentID 3. you can see all the mapping has be inserted.

    3 -> 3 where depth 0
    3 -> 6 where depth 1
    3 -> 7 where depth 1
    3 -> 8 where depth 2
    3 -> 9 where depth 2

To handle the changes automatic. Triggers will be used, hence when there an entry / delete 
in the comments table the hierarchy table get updated.


Getting the tree for a particular comment.
----
    
    SELECT c."userID", c.message, c."parentID" as pd, m."childID", m.depth
    FROM comments.comments_hierarchy m JOIN comments.comments c
    ON (m."childID" = c.id)
    WHERE m."parentID" = 1 and c."typeID"=1 ORDER BY pd ASC, m."childID" ASC;

`NOTE: Working on the ranking order from database end. Currently the tree can 
be derived at the API layer `



