# tile upload managerの仕様

タイルアップロードマネージャーの仕様は次のとおりです。  

# tile upload managerの概要

地理院地図タイルアップローダの参照実装です。  
ダウンロード済みの地理院地図タイルをAmazonDynamoDBを用いてAmazonS3へアップロードします。  
Amazon Web Serviceを用いるため、ご使用時にはIAM認証情報（アクセスキー、シークレットキー）が必要です。　

# tile upload managerの構成

◆tum  
・ソースコード一式  
・Params.xml  
・AwsCredentials.properties  


◆AWS  
・S3  
 Amazon Web Serviceのオブジェクトストレージ。タイルの保存先として使用します。  

・DynamoDB  
 Amazon Web Serviceのキーバリューストア。タイル情報の保存先として使用します。  
 保存する情報は、タイルのフルパス(xyz/{t}/{z}/{x}/{y}.{ext})、MD5SUM、タイルの座標({z}/{x}/{y})、タイルの種類({t})、拡張子（{ext}）です。  
タイルアップロード時にDynamoDBにてタイルのフルパスとMD5SUMによる突き合わせを行い、既に登録済みであればアップロードしない仕組みとなります。  

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

input host、input portにはそれぞれプロキシのhost名、ポート番号を記載してください。　　

## AwsCredentials.properties

AwsCredentials.propertiesは以下のように作成してください。

```
accessKey = xxx
secretKey = yyy
```

xxx、yyyはそれぞれIAM認証情報に置き換えてください。

# tum使用準備
本ページでご紹介する方法は一例です。  
なお本ページでご紹介する方法はWindowsでの使用を想定しています。  


**1.AWSの準備**  
1-0.AWSのアカウントをお持ちでない方は、下記URLよりアカウントを作成してください。  
https://aws.amazon.com/jp/register-flow/  
1-1.Admin権限のIAMユーザーを作成し、アクセスキーとシークレットキーを取得してください。  
1-2.S3のバケットを1つ作成してください。  
1-3.DynamoDBのテーブルを1つ作成してください。  
また、テーブルのパーティションキー、ソートキーはそれぞれ下記のように設定してください。  
・パーティションキー: tileInfo (文字列)  
・ソートキー: md5sum (文字列)  


**2.tumの準備**  
ソースコードにAmazonS3のバケット名やAmazonDynamoDBのテーブル名等を記載する必要があるため、開発環境を用意します。  

2-0.Eclipse準備  
・Eclipseをダウンロード、インストールしてください。言語はJavaを選択してください。下記URL参照  
http://mergedoc.osdn.jp/  
・AWS Toolkit for EclipseをEclipseにインストールしてください。下記URL参照  
https://aws.amazon.com/jp/eclipse/  

2-1.EclipseのAWS Toolkit for AWSのプルダウンメニューより、「New AWS JAVA Project」を選択します。  
2-2.作成されたプロジェクト内の「src」を右クリックし、表示されたコンテキストメニューから「新規」「パッケージ」を選択します。  
2-3.表示された新規パッケージ作成画面にて「名前」の欄にtumと入力し、完了ボタンを押下します。  
2-4.作成されたtumパッケージを右クリックし、表示されたコンテキストメニューから「エクスプローラーで開く」を選択します。  
2-5.表示されたフォルダにソースコード一式を格納し、Eclipseを再起動します。  
2-6.読み込まれたソースコードを確認し、"input your bucket name"やinput your table name"とある箇所を「1.AWSの準備」で作成した名前に変更します。

# タイルアップロードの方法

**1.Eclipseから実行する方法**  
1-1.Eclipseを起動します。「tum使用準備」2-1で作成したプロジェクトを右クリックし、表示されたコンテキストメニューから「エクスプローラーで開く」を選択します。  
1-2.表示されたフォルダにParams.xmlを格納します。binディレクトリ内にAwsCredentials.propertiesを格納します。  
1-3.「tum使用準備」2-1で作成したプロジェクトを右クリックし、表示されたコンテキストメニューから「実行」 「実行の構成」を選択します。  
1-4.表示された実行構成メニューから、「Javaアプリケーション」を右クリックし、「新規」を選択します。  
1-5.表示された新規構成の「メイン」タブで、「参照」ボタンを押下し、「tum使用準備」2-1で作成したプロジェクトを選択します。  
1-6.「引数」タブ内の「プログラムの引数」に下記を参考に[第一引数] [第二引数]を記入します。  

例: C:\Users\Administrator\up\xyz\フォルダにアップロードするタイルが格納されている場合  
tum C:\Users\Administrator\up\xyz

**2.コマンドプロンプトから実行する方法**  
2-1.Eclipseを起動します。「tum使用準備」2-1で作成したプロジェクトを右クリックし、表示されたコンテキストメニューから「エクスポート」を選択します。  
2-2.「実行可能jarファイル」を選択し、任意のエクスポート先を設定します。  
2-3.「ライブラリー処理」は「生成されるJARに必須ライブラリーをパッケージ」を選択します。  
2-4.「完了」ボタンを押下します。  
2-5.2-2で設定したエクスポート先に、Params.xmlとAwsCredentials.propertiesを格納します。  
2-6.コマンドプロンプトでエクスポート先フォルダに移動し、下記コマンドを実行します。  
java -jar tum [第一引数] [第二引数]  
第一引数にはtum、第二引数にはアップロードするタイルが存在するフォルダまでのパスを記入してください。  
例: C:\Users\Administrator\up\xyz\フォルダにアップロードするタイルが格納されている場合  
java -jar tum.jar tum C:\Users\Administrator\up\xyz　　

# 目録及びココタイルアップロードの仕組み
目録及びココタイルの詳しい仕様は下記URLをご確認ください。  
https://github.com/gsi-cyberjapan/mokuroku-spec  
https://github.com/gsi-cyberjapan/cocotile-spec  

**1.目録アップロードの仕組み**  
目録に書き込まれるデータはパス,最終更新時刻,サイズ,MD5SUMです。  
それらのデータをタイル種毎に1タイルずつS3より取得し、1行ずつ書き出す事でmokuroku.csvを作成します。  
mokuroku.csv作成後はgzipにて圧縮し、S3の{t}（タイル種）フォルダの直下にアップロードされます。  

実行コマンド  
java -jar  [第一引数] [第二引数]  
第一引数にはmokuroku、第二引数には一時使用フォルダのパスを記入してください。  
第二引数で指定されたフォルダを使用してmokurokuを作成/アップロードします。  
例: C:\tempフォルダを一時使用フォルダとする場合  
java -jar tum.jar mokuroku C:\temp　　
ヒープメモリの最大値は512m以上推奨  

**2.ココタイルアップロードの仕組み**  
ココタイルに書き込まれるデータはその座標に存在するタイルの種類です。  
タイルの種類毎に目録を参照し、座標毎にココタイルを作成、書き出しを行います。  
ココタイル作成後は、バケット直下のcocotileフォルダに座標毎のフォルダを作成し、そのフォルダにアップロードします。  

実行コマンド  
java -jar  [第一引数] [第二引数]  
第一引数にはcocotile、第二引数には一時使用フォルダのパスを記入してください。  
第二引数で指定されたフォルダを使用してcocotileが作成/アップロードされます。ココタイル作成用一時ファイルも同フォルダに作成されます。    
例: C:\cocotempフォルダを一時使用フォルダとする場合  
java -jar tum.jar mokuroku C:\cocotemp　　
ヒープメモリの最大値は512m以上推奨  

# 動作環境について

下記環境にて動作確認済みです。
なお、全ての環境における動作を保障するものではありません。

- Windows7 32bit
- メモリ 4GB
- ハードディスク（使用量） 40MB
- CD-ROMドライブ　不要
- インターネット環境 - 常時接続できるブロードバンド環境（モバイル端末等は除く）でご利用ください。
- Java バージョン1.8.0  ヒープメモリサイズは512mにて実行

# ご使用時のご注意  
**tum実行時**  
- 第二引数で指定するxyzまでのパスに、「xyz」という文字列を含まないようお願します。  
例）  
正C:\test\123\xyz  
誤C:\test\xyz123\xyz  

- xyzフォルダと同じ位置に「xyz」という文字列を含むフォルダを置かないようお願いします。  
例）  
正C:\test\123フォルダに「xyz」フォルダのみ  
誤C:\test\123フォルダに「xyz」フォルダと「old_xyz」フォルダが存在  

**cocotile実行時**　　
- 第二引数で指定するドライブの空き容量が500GB以上である事をご確認ください。必要容量は増減する可能性があります。  

- 同一環境でcocotileを二重に実行しないようお願いします。  

- cocotile実行前に、第二引数で指定するフォルダ内に「cocotile」フォルダが存在しない事を確認し、存在する場合は削除されますようお願いします。  

- 一時フォルダまでのパスに「cocotile」という文字列を含まないようお願いします。  
例）  
正C:\cocotemp\  
誤C:\cocotile\cocotemp\  
 
- 一時フォルダ内にはプログラムが使用する一時ファイルが作成されます。プログラム終了まで、それらを削除されないようお願いします。  
