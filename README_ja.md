# [astah* 簡単コードリバース プラグイン](http://astah.change-vision.com/ja/feature/code-reverse-plugin.html)
GithubやBitbucket、Google Project等ほかのリポジトリサービスにあるJavaコードを直接ドラッグ＆ドロップでastah*へリバースすることができます。

**Demo Movie**

<http://www.youtube.com/watch?v=_7shceFgfVw>


## Available for
astah* professional, astah* UML (6.6.4 or later)


## Ability
* リポジトリ上のURL/リンクをドラッグ ＆ ドロップすることで、astah*のUMLクラスを作成します。
* ローカルにある.javaファイルをドラッグ ＆ ドロップすることで、astah*のUMLクラスを作成します。


## 非サポート事項
* アノテーションの分析
* 関連の作成


## インストール方法
1. [ここから](http://astah.change-vision.com/plugins/easycodereverse/1.0.2.html).jarファイルをダウンロードします
2. メインメニュー[ヘルプ] – [プラグイン一覧]を選択します
3. [インストール] ボタンを押下して.jarファイルを選択します(バージョン6.8以降は、.jarファイルをastah*にドラッグ＆ドロップしてインストールできます)
4. メッセージに従ってastah*を再起動してください
5. リポジトリをカスタマイズしたい場合は、[easycodereverse-dict.json](https://raw.github.com/ChangeVision/astah-easycodereverse-plugin/master/easycodereverse-dict.json)をダウンロードし、`$USER_HOME/.astah/professional/` もしくは、 `$USER_HOME/.astah/uml/`へコピーします


## ビルド方法
1. astah* Plug-in SDK をインストールします。 - <http://astah.net/features/sdk>
1. `git clone git://github.com/ChangeVision/astah-easycodereverse-plugin.git`
1. `cd easycodereverse`
1. `astah-build`
1. `astah-launch`

#### [Eclipse](http://astah.net/tutorials/plug-ins/plugin_tutorial_en/html/helloworld.html#eclipse)用プロジェクトの作成方法

 * `astah-mvn eclipse:eclipse`


## Demo Movie で使用したURL
* Github:

  <https://github.com/KentBeck/junit/blob/master/src/main/java/junit/extensions/ActiveTestSuite.java>

* Google Project Hosting:

  <http://code.google.com/p/google-web-toolkit/source/browse/trunk/dev/core/src/com/google/gwt/dev/GWTShell.java>

* Bitbucket:

  <https://bitbucket.org/jmurty/jets3t/src/844ad30e3c13/src/org/jets3t/service/S3ServiceException.java>


## easycodereverse-dict.json について
**[easycodereverse-dict.json](https://github.com/ChangeVision/astah-easycodereverse-plugin/blob/master/easycodereverse-dict.json)** をカスタマイズすることで、redmineやSourceForgeのソースもリバースできるようになります。


例)

**With revisions**

<https://fisheye2.atlassian.com/browse/mockito/trunk/src/org/mockito/Answers.java?r=1928>

**raw file**

<https://fisheye2.atlassian.com/browse/~raw,r=1928/mockito/trunk/src/org/mockito/Answers.java>

## License
Copyright 2012 Change Vision, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
