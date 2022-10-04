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
 *  各データは1個ずつにする。
 *  テーブル
 *  user: 管理者、非会員ユーザ、カカオ、ライン
 *  profile: userテーブルに紐づく
 *  celebration： userテーブルに紐づくユーザの作成
 *  celebration_reply： celebrationテーブルに紐づく2件ずつ
 *  celebration_notice: なし
 * </pre>
 * 
 * <p>
 * データの確認方法 <notice>DBunitは使わない
 * 
 * <pre>
 * 1件検索：予測データと結果を比較  * 未来 :findById or しない。
 * 2件以上の検索: 1件の予測データと結果を比較。もし、中で分岐される場合には分岐の分もケースに追加  * 未来 :findById
 * 挿入: 予測データとfindByIdで挿入したデータ比較
 * 更新: 予測データとfindByIdで更新したデータ比較
 * 削除: 予測データとfindByIdで更新したデータ比較 * 未来 :削除したデータが参照できないこと
 * </pre>
 * 
 * 
 * @author Carmel
 *
 */
package com.my.azusato.integration.api.service;
