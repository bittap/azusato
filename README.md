# azusato(あずさと)

## 作ったきっかけ

### [プロポーズ](https://azusato.com/)
+ 彼女と一緒にインスタグラムを見た際、彼氏が彼女のために、ゲームを作ったことを見て私も自分だけができる何かを作ってあげたい。
+ 知人からのお祝いを一生残していきたい。
+ [JPAの本](https://blogshine.tistory.com/552)を勉強したので実際のコードに活用して行き、知識を身につけたい。
+ 技術を駆使し、プロジェクトで不効率だったところの改善をしたい。
  1. 権限管理はServiceではなく、filter層で管理する。
  2. JPAの集約を使ってなるべく二つのテーブルをJoinする場合でもSQLを書かないようにしたい。
  3. ControllerとServiceをswaggerを使って管理する。

### [結婚式招待状](https://azusato.com/wedding/invitation)   
+ 無料の結婚式招待状アプリを使うのではなく、私自身が作ったプログラムで知人が結婚式まで迷わずくることを運用したい。
+ DDDを使い、ドメインの修正はServiceではなく、Entityで修正するようにしたい。
+ 動的クエリをQueryDSLを使って実装したい。（JPA標準のCreteriaだと実装が理解しにくい不便がある。）

## プロジェクト期間

+ [プロポーズ](https://azusato.com/) 2022/01 ~ 2022/07
  
     ※ 開発完了後に少しずつ改善を行い、デプロイの自動化やバグ改修、カバレッジ確保、Bug Spots等の対応を行った。
+ [結婚式招待状](https://azusato.com/wedding/invitation)   2023/03 ~ 2023/06

## 特徴

1. 日本人・韓国人に対応するために日本語・韓国語をサポートする。
2. メインページは管理者と管理者以外で内容が異なる。（プロポーズした際はそちらを活用）
3. 管理者にログインすると、結婚式参加者の一覧が閲覧できる。
4. お祝いに書き込む場合は、書き込み内容の修正は可能としたいが、ユーザの手間を少なくするため、トークンを使ってログインせずに書き込みができて修正もできる。

## URL
+ [プロポーズ](https://azusato.com/)
+ [結婚式招待状](https://azusato.com/wedding/invitation)   

## 技術
### サーバ
+ EC2(Ubuntu)
### バックエンド
+ Java11
### フロントエンド
+ Javascript
+ Bootstrap
### フレームワーク
+ SpringBoot
### DB関連
+ RDS - Mysql
+ JPA
+ QueryDSL
### テスト
+ Junit
### デプロイの自動化
+ GithubActions
### プロジェクトの構築と管理
+ Maven
+ Git
### https
+ CloudFlare
### etc
+ PWABuilder
+ Swagger-ui

## システム構成図
![あずさと - 構成図.jpg](./readme/azusato_server_img.jpg)
## ソフトウェアアーキテクチャー図
![あずさと - ソフトウェアアーキテクチャー図.jpg](./readme/azusato_software_architecture_img.jpg)



