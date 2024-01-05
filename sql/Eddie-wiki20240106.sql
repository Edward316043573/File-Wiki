/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.245.129
 Source Server Type    : MySQL
 Source Server Version : 50740
 Source Host           : 192.168.245.129:3306
 Source Schema         : Eddie-wiki

 Target Server Type    : MySQL
 Target Server Version : 50740
 File Encoding         : 65001

 Date: 06/01/2024 01:24:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1030 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2022-11-09 18:11:08', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2022-11-09 18:11:08', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2022-11-09 18:11:08', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2022-11-09 18:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, 'WIKI权限', 0, 3, '', '', '', 1, 0, 'C', '0', '0', 'test2', '#', 'test', NULL, 'test', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '编辑wiki文档', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_EDIT_PAGE_', '#', '', NULL, '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '查看wiki文档', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_VIEW_PAGE_', '#', '', NULL, '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '评论wiki文档', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_COMMENT_PAGE_', '#', '', NULL, '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '删除wiki文档', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_DELETE_PAGE_', '#', '', NULL, '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '上传wiki文档附件', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_PAGE_FILE_UPLOAD_', '#', '', NULL, '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '删除wiki文档附件', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_PAGE_FILE_DELETE_', '#', '', NULL, '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, 'wiki权限管理', 1022, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'WIKI_PAGE_AUTH_MANAGE_', '#', '', NULL, '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2022-11-09 18:11:08', 'test', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2022-11-09 18:11:08', '', NULL, '普通角色');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1003);
INSERT INTO `sys_role_menu` VALUES (1, 1004);
INSERT INTO `sys_role_menu` VALUES (1, 1023);
INSERT INTO `sys_role_menu` VALUES (1, 1026);
INSERT INTO `sys_role_menu` VALUES (2, 1027);
INSERT INTO `sys_role_menu` VALUES (2, 1028);
INSERT INTO `sys_role_menu` VALUES (5, 100);
INSERT INTO `sys_role_menu` VALUES (5, 101);
INSERT INTO `sys_role_menu` VALUES (5, 1000);
INSERT INTO `sys_role_menu` VALUES (5, 1001);
INSERT INTO `sys_role_menu` VALUES (5, 1002);
INSERT INTO `sys_role_menu` VALUES (5, 1003);
INSERT INTO `sys_role_menu` VALUES (5, 1004);
INSERT INTO `sys_role_menu` VALUES (5, 1005);
INSERT INTO `sys_role_menu` VALUES (5, 1006);
INSERT INTO `sys_role_menu` VALUES (5, 1007);
INSERT INTO `sys_role_menu` VALUES (5, 1008);
INSERT INTO `sys_role_menu` VALUES (5, 1009);
INSERT INTO `sys_role_menu` VALUES (5, 1010);
INSERT INTO `sys_role_menu` VALUES (5, 1011);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, 'test', 'test', '00', 'test@163.com', '13811112223', '0', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', '$2a$10$v4fU.pv2Aw7R/pt92TtESutDChGCCFOYhZug5FwbRiLd65yiCW7iK', '0', '0', '', NULL, '', NULL, '', NULL, NULL);
INSERT INTO `sys_user` VALUES (4, 2, '张三', '小三', '00', 'zhangsan@example.com', '13811112222', '男', 'https://example.com/avatar/1.jpg', '$2a$10$v4fU.pv2Aw7R/pt92TtESutDChGCCFOYhZug5FwbRiLd65yiCW7iK', '0', '0', '', NULL, '$2a$10$npv5JSeFR6/wLz8BBMmSBOMb8byg2eyfK4/vvoBk3RKtTLBhIhcpy', NULL, '', NULL, '这是一个测试用户');
INSERT INTO `sys_user` VALUES (16, NULL, 'testasdfaf', 'dafdsaf ', '00', '', '', '0', '', '$2a$10$EGNovuROmQPMpfigjB/8pulxD8fJm6zm/6jNB7gSqexx772q2Sbue', '1', '0', '', NULL, 'test', NULL, '', NULL, NULL);
INSERT INTO `sys_user` VALUES (18, 2, 'test111adfsf', 'test11112', '00', '', '', '0', 'https://example.com/avatar/1.jpg', '1234', '0', '0', '', NULL, 'test', NULL, 'test', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (16, 1);
INSERT INTO `sys_user_role` VALUES (16, 2);
INSERT INTO `sys_user_role` VALUES (18, 2);

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `sys_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '系统类型 1=manage 2=wiki 3=db',
  `msg_type` int(11) NOT NULL DEFAULT 1 COMMENT '消息类型 1=普通文本消息 2=wiki文档创建 3=wiki文档删除 4=wiki文档编辑 5=wiki文档权限修改 6=wiki文档评论 7=wiki文档删除评论 8=wiki文档上传附件',
  `data_id` bigint(20) NULL DEFAULT NULL COMMENT '消息关联的数据ID',
  `data_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息关联的数据说明',
  `msg_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `msg_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '消息状态 0=未读 1=已读 2=已删除',
  `operator_user_id` bigint(20) NULL DEFAULT NULL COMMENT '操作人用户ID',
  `operator_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人用户名',
  `affect_user_id` bigint(20) NULL DEFAULT NULL COMMENT '影响人用户ID',
  `affect_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '影响人用户名',
  `accept_user_id` bigint(20) NULL DEFAULT NULL COMMENT '接收人用户ID',
  `creation_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 241 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------

-- ----------------------------
-- Table structure for user_setting
-- ----------------------------
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设置的名字',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设置的值',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记 0=正常 1=已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_setting
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_auth_role_space
-- ----------------------------
DROP TABLE IF EXISTS `wiki_auth_role_space`;
CREATE TABLE `wiki_auth_role_space`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色Id',
  `space_id` bigint(20) NOT NULL COMMENT '空间Id',
  PRIMARY KEY (`role_id`, `space_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_auth_role_space
-- ----------------------------
INSERT INTO `wiki_auth_role_space` VALUES (1, 1);
INSERT INTO `wiki_auth_role_space` VALUES (2, 1);

-- ----------------------------
-- Table structure for wiki_page
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page`;
CREATE TABLE `wiki_page`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `space_id` bigint(20) NULL DEFAULT NULL COMMENT '空间ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名字',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父ID',
  `node_type` tinyint(4) NULL DEFAULT 0 COMMENT '节点类型 0=有子节点 1=终节点',
  `zan_num` int(11) NOT NULL DEFAULT 0 COMMENT '赞的数量',
  `edit_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '编辑类型 0=可编辑 1=不允许编辑',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人ID',
  `update_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改人名字',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=有效 1=删除',
  `view_num` int(11) NOT NULL DEFAULT 0 COMMENT '阅读数',
  `seq_no` int(11) NOT NULL DEFAULT 0 COMMENT '顺序',
  `editor_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '编辑框类型 1=HTML 2=Markdown 3=PDF 4=DOCX 5= XLSX 6=PPTX',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_page_comment
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page_comment`;
CREATE TABLE `wiki_page_comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父评论ID',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评论内容',
  `zan_num` int(11) NOT NULL DEFAULT 0 COMMENT '赞的数量',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(4) NULL DEFAULT 0 COMMENT '0=有效 1=删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page_comment
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_page_content
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page_content`;
CREATE TABLE `wiki_page_content`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人ID',
  `update_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改人名字',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `preview` varchar(16000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预览内容',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_page_id`(`page_id`) USING BTREE COMMENT '页面ID'
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page_content
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_page_file
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page_file`;
CREATE TABLE `wiki_page_file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件URL',
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件UUID',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人ID',
  `update_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改人名字',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(4) NULL DEFAULT 0 COMMENT '0=有效 1=删除',
  `download_num` int(11) NOT NULL DEFAULT 0 COMMENT '下载次数',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `file_source` tinyint(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_uuid`(`uuid`) USING BTREE COMMENT '文件ID'
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page_file
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_page_history
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page_history`;
CREATE TABLE `wiki_page_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记 0=正常 1=已删除',
  `content` mediumblob NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_page_id`(`page_id`) USING BTREE COMMENT '页面ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 174 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page_history
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_page_template
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page_template`;
CREATE TABLE `wiki_page_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板信息主键',
  `space_id` bigint(20) NULL DEFAULT NULL COMMENT '空间id',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '模板挂载文档id',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签信息',
  `share_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '模板公开状态（0-个人模板1-公共模板）',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `modify_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人ID',
  `modify_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人姓名',
  `yn` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效 1=有效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page_template
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_page_zan
-- ----------------------------
DROP TABLE IF EXISTS `wiki_page_zan`;
CREATE TABLE `wiki_page_zan`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `page_id` bigint(20) NULL DEFAULT NULL COMMENT '页面ID',
  `comment_id` bigint(20) NULL DEFAULT NULL COMMENT '评论ID',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `yn` tinyint(4) NULL DEFAULT NULL COMMENT '是否有效 0=无效 1=有效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_page_zan
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_space
-- ----------------------------
DROP TABLE IF EXISTS `wiki_space`;
CREATE TABLE `wiki_space`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '空间名',
  `type` tinyint(4) NULL DEFAULT 1 COMMENT '空间类型 1=公司 2=个人 3=私人',
  `space_explain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `edit_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '编辑类型 0=可编辑 1=不允许编辑',
  `tree_lazy_load` tinyint(4) NOT NULL DEFAULT 0 COMMENT '目录延迟加载 0=否 1=是',
  `open_doc` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否是开放文档 0=否 1=是',
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '唯一UUID',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人名字',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记 0=正常 1=已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_space
-- ----------------------------

-- ----------------------------
-- Table structure for wiki_space_favorite
-- ----------------------------
DROP TABLE IF EXISTS `wiki_space_favorite`;
CREATE TABLE `wiki_space_favorite`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `space_id` bigint(20) NULL DEFAULT NULL COMMENT '空间ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记 0=正常 1=已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户空间收藏记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wiki_space_favorite
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
