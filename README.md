Twij
====
Twitter API Proxy in Java

功能特色和已知问题
----

- 完全支持 Twitter Rest API v1.1
- 支持 POST statuses/update_with_media 即官方图床的图片上传
- 支持 multipart/form-data 格式的 POST 请求（从根本上解决 Tweetbot 不能关注和收藏的问题。）
- 不支持 Streaming API （GAE 不支持长连接）
- 目前暂时只支持单个用户

已确认可用的客户端
----

- Twitter for iOS
- Twitter for Mac
- Tweetbot for iOS
- Tweetbot for Mac

API 修改工具
----
 - Twitter for iOS 和 Tweetbot for iOS
   <p>[Qusic.me/Tweetbot-API](http://Qusic.me/Tweetbot-API)</p>
 - Twitter for Mac 和 Tweetbot for Mac
   <p>[Qusic.me/Twitter-API](http://Qusic.me/Twitter-API)</p>

使用方法
----

设置以下的环境变量后部署即可。

- ConsumerKey
- ConsumerSecret
- AccessToken
- AccessTokenSecret

--------

部署到 Google App Engine 的详细教程
----

### 〇、开始之前

这部分在网络上已经有很多教程了，所以我就不在这些地方浪费时间了。

1. 你应该已经注册了一个 Twitter 应用程序。
   <p>点击[这里](https://dev.twitter.com/apps)可以查看你已注册的 Twitter 应用程序。</p>
2. 你应该已经注册了一个 Google App Engine 应用程序。
   <p>点击[这里](https://appengine.google.com/)可以查看你已注册的 Google App Engine 应用程序。</p>
3. 你应该已经安装了 Java 运行时环境。
   <p>打开终端或命令提示符，输入 java -version 后回车，如果能看到和下面类似的 Java 版本号的输出则表示 Java 运行时环境已经安装正确。</p>
   <p>java version "1.7.0_07"<br />Java(TM) SE Runtime Environment (build 1.7.0_07-b10)<br />Java HotSpot(TM) 64-Bit Server VM (build 23.3-b01, mixed mode)</p>

### 一、获取信息

每个信息的获取方法前面就是这个信息的名字，后文中需要这个信息的时候，我都会使用这个名字来引用对应的信息。

1. AppID
   <p>打开[这里](https://appengine.google.com/)后找到你要用来部署 API 代理的应用程序的对应的那一行，然后它的 Application 那一栏的信息就是 AppID。</p>
2. ConsumerKey
   <p>打开[这里](https://dev.twitter.com/apps)后点击你要使用的 API 应用程序，在 Details 栏的 OAuth settings 部分，Consumer key 右边的那一串字符就是 ConsumerKey。</p>
3. ConsumerSecret
   <p>打开[这里](https://dev.twitter.com/apps)后点击你要使用的 API 应用程序，在 Details 栏的 OAuth settings 部分，Consumer secret 右边的那一串字符就是 ConsumerSecret。</p>
4. AccessToken
   <p>打开[这里](https://dev.twitter.com/apps)后点击你要使用的 API 应用程序，在 Details 栏的 Your access token 部分，Access token 右边的那一串字符就是 AccessToken。</p>
5. AccessTokenSecret
   <p>打开[这里](https://dev.twitter.com/apps)后点击你要使用的 API 应用程序，在 Details 栏的 Your access token 部分，Access token secret 右边的那一串字符就是 AccessTokenSecret。</p>

### 二、下载程序

下面会得到一些稍后会用到的文件，我会提示你把某个目录的路径记下来，后文我将使用这些程序的名字来引用对应的目录。

1. Google App Engine SDK for Java
   <p>[https://developers.google.com/appengine/downloads](https://developers.google.com/appengine/downloads)</p>
   <p>进入下载页面找到 Google App Engine SDK for Java 后下载那个 zip 包，解压缩到任意位置即可。请记下解压后得到的那个文件夹的路径。</p>
2. Twij
   <p>[https://github.com/Qusic/Twij/archive/master.zip](https://github.com/Qusic/Twij/archive/master.zip)</p>
   <p>直接点击上面的链接即可下载最新版本，解压缩到任意位置，进入解压后得到的文件夹，将那个拓展名为 war 的文件留下即可，其余的均用不到。这个 war 文件实际上也是压缩包，新建一个空文件夹，然后将这个 war 文件的内容解压缩到这个文件夹中。请记下这个文件夹的路径。</p>

### 三、修改配置

进入 Twij 的目录，打开 WEB-INF 文件夹，用文本编辑器（如记事本）打开 appengine-web.xml 文件。我已经在需要修改的地方用中文标注了该处应该填写什么信息，对应地填入之后保存即可。

### 四、上传部署

1. 打开终端或命令提示符，输入以下命令，注意将括号部分替换成自己对应的路径。
 - Mac OS X / Linux
   <p>(Google App Engine SDK for Java 的路径)/bin/appcfg.sh update (Twij 的路径)</p>
 - Windows
   <p>(Google App Engine SDK for Java 的路径)\bin\appcfg.cmd update (Twij 的路径)</p>
2. 按照提示输入你的 Google 帐号和密码，等待部署完成。如果过程中出现无法连接的错误请尝试修改 hosts 或者使用 VPN。
3. 部署成功后即可使用。你的 API 地址为 https://(AppID).appspot.com 。
