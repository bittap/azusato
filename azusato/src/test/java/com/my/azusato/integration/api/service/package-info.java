/**
 * Serviceの結合テスト。
 * <p>
 * Service ~ Repository
 * <p>
 * データベースの構成
 * 
 * <pre>
 * データの初期化： 行わない。
 * =>JunitのTransactionalでロールバックされるため常に初期化されていると仮定する。
 * もし、初期化が必要な場合は以下のSQLを実行する。
 *  SET FOREIGN_KEY_CHECKS = 0;
 *  DROP TABLE IF EXISTS `celebration`;
 *  DROP TABLE IF EXISTS `celebration_notice`;
 *  DROP TABLE IF EXISTS `celebration_reply`;
 *  DROP TABLE IF EXISTS `flyway_schema_history`;
 *  DROP TABLE IF EXISTS `profile`;
 *  DROP TABLE IF EXISTS `user`;
 *  SET FOREIGN_KEY_CHECKS = 1;
 * データベースの初期スキーマ：flywayを使う。パスはdb/test{@code -}migrationを使う。
 * データベースの初期データ:db/test{@code -}migration.data.sql
 * </pre>
 * 
 * <p>
 * 共通データの構成
 * 
 * <pre>
 * test - migrationフォルダの中のxxx_data.sqlを参考
 * </pre>
 * 
 * <p>
 * データの確認方法(Service) <notice>DBunitは使わない
 * 
 * <pre>
 * 検索:Noを比較
 * 挿入・更新:挿入・更新される必要があるカラムの戻り値を比較(無駄に全カラムの比較はしない)
 * 削除: findByIdでないか確認
 * </pre>
 * 
 * データの確認方法(Controller) <notice>DBunitは使わない
 * 
 * <pre>
 * 検索:結合テストで行う。レポジトリにて参照したデータを一致するか確認する。
 * 挿入・更新・削除:しない。
 * </pre>
 * 
 * 
 * @author Carmel
 *
 */
package com.my.azusato.integration.api.service;
