# [WIP] tile upload managerの仕様

タイルアップロードマネージャ」の仕様は次のとおりです。

# 必要ファイル

1. ソースコード一式
2. Params.xml
3. AwsCredentials.properties

## Params.xml

Params.xmlは以下のように作成してください。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Params>
  <Proxy>
    <Host>input host</Host>
    <Port>input port</Port>
  </Proxy>
</Params>
```

input host、input portにはそれぞれプロキシのhost名、ポート番号を記載してきださい。　　

## AwsCredentials.properties

AwsCredentials.propertiesは以下のように作成してください。

```
accessKey = xxx
secretKey = yyy
```

xxx、yyyはそれぞれaccessKey、secretKeyに置き換えてください。

# 動作環境について

下記環境にて動作確認済みです。
なお、全ての環境における動作を保障するものではありません。

- Windows7 32bit
- メモリ 4GB
- ハードディスク（使用量） 40MB
- CD-ROMドライブ　不要
- インターネット環境
  - 常時接続できるブロードバンド環境（モバイル端末等は除く）でご利用ください。
