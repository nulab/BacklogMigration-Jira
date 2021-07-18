# Backlog Migration for JIRA

![](https://github.com/nulab/BacklogMigration-Jira/workflows/Build/badge.svg)
![](https://github.com/nulab/BacklogMigration-Jira/workflows/Test/badge.svg)

Migrate your projects from JIRA to [Backlog].
(英語の下に日本文が記載されています)

**Backlog Migration for JIRA is in beta. To avoid problems, create a new project and import data before importing data to the existing project in Backlog.**

- Backlog
  - [https://backlog.com](https://backlog.com/)

## DEMO

![Demo](https://www.backlog.jp/backlog-migration/backlog-jira-migration.gif)

## Requirements

- **Java 8**
- The Backlog Space's **administrator** roles.

https://github.com/nulab/BacklogMigration-Jira/releases

## Download

Please download the jar file from this link, and run from the command line as follows.

https://github.com/nulab/BacklogMigration-Jira/releases

    java -jar backlog-migration-jira-[latest version].jar

To use via proxy server, run from the command line as follows.

    java -Djdk.http.auth.tunneling.disabledSchemes= -Dhttps.proxyHost=[proxy host name] -Dhttps.proxyPort=[proxy port] -Dhttps.proxyUser=[proxy user] -Dhttps.proxyPassword=[proxy password] -jar backlog-migration-jira-[latest version].jar

## How to use

### Preparation

Create a directory.

    $ mkdir work
    $ cd work

### Export command

Run the [**export**] command to export issues and mapping files.
(The mapping file is used to link data between Backlog and JIRA.)

    java -jar backlog-migration-jira-[latest version].jar \
      export \
      --jira.username [JIRA user name] \
      --jira.apiKey   [JIRA of API key] \
      --jira.url      [URL of JIRA] \
      --backlog.key   [Backlog of API key] \
      --backlog.url   [URL of Backlog] \
      --projectKey    [JIRA project identifier]:[Backlog project key]

Sample commands:

    java -jar backlog-migration-jira-[latest version].jar \
      export \
      --jira.username XXXXXXXXXXXXX \
      --jira.apiKey   XXXXXXXXXXXXX \
      --jira.url https://nulab.atlassian.net \
      --backlog.key XXXXXXXXXXXXX \
      --backlog.url https://nulab.backlog.jp \
      --projectKey jira_project:BACKLOG_PROJECT

Issues and the mapping files are created as follows.

    .
    ├── backlog
    │   ├── project
    │   │   └── ...
    │   └── project.json
    ├── log
    │   ├── backlog-migration-jira-warn.log
    │   └── backlog-migration-jira.log
    └── mapping
        ├── priorities.csv
        ├── priorities_list.csv
        ├── statuses.csv
        ├── statuses_list.csv
        ├── users.csv
        └── users_list.csv

- mapping/users.csv (users)
- mapping/priorities.csv (priority)
- mapping/statuses.csv (state)

#### About mapping projects

Specify the destination project for **--projectKey** option by colon (:). i.e. [**--projectKey jira_project:BACKLOG_PROJECT**] migrates **jira_project** jira project in **BACKLOG_PROJECT** backlog project.

    --projectKey [JIRA project identifier]:[Backlog project key]

### Fix the mapping file

A file in CSV format will be automatically created.
The right side is Backlog item.
For the assignable items from **Name** column, please refer to the following file by reference

- mapping/users_list.csv (users)
- mapping/priorities_list.csv (priority)
- mapping/statuses_list.csv (state)

### Import command

Run the [**import**] command to import data.

    java -jar backlog-migration-jira-[latest version].jar \
      import \
      --jira.username [JIRA user name] \
      --jira.apiKey   [JIRA of API key] \
      --jira.url      [URL of JIRA] \
      --backlog.key   [Backlog of API key] \
      --backlog.url   [URL of Backlog] \
      --projectKey    [JIRA project identifier]:[Backlog project key]

Sample commands:

    java -jar backlog-migration-jira-[latest version].jar \
      import \
      --jira.username XXXXXXXXXXXXX \
      --jira.apiKey   XXXXXXXXXXXXX \
      --jira.url https://your-space-name.atlassian.net \
      --backlog.key XXXXXXXXXXXXX \
      --backlog.url https://your-space-name.backlog.jp \
      --projectKey jira_project:BACKLOG_PROJECT

#### Control retry count

[**--retryCount**] Setting this property allows you to specify the number of retries when a network error occurs.

Sample commands:

    java -jar backlog-migration-jira-[latest version].jar \
      import \
      --jira.username XXXXXXXXXXXXX \
      --jira.apiKey   XXXXXXXXXXXXX \
      --jira.url https://your-space-name.atlassian.net \
      --backlog.key XXXXXXXXXXXXX \
      --backlog.url https://your-space-name.backlog.jp \
      --projectKey jira_project:BACKLOG_PROJECT
      --retryCount [Number of retries]

## Limitation

### Supported JIRA version

#### Cloud version

We support.

#### Server version(Limited)

We support the following versions.

- 6.3.4

Can not migrate the change logs.  
Can not migrate the status. It always be **Open**.

### Backlog's user roles

This program is for the users with the Space's **administrator** roles.

### Migration project with custom fields

Only applied to **max** or **platina** plan.

### About Project

- Text formatting rules: **markdown**
- Some changes will be applied to the JIRA's project identifier to meet the project key format in Backlog.

**Hyphen** → **underscore**

Single-byte **lowercase** character → Single-byte **uppercase** character

### About custom fields

- Versions and users will be registered as lists and will be the fixed values.
- User will not be converted.
- Boolean values will be registered in radio button format of "Yes" or "No".
- The date and time are converted to dates and registered.

### About change logs

- Worklog is not supported. Scheduled for next release.

### Others

- If the space of Backlog is a free plan, it cannot be migrated due to API rate limiting. 
- This tool cannot be used in parallel as it can exceed the API rate limit when run in parallel.

## Re-importing

When the project key in Backlog and JIRA matches, they will be considered as the same project and data will be imported as follows.

**If the person migrating data is not in the project.**

The project will not be imported and the following message will be shown. Join the project to migrate data.
Importing to this project failed. You are not a member of this project. Join the project to add issues.

| Item          | Specifications                                                                                                                           |
| :------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Project       | The project will not be added when there is a project with same project key. The issues and wikis will be added to the existing project. |
| Issues        | Issues with matching subject, creator, creation date are not registered.                                                                 |
| Custom fields | The custom field will not be added when there is a custom field with same name.                                                          |

## License

MIT License

- http://www.opensource.org/licenses/mit-license.php

## Inquiry

Please contact us if you encounter any problems during the JIRA to Backlog migration.

https://backlog.com/contact/

# Backlog Migration for JIRA

JIRA のプロジェクトを[Backlog]に移行するためのツールです。

**Backlog Migration for JIRA はベータバージョンです。Backlog 上の既存プロジェクトにインポートする場合は、先に新しく別プロジェクトを作成し、こちらにインポートし内容を確認後、正式なプロジェクトにインポートしてください**

- Backlog
  - [https://www.backlog.jp](https://www.backlog.jp/)

## 必須要件

- **Java 8**
- Backlog の **管理者権限**

https://github.com/nulab/BacklogMigration-Jira/releases

## ダウンロード

こちらのリンクから jar ファイルをダウンロードし、以下のようにコマンドラインから実行します。

https://github.com/nulab/BacklogMigration-Jira/releases

    java -jar backlog-migration-jira-[最新バージョン].jar

プロキシ経由で使用する場合は、以下のように実行します。

    java -Djdk.http.auth.tunneling.disabledSchemes= -Dhttps.proxyHost=[プロキシサーバのホスト名] -Dhttps.proxyPort=[プロキシサーバのポート番号] -Dhttps.proxyUser=[プロキシユーザー名] -Dhttps.proxyPassword=[プロキシパスワード] -jar backlog-migration-jira-[最新バージョン].jar

## 使い方

### 前準備

作業用のディレクトリを作成します。

    $ mkdir work
    $ cd work

### エクスポートコマンド

[**export**]コマンドを実行し、課題等のエクスポートとマッピングファイルを準備する必要があります。
(マッピングファイルは JIRA と Backlog のデータを対応付けるために使用します。)

    java -jar backlog-migration-jira-[最新バージョン].jar \
      export \
      --jira.username [JIRAのユーザー名] \
      --jira.apiKey   [JIRAのAPIキー] \
      --jira.url      [JIRAのURL] \
      --backlog.key   [BacklogのAPIキー] \
      --backlog.url   [BacklogのURL] \
      --projectKey    [JIRAプロジェクト識別子]:[Backlogプロジェクトキー]

サンプルコマンド：

    java -jar backlog-migration-jira-[最新バージョン].jar \
      export \
      --jira.username XXXXXXXXXXXXX \
      --jira.apiKey   XXXXXXXXXXXXX \
      --jira.url https://nulab.atlassian.net \
      --backlog.key XXXXXXXXXXXXX \
      --backlog.url https://nulab.backlog.jp \
      --projectKey jira_project:BACKLOG_PROJECT

以下のように課題等とマッピングファイルが作成されます。

    .
    ├── backlog
    │   ├── project
    │   │   └── ...
    │   └── project.json
    ├── log
    │   ├── backlog-migration-jira-warn.log
    │   └── backlog-migration-jira.log
    └── mapping
        ├── priorities.csv
        ├── priorities_list.csv
        ├── statuses.csv
        ├── statuses_list.csv
        ├── users.csv
        └── users_list.csv

- mapping/users.csv(ユーザー)
- mapping/priorities.csv(優先度)
- mapping/statuses.csv(状態)

#### プロジェクトのマッピングについて

**--projectKey** オプションに以下のようにコロン **[:]** 区切りでプロジェクトを指定することで、Backlog 側の移行先のプロジェクトを指定することができます。

    --projectKey [JIRAのプロジェクト識別子]:[Backlogのプロジェクトキー]

### マッピングファイルを修正

自動作成されるファイルは以下のように CSV 形式で出力されます。
Backlog 側の空白の項目は自動設定できなかった項目になります。
以下のファイルから **Name** 列の項目をコピーして、空白を埋める必要が有ります。

- mapping/users_list.csv(ユーザー)
- mapping/priorities_list.csv(優先度)
- mapping/statuses_list.csv(状態)

### インポートコマンド

[**import**]コマンドを実行することでインポートを実行します。

    java -jar backlog-migration-jira-[最新バージョン].jar \
      import \
      --jira.username [JIRAのユーザー名] \
      --jira.apiKey   [JIRAのAPIキー] \
      --jira.url      [JIRAのURL] \
      --backlog.key   [BacklogのAPIキー] \
      --backlog.url   [BacklogのURL] \
      --projectKey    [JIRAプロジェクト識別子]:[Backlogプロジェクトキー]

サンプルコマンド：

    java -jar backlog-migration-jira-[最新バージョン].jar \
      import \
      --jira.username XXXXXXXXXXXXX \
      --jira.apiKey   XXXXXXXXXXXXX \
      --jira.url https://your-space-name.atlassian.net \
      --backlog.key XXXXXXXXXXXXX \
      --backlog.url https://your-space-name.backlog.jp \
      --projectKey jira_project:BACKLOG_PROJECT

#### 再試行する回数の指定

[**--retryCount**]オプションを利用すると、ネットワークエラー等が発生したときに再試行する回数を指定できます。

サンプルコマンド：

    java -jar backlog-migration-jira-[最新バージョン].jar \
      import \
      --jira.username XXXXXXXXXXXXX \
      --jira.apiKey   XXXXXXXXXXXXX \
      --jira.url https://your-space-name.atlassian.net \
      --backlog.key XXXXXXXXXXXXX \
      --backlog.url https://your-space-name.backlog.jp \
      --projectKey jira_project:BACKLOG_PROJECT \
      --retryCount [再試行する回数]

## 制限事項

### JIRA の対応バージョン

#### クラウド版

対応しております。

#### Server 版(一部機能のみ)

以下のバージョンで確認しております。

- 6.3.4

変更履歴は移行できません。  
状態は移行できません。状態は常に **未対応** となります。

### 実行できるユーザー

Backlog の **管理者権限** が必要になります。

### カスタムフィールドを使用しているプロジェクトの移行

Backlog で **マックスプラン以上** のプランを契約している必要があります。

### プロジェクトについて

- テキスト整形のルール： **markdown**
- JIRA のプロジェクト識別子は以下のように変換され Backlog のプロジェクトキーとして登録されます。

**ハイフン** → **アンダースコア**

**半角英子文字** → **半角英大文字**

### カスタムフィールドについて

- バージョンとユーザーはリストとして登録され固定値になります。
- ユーザーは変換されません。
- 真偽値は[はい]、[いいえ]のラジオボタン形式で登録されます。
- 日時は日付に変換され登録されます。

### Change log について

- Worklog には対応していません。(次回リリースで対応予定)

### その他

- Backlog のスペースがフリープランの場合はAPIのレート制限により移行できません。
- 本移行ツールの並列実行は、APIのレート制限を超える可能性があるため動作を保証できません。

## 再インポートの仕様

Backlog 側に JIRA に対応するプロジェクトキーがある場合同一プロジェクトとみなし、以下の仕様でインポートされます。

※ 対象のプロジェクトに 参加していない場合

対象プロジェクトはインポートされず以下のメッセージが表示されます。対象プロジェクトをインポートする場合は、対象プロジェクトに参加してください。[⭕️⭕️ を移行しようとしましたが ⭕️⭕️ に参加していません。移行したい場合は ⭕️⭕️ に参加してください。]

| 項目         | 仕様                                                                                                               |
| :----------- | ------------------------------------------------------------------------------------------------------------------ |
| プロジェクト | 同じプロジェクトキーのプロジェクトがある場合、プロジェクトを作成せず対象のプロジェクトに課題や Wiki を登録します。 |
| 課題         | 件名、作成者、作成日が一致する課題は登録されません。                                                               |
| カスタム属性 | 同じ名前のカスタム属性がある場合登録しません。                                                                     |

## ライセンス

MIT License

- http://www.opensource.org/licenses/mit-license.php

## お問い合わせ

お問い合わせは下記サイトからご連絡ください。

https://backlog.com/ja/contact/

[backlog]: https://backlog.com/ja/
