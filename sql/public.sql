/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 170002 (170002)
 Source Host           : localhost:5432
 Source Catalog        : drive
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 170002 (170002)
 File Encoding         : 65001

 Date: 25/01/2025 22:32:11
*/


-- ----------------------------
-- Sequence structure for db_account_id_seq1
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_account_id_seq1";
CREATE SEQUENCE "public"."db_account_id_seq1" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_account_id_seq1" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_checkin_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_checkin_id_seq";
CREATE SEQUENCE "public"."db_user_checkin_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_checkin_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_detail_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_detail_id_seq";
CREATE SEQUENCE "public"."db_user_detail_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_detail_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_download_task_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_download_task_id_seq";
CREATE SEQUENCE "public"."db_user_download_task_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_download_task_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_file_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_file_id_seq";
CREATE SEQUENCE "public"."db_user_file_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_file_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_file_ops_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_file_ops_id_seq";
CREATE SEQUENCE "public"."db_user_file_ops_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_file_ops_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_folder_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_folder_id_seq";
CREATE SEQUENCE "public"."db_user_folder_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_folder_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_recycle_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_recycle_id_seq";
CREATE SEQUENCE "public"."db_user_recycle_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_recycle_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_redeem_code_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_redeem_code_id_seq";
CREATE SEQUENCE "public"."db_user_redeem_code_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_redeem_code_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_setting_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_setting_id_seq";
CREATE SEQUENCE "public"."db_user_setting_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_setting_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_share_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_share_id_seq";
CREATE SEQUENCE "public"."db_user_share_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_share_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for db_user_solution_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."db_user_solution_id_seq";
CREATE SEQUENCE "public"."db_user_solution_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."db_user_solution_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for db_account
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_account";
CREATE TABLE "public"."db_account" (
  "id" int4 NOT NULL DEFAULT nextval('db_account_id_seq1'::regclass),
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "phone" varchar(255) COLLATE "pg_catalog"."default",
  "email" varchar(255) COLLATE "pg_catalog"."default",
  "avatar" varchar(255) COLLATE "pg_catalog"."default",
  "role" varchar[] COLLATE "pg_catalog"."default",
  "status" varchar(255) COLLATE "pg_catalog"."default",
  "checkin" int4,
  "details" int4,
  "settings" int4,
  "register_time" date,
  "is_deleted" bool
)
;
ALTER TABLE "public"."db_account" OWNER TO "postgres";

-- ----------------------------
-- Records of db_account
-- ----------------------------
BEGIN;
INSERT INTO "public"."db_account" ("id", "username", "password", "phone", "email", "avatar", "role", "status", "checkin", "details", "settings", "register_time", "is_deleted") VALUES (3, 'yuzoi', '$2a$10$G0.bSytRjSRpzaYtGikdfe1F5/Blz.Pgv.ENr4vT5P/c.yHLpVsLC', NULL, '123@abc.com', NULL, '{user}', NULL, 3, 3, 1, '2024-09-20', 'f');
INSERT INTO "public"."db_account" ("id", "username", "password", "phone", "email", "avatar", "role", "status", "checkin", "details", "settings", "register_time", "is_deleted") VALUES (2, 'test', '$2a$10$wQIm85991R99JEYRIN80JOcsZnTCOYZJfp5rMVtflQMGit1vgXV0y', NULL, 'abc@123.com', NULL, '{user}', NULL, 2, 2, NULL, '2024-09-20', 'f');
INSERT INTO "public"."db_account" ("id", "username", "password", "phone", "email", "avatar", "role", "status", "checkin", "details", "settings", "register_time", "is_deleted") VALUES (1, 'admin', '$2a$10$R8dXZ0p27/DvprCNoMxqiudn20dG89/Du21/cN/BylTtb4ZE877Ki', NULL, '1763611895@qq.com', '/image/avatar/2025-01-06/19D2FD3D7F8D4E6CB9BD518315AD8E7F', '{user}', NULL, 4, 1, 2, '2024-07-25', 'f');
COMMIT;

-- ----------------------------
-- Table structure for db_user_checkin
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_checkin";
CREATE TABLE "public"."db_user_checkin" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_checkin_id_seq'::regclass),
  "account_id" int4,
  "checkin_count" int4 DEFAULT 0,
  "last_checkin" timestamptz(0),
  "checkin_reward" int8 DEFAULT 0,
  "checkin_consecutive" int4 DEFAULT 0
)
;
ALTER TABLE "public"."db_user_checkin" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_checkin"."checkin_count" IS '签到总计次数';
COMMENT ON COLUMN "public"."db_user_checkin"."last_checkin" IS '最后一次签到';
COMMENT ON COLUMN "public"."db_user_checkin"."checkin_reward" IS '签到奖励';
COMMENT ON COLUMN "public"."db_user_checkin"."checkin_consecutive" IS '当天为止连续签到天数';

-- ----------------------------
-- Records of db_user_checkin
-- ----------------------------
BEGIN;
INSERT INTO "public"."db_user_checkin" ("id", "account_id", "checkin_count", "last_checkin", "checkin_reward", "checkin_consecutive") VALUES (4, 1, 5, '2025-01-09 14:44:32+00', 249561088, 1);
COMMIT;

-- ----------------------------
-- Table structure for db_user_coupons
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_coupons";
CREATE TABLE "public"."db_user_coupons" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_redeem_code_id_seq'::regclass),
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "code" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2,
  "create_time" timestamptz(6),
  "expire_time" timestamptz(6)
)
;
ALTER TABLE "public"."db_user_coupons" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_coupons"."status" IS '0-未激活 1-未使用 2-已使用 3-已过期 -1-不可用';

-- ----------------------------
-- Records of db_user_coupons
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for db_user_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_detail";
CREATE TABLE "public"."db_user_detail" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_detail_id_seq'::regclass),
  "account_id" int4,
  "total_storage" int8,
  "used_storage" int8
)
;
ALTER TABLE "public"."db_user_detail" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_detail"."total_storage" IS '总存储空间';
COMMENT ON COLUMN "public"."db_user_detail"."used_storage" IS '已用存储空间';

-- ----------------------------
-- Records of db_user_detail
-- ----------------------------
BEGIN;
INSERT INTO "public"."db_user_detail" ("id", "account_id", "total_storage", "used_storage") VALUES (2, 2, NULL, NULL);
INSERT INTO "public"."db_user_detail" ("id", "account_id", "total_storage", "used_storage") VALUES (3, 3, NULL, NULL);
INSERT INTO "public"."db_user_detail" ("id", "account_id", "total_storage", "used_storage") VALUES (1, 1, 1298137088, 990852170);
COMMIT;

-- ----------------------------
-- Table structure for db_user_download_task
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_download_task";
CREATE TABLE "public"."db_user_download_task" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_download_task_id_seq'::regclass),
  "account_id" int4 NOT NULL,
  "url" text COLLATE "pg_catalog"."default",
  "task_type" varchar(255) COLLATE "pg_catalog"."default",
  "file_name" varchar(255) COLLATE "pg_catalog"."default",
  "status" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" timestamptz(6),
  "started_at" timestamptz(6),
  "completed_at" timestamptz(6),
  "progress" int2
)
;
ALTER TABLE "public"."db_user_download_task" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_download_task"."status" IS '任务状态：''pending'', ''downloading'', ''completed'', ''failed'', ''paused''';

-- ----------------------------
-- Records of db_user_download_task
-- ----------------------------
BEGIN;
INSERT INTO "public"."db_user_download_task" ("id", "account_id", "url", "task_type", "file_name", "status", "created_at", "started_at", "completed_at", "progress") VALUES (3, 1, 'http://localhost:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL3pvaS1kcml2ZS1zeXN0ZW0vbWFpbi8xL2lkZWFJVS0yMDI0LjIuZXhlP1gtQW16LUFsZ29yaXRobT1BV1M0LUhNQUMtU0hBMjU2JlgtQW16LUNyZWRlbnRpYWw9VklDM0Y1MkVRUFBKUzJSWU44RjglMkYyMDI0MTAxMCUyRnVzLWVhc3QtMSUyRnMzJTJGYXdzNF9yZXF1ZXN0JlgtQW16LURhdGU9MjAyNDEwMTBUMTA0NTM2WiZYLUFtei1FeHBpcmVzPTQzMjAwJlgtQW16LVNlY3VyaXR5LVRva2VuPWV5SmhiR2NpT2lKSVV6VXhNaUlzSW5SNWNDSTZJa3BYVkNKOS5leUpoWTJObGMzTkxaWGtpT2lKV1NVTXpSalV5UlZGUVVFcFRNbEpaVGpoR09DSXNJbVY0Y0NJNk1UY3lPRFl3TURNeU9Td2ljR0Z5Wlc1MElqb2liV2x1YVc5aFpHMXBiaUo5LmhzSkJFbXNMOUVWVzF0Wi05QTZEZkpXYmhIWnJlOW13bnpQbVc4aF9BckoyWTJUNFNGLU1uMWFGMVUyVnhoWWVEZGJMRzlwVDE0OThHMW5rUUowWWFBJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZ2ZXJzaW9uSWQ9bnVsbCZYLUFtei1TaWduYXR1cmU9MjNjOTI1NGU0YmNlNThhZmEzNjYzNmZlMWY0MThhMmI2YzBjMjIxZGI4NThiODY2MmE1MjJmMDNmNDYzMjQ5OQ', 'http', NULL, 'downloading', '2024-10-10 10:55:01.001+00', '2024-10-10 10:55:01.069+00', NULL, 0);
INSERT INTO "public"."db_user_download_task" ("id", "account_id", "url", "task_type", "file_name", "status", "created_at", "started_at", "completed_at", "progress") VALUES (4, 1, 'http://localhost:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL3pvaS1kcml2ZS1zeXN0ZW0vbWFpbi8xL2lkZWFJVS0yMDI0LjIuZXhlP1gtQW16LUFsZ29yaXRobT1BV1M0LUhNQUMtU0hBMjU2JlgtQW16LUNyZWRlbnRpYWw9VklDM0Y1MkVRUFBKUzJSWU44RjglMkYyMDI0MTAxMCUyRnVzLWVhc3QtMSUyRnMzJTJGYXdzNF9yZXF1ZXN0JlgtQW16LURhdGU9MjAyNDEwMTBUMTA0NTM2WiZYLUFtei1FeHBpcmVzPTQzMjAwJlgtQW16LVNlY3VyaXR5LVRva2VuPWV5SmhiR2NpT2lKSVV6VXhNaUlzSW5SNWNDSTZJa3BYVkNKOS5leUpoWTJObGMzTkxaWGtpT2lKV1NVTXpSalV5UlZGUVVFcFRNbEpaVGpoR09DSXNJbVY0Y0NJNk1UY3lPRFl3TURNeU9Td2ljR0Z5Wlc1MElqb2liV2x1YVc5aFpHMXBiaUo5LmhzSkJFbXNMOUVWVzF0Wi05QTZEZkpXYmhIWnJlOW13bnpQbVc4aF9BckoyWTJUNFNGLU1uMWFGMVUyVnhoWWVEZGJMRzlwVDE0OThHMW5rUUowWWFBJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZ2ZXJzaW9uSWQ9bnVsbCZYLUFtei1TaWduYXR1cmU9MjNjOTI1NGU0YmNlNThhZmEzNjYzNmZlMWY0MThhMmI2YzBjMjIxZGI4NThiODY2MmE1MjJmMDNmNDYzMjQ5OQ', 'http', NULL, 'downloading', '2024-10-10 10:58:44.749+00', '2024-10-10 10:58:44.817+00', NULL, 0);
INSERT INTO "public"."db_user_download_task" ("id", "account_id", "url", "task_type", "file_name", "status", "created_at", "started_at", "completed_at", "progress") VALUES (5, 1, 'http://localhost:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL3pvaS1kcml2ZS1zeXN0ZW0vbWFpbi8xL2lkZWFJVS0yMDI0LjIuZXhlP1gtQW16LUFsZ29yaXRobT1BV1M0LUhNQUMtU0hBMjU2JlgtQW16LUNyZWRlbnRpYWw9VklDM0Y1MkVRUFBKUzJSWU44RjglMkYyMDI0MTAxMCUyRnVzLWVhc3QtMSUyRnMzJTJGYXdzNF9yZXF1ZXN0JlgtQW16LURhdGU9MjAyNDEwMTBUMTA0NTM2WiZYLUFtei1FeHBpcmVzPTQzMjAwJlgtQW16LVNlY3VyaXR5LVRva2VuPWV5SmhiR2NpT2lKSVV6VXhNaUlzSW5SNWNDSTZJa3BYVkNKOS5leUpoWTJObGMzTkxaWGtpT2lKV1NVTXpSalV5UlZGUVVFcFRNbEpaVGpoR09DSXNJbVY0Y0NJNk1UY3lPRFl3TURNeU9Td2ljR0Z5Wlc1MElqb2liV2x1YVc5aFpHMXBiaUo5LmhzSkJFbXNMOUVWVzF0Wi05QTZEZkpXYmhIWnJlOW13bnpQbVc4aF9BckoyWTJUNFNGLU1uMWFGMVUyVnhoWWVEZGJMRzlwVDE0OThHMW5rUUowWWFBJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZ2ZXJzaW9uSWQ9bnVsbCZYLUFtei1TaWduYXR1cmU9MjNjOTI1NGU0YmNlNThhZmEzNjYzNmZlMWY0MThhMmI2YzBjMjIxZGI4NThiODY2MmE1MjJmMDNmNDYzMjQ5OQ', 'http', NULL, 'downloading', '2024-10-10 10:58:44.749+00', '2024-10-10 10:59:35.455+00', NULL, 0);
INSERT INTO "public"."db_user_download_task" ("id", "account_id", "url", "task_type", "file_name", "status", "created_at", "started_at", "completed_at", "progress") VALUES (6, 1, 'https://placehold.co/600x400', 'http', NULL, 'downloading', '2024-10-10 14:23:47.489+00', '2024-10-10 14:23:47.565+00', NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for db_user_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_file";
CREATE TABLE "public"."db_user_file" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_file_id_seq'::regclass),
  "account_id" int4 NOT NULL,
  "folder_id" int4,
  "filename" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "type" varchar(255) COLLATE "pg_catalog"."default",
  "size" int8,
  "hash" varchar(255) COLLATE "pg_catalog"."default",
  "storage_url" varchar(1024) COLLATE "pg_catalog"."default",
  "is_deleted" bool DEFAULT false,
  "upload_at" timestamptz(6),
  "viewed_at" timestamptz(6),
  "status" int2
)
;
ALTER TABLE "public"."db_user_file" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_file"."status" IS '0-回收站中 1-正常 -1 已永久删除';

-- ----------------------------
-- Records of db_user_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for db_user_file_ops
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_file_ops";
CREATE TABLE "public"."db_user_file_ops" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_file_ops_id_seq'::regclass),
  "user_id" int4 NOT NULL,
  "file_id" int4,
  "action" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamptz(6),
  "uuid" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."db_user_file_ops" OWNER TO "postgres";

-- ----------------------------
-- Records of db_user_file_ops
-- ----------------------------
BEGIN;
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (42, 1, 139, '上传文件', '2024-12-28 09:30:47.632+00', 'B2788E78-8944-4708-9D3B-637570E38528');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (43, 1, 139, '删除文件', '2025-01-05 08:11:33.352+00', '59338E64-77D8-472B-80CA-4EC7ECC198C8');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (44, 1, 140, '上传文件', '2025-01-05 08:11:51.81+00', '59338E64-77D8-472B-80CA-4EC7ECC198C8');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (45, 1, 141, '上传文件', '2025-01-05 09:40:52.268+00', 'B0070585-8D6B-496A-83D9-3533A9EECC63');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (46, 1, 142, '上传文件', '2025-01-05 09:45:08.137+00', 'B0070585-8D6B-496A-83D9-3533A9EECC63');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (47, 1, 143, '上传文件', '2025-01-05 09:53:51.941+00', '72DF3B9F-13BE-4CD1-AF9F-8070F9076D59');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (48, 1, 146, '上传文件', '2025-01-05 10:07:08.604+00', '358E3044-78F6-49C5-905E-EFF23F311146');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (49, 1, 147, '上传文件', '2025-01-05 13:12:19.172+00', 'EB08197C-07EF-4681-8FF1-AAAFE8A252D3');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (50, 1, 148, '上传文件', '2025-01-05 14:20:33.636+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (51, 1, 140, '移动文件', '2025-01-05 14:47:08.133+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (52, 1, 141, '移动文件', '2025-01-05 14:47:08.152+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (53, 1, 142, '下载文件', '2025-01-05 15:22:09.824+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (54, 1, 146, '下载文件', '2025-01-05 15:22:09.847+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (55, 1, 145, '移动文件', '2025-01-05 15:22:46.17+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (56, 1, 143, '移动文件', '2025-01-05 15:22:46.194+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (57, 1, 146, '移动文件', '2025-01-05 15:24:20.723+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (58, 1, 142, '移动文件', '2025-01-05 15:24:20.745+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (59, 1, 147, '移动文件', '2025-01-05 15:24:30.337+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (60, 1, 142, '删除文件', '2025-01-05 15:39:55.834+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (61, 1, 146, '删除文件', '2025-01-05 15:39:55.856+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (62, 1, 147, '删除文件', '2025-01-05 15:39:55.877+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (63, 1, 148, '移动文件', '2025-01-05 15:47:28.575+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (64, 1, 140, '移动文件', '2025-01-05 15:47:28.603+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (65, 1, 141, '移动文件', '2025-01-05 15:47:28.638+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (66, 1, 145, '移动文件', '2025-01-05 15:47:28.672+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (67, 1, 143, '移动文件', '2025-01-05 15:47:28.708+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (68, 1, 146, '移动文件', '2025-01-05 15:47:28.732+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (69, 1, 142, '移动文件', '2025-01-05 15:47:28.756+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (70, 1, 147, '移动文件', '2025-01-05 15:47:28.781+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (71, 1, 144, '移动文件', '2025-01-05 15:47:55.323+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (72, 1, 148, '移动文件', '2025-01-05 15:47:55.345+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (73, 1, 8, '删除文件', '2025-01-05 15:47:58.794+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (74, 1, 8, '删除文件', '2025-01-05 15:57:29.561+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (75, 1, 8, '删除文件', '2025-01-05 16:01:17.854+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (76, 1, 8, '删除文件', '2025-01-05 16:01:52.929+00', '658335DA-FCCE-42EF-99F6-E68344E26ECE');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (77, 1, 8, '删除文件夹', '2025-01-05 16:03:24.728+00', '3418B34A-2BBB-4A70-BDB5-E380F56ED10B');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (78, 1, 8, '删除文件夹', '2025-01-05 16:04:03.317+00', '3418B34A-2BBB-4A70-BDB5-E380F56ED10B');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (79, 1, 8, '删除文件夹', '2025-01-05 16:06:48.399+00', '3418B34A-2BBB-4A70-BDB5-E380F56ED10B');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (80, 1, 8, '删除文件夹', '2025-01-05 16:13:19.875+00', 'B105E39A-1740-46FB-BA1E-8F561D12DF0C');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (81, 1, 145, '删除文件', '2025-01-07 14:25:36.059+00', 'F2118C0D-CA55-434A-9022-C384312F51BC');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (82, 1, 141, '删除文件', '2025-01-07 14:35:40.696+00', 'F2118C0D-CA55-434A-9022-C384312F51BC');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (83, 1, 146, '移动文件', '2025-01-07 14:51:16.477+00', '6A6C9D81-AB83-4229-8059-6B0ED4ABD4FF');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (84, 1, 142, '移动文件', '2025-01-07 14:51:16.496+00', '6A6C9D81-AB83-4229-8059-6B0ED4ABD4FF');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (85, 1, 139, '移动文件', '2025-01-07 14:51:16.526+00', '6A6C9D81-AB83-4229-8059-6B0ED4ABD4FF');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (86, 1, 9, '删除文件夹', '2025-01-07 14:51:20.418+00', '6A6C9D81-AB83-4229-8059-6B0ED4ABD4FF');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (87, 1, 146, '移动文件', '2025-01-07 14:52:26.052+00', 'EF2A1591-7C4E-4F5F-BE62-11E73BDE4E56');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (88, 1, 142, '移动文件', '2025-01-07 14:52:26.069+00', 'EF2A1591-7C4E-4F5F-BE62-11E73BDE4E56');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (89, 1, 140, '移动文件', '2025-01-07 14:52:26.103+00', 'EF2A1591-7C4E-4F5F-BE62-11E73BDE4E56');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (90, 1, 10, '删除文件夹', '2025-01-07 14:52:29.348+00', 'EF2A1591-7C4E-4F5F-BE62-11E73BDE4E56');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (91, 1, 139, '移动文件', '2025-01-07 15:01:06.362+00', '5078A7DE-2992-493F-B382-700CAA6A96A8');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (92, 1, 146, '移动文件', '2025-01-07 15:01:06.383+00', '5078A7DE-2992-493F-B382-700CAA6A96A8');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (93, 1, 142, '移动文件', '2025-01-07 15:01:06.4+00', '5078A7DE-2992-493F-B382-700CAA6A96A8');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (94, 1, 11, '删除文件夹', '2025-01-07 15:01:11.6+00', '5078A7DE-2992-493F-B382-700CAA6A96A8');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (95, 1, 145, '移动文件', '2025-01-07 15:08:59.625+00', '480CF85E-DA49-4F1B-A835-24606A33A183');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (96, 1, 141, '移动文件', '2025-01-07 15:09:01.11+00', '480CF85E-DA49-4F1B-A835-24606A33A183');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (97, 1, 12, '删除文件夹', '2025-01-07 15:09:05.576+00', '480CF85E-DA49-4F1B-A835-24606A33A183');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (98, 1, 144, '删除文件', '2025-01-09 14:11:53.933+00', '37351509-5583-4F12-87AE-A94B84E77A23');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (99, 1, 150, '上传文件', '2025-01-09 15:04:53.857+00', 'B844494B-A722-4F5A-B3C5-26C1ADA58C23');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (100, 1, 150, '删除文件', '2025-01-09 15:14:59.118+00', 'D5A42E02-06FB-4EB6-819C-2CAAB5C0749D');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (101, 1, 150, '移动文件', '2025-01-09 15:33:18.235+00', '225AD708-4351-4BBB-BE23-E422D56D7E75');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (102, 1, 148, '移动文件', '2025-01-09 15:33:18.26+00', '225AD708-4351-4BBB-BE23-E422D56D7E75');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (103, 1, 147, '移动文件', '2025-01-09 15:33:18.28+00', '225AD708-4351-4BBB-BE23-E422D56D7E75');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (104, 1, 13, '删除文件夹', '2025-01-09 15:33:22.278+00', '225AD708-4351-4BBB-BE23-E422D56D7E75');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (105, 1, 13, '删除文件夹', '2025-01-09 15:55:42.711+00', '856A8BE2-252F-4DD2-81F4-2E8BB014723D');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (106, 1, 13, '删除文件夹', '2025-01-09 15:55:57.526+00', '856A8BE2-252F-4DD2-81F4-2E8BB014723D');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (107, 1, 145, '删除文件', '2025-01-12 15:59:30.272+00', '2961E4FE-C5D7-4289-BB02-E944468B2C7A');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (108, 1, 141, '删除文件', '2025-01-12 16:00:07.029+00', '2961E4FE-C5D7-4289-BB02-E944468B2C7A');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (109, 1, 139, '删除文件', '2025-01-12 16:01:28.5+00', '2961E4FE-C5D7-4289-BB02-E944468B2C7A');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (110, 1, 140, '删除文件', '2025-01-12 16:03:01.048+00', '2961E4FE-C5D7-4289-BB02-E944468B2C7A');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (111, 1, 151, '上传文件', '2025-01-12 16:04:49.158+00', '2961E4FE-C5D7-4289-BB02-E944468B2C7A');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (112, 1, 151, '删除文件', '2025-01-12 16:04:58.302+00', '2961E4FE-C5D7-4289-BB02-E944468B2C7A');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (113, 1, 152, '上传文件', '2025-01-12 16:08:38.999+00', '2342AE8C-4451-42F1-859F-660A9B9715D0');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (114, 1, 152, '删除文件', '2025-01-12 16:08:50.776+00', '2342AE8C-4451-42F1-859F-660A9B9715D0');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (115, 1, 153, '上传文件', '2025-01-12 16:09:20.352+00', '2342AE8C-4451-42F1-859F-660A9B9715D0');
INSERT INTO "public"."db_user_file_ops" ("id", "user_id", "file_id", "action", "created_at", "uuid") VALUES (116, 1, 153, '删除文件', '2025-01-12 16:09:29.389+00', '2342AE8C-4451-42F1-859F-660A9B9715D0');
COMMIT;

-- ----------------------------
-- Table structure for db_user_folder
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_folder";
CREATE TABLE "public"."db_user_folder" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_folder_id_seq'::regclass),
  "account_id" int4 NOT NULL,
  "parent_id" int4,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamptz(6),
  "is_deleted" bool,
  "status" int2
)
;
ALTER TABLE "public"."db_user_folder" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_folder"."status" IS '0-回收站 1-正常';

-- ----------------------------
-- Records of db_user_folder
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for db_user_recycle
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_recycle";
CREATE TABLE "public"."db_user_recycle" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_recycle_id_seq'::regclass),
  "tid" int4,
  "uid" int4,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "type" varchar(255) COLLATE "pg_catalog"."default",
  "expired_at" timestamptz(6),
  "create_at" timestamptz(6)
)
;
ALTER TABLE "public"."db_user_recycle" OWNER TO "postgres";

-- ----------------------------
-- Records of db_user_recycle
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for db_user_redeem_code
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_redeem_code";
CREATE TABLE "public"."db_user_redeem_code" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_redeem_code_id_seq'::regclass),
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "code" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2,
  "create_time" timestamptz(6),
  "expire_time" timestamptz(6)
)
;
ALTER TABLE "public"."db_user_redeem_code" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_redeem_code"."status" IS '0-未激活 1-未使用 2-已使用 3-已过期 -1-不可用';

-- ----------------------------
-- Records of db_user_redeem_code
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for db_user_setting
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_setting";
CREATE TABLE "public"."db_user_setting" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_setting_id_seq'::regclass),
  "account_id" int4 NOT NULL,
  "two_factor_status" bool,
  "two_factor_code" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."db_user_setting" OWNER TO "postgres";

-- ----------------------------
-- Records of db_user_setting
-- ----------------------------
BEGIN;
INSERT INTO "public"."db_user_setting" ("id", "account_id", "two_factor_status", "two_factor_code") VALUES (1, 3, 'f', 'GHPWLWFFMJFRUG4YWM5GKOPQKSXJ6I7G');
INSERT INTO "public"."db_user_setting" ("id", "account_id", "two_factor_status", "two_factor_code") VALUES (2, 1, 'f', 'XDOGWIHOINVHXGWZ2H5PSWVJZUMKVS7E');
COMMIT;

-- ----------------------------
-- Table structure for db_user_share
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_share";
CREATE TABLE "public"."db_user_share" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_share_id_seq'::regclass),
  "file_id" int4 NOT NULL,
  "shared_by" int4 NOT NULL,
  "shared_with" int4,
  "link" varchar(255) COLLATE "pg_catalog"."default",
  "expired_at" timestamptz(6),
  "created_at" timestamptz(6)
)
;
ALTER TABLE "public"."db_user_share" OWNER TO "postgres";

-- ----------------------------
-- Records of db_user_share
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for db_user_solution
-- ----------------------------
DROP TABLE IF EXISTS "public"."db_user_solution";
CREATE TABLE "public"."db_user_solution" (
  "id" int4 NOT NULL DEFAULT nextval('db_user_solution_id_seq'::regclass),
  "type" varchar(255) COLLATE "pg_catalog"."default",
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "tag" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "price" numeric(10,2),
  "duration" int4,
  "extra" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."db_user_solution" OWNER TO "postgres";
COMMENT ON COLUMN "public"."db_user_solution"."duration" IS '/days';

-- ----------------------------
-- Records of db_user_solution
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_account_id_seq1"
OWNED BY "public"."db_account"."id";
SELECT setval('"public"."db_account_id_seq1"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_checkin_id_seq"
OWNED BY "public"."db_user_checkin"."id";
SELECT setval('"public"."db_user_checkin_id_seq"', 4, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_detail_id_seq"
OWNED BY "public"."db_user_detail"."id";
SELECT setval('"public"."db_user_detail_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_download_task_id_seq"
OWNED BY "public"."db_user_download_task"."id";
SELECT setval('"public"."db_user_download_task_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_file_id_seq"
OWNED BY "public"."db_user_file"."id";
SELECT setval('"public"."db_user_file_id_seq"', 153, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_file_ops_id_seq"
OWNED BY "public"."db_user_file_ops"."id";
SELECT setval('"public"."db_user_file_ops_id_seq"', 116, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_folder_id_seq"
OWNED BY "public"."db_user_folder"."id";
SELECT setval('"public"."db_user_folder_id_seq"', 13, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_recycle_id_seq"
OWNED BY "public"."db_user_recycle"."id";
SELECT setval('"public"."db_user_recycle_id_seq"', 13, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_redeem_code_id_seq"
OWNED BY "public"."db_user_redeem_code"."id";
SELECT setval('"public"."db_user_redeem_code_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_setting_id_seq"
OWNED BY "public"."db_user_setting"."id";
SELECT setval('"public"."db_user_setting_id_seq"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_share_id_seq"
OWNED BY "public"."db_user_share"."id";
SELECT setval('"public"."db_user_share_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."db_user_solution_id_seq"
OWNED BY "public"."db_user_solution"."id";
SELECT setval('"public"."db_user_solution_id_seq"', 1, false);

-- ----------------------------
-- Primary Key structure for table db_account
-- ----------------------------
ALTER TABLE "public"."db_account" ADD CONSTRAINT "db_account_pkey1" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_checkin
-- ----------------------------
ALTER TABLE "public"."db_user_checkin" ADD CONSTRAINT "db_user_checkin_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_coupons
-- ----------------------------
ALTER TABLE "public"."db_user_coupons" ADD CONSTRAINT "db_user_coupons_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_download_task
-- ----------------------------
ALTER TABLE "public"."db_user_download_task" ADD CONSTRAINT "db_user_download_task_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_file
-- ----------------------------
ALTER TABLE "public"."db_user_file" ADD CONSTRAINT "db_user_file_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_file_ops
-- ----------------------------
ALTER TABLE "public"."db_user_file_ops" ADD CONSTRAINT "db_user_file_ops_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_folder
-- ----------------------------
ALTER TABLE "public"."db_user_folder" ADD CONSTRAINT "db_user_folder_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_recycle
-- ----------------------------
ALTER TABLE "public"."db_user_recycle" ADD CONSTRAINT "db_user_recycle_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_redeem_code
-- ----------------------------
ALTER TABLE "public"."db_user_redeem_code" ADD CONSTRAINT "db_user_redeem_code_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_setting
-- ----------------------------
ALTER TABLE "public"."db_user_setting" ADD CONSTRAINT "db_user_setting_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_share
-- ----------------------------
ALTER TABLE "public"."db_user_share" ADD CONSTRAINT "db_user_share_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table db_user_solution
-- ----------------------------
ALTER TABLE "public"."db_user_solution" ADD CONSTRAINT "db_user_solution_pkey" PRIMARY KEY ("id");
