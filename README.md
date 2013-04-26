Twij
====
Twitter API Proxy in Java

功能特色和已知问题
----

- 完全支持 Twitter Rest API v1.1
- 支持 Streaming API（受容器条件限制，已在 Google App Engine 测试成功。）
- 支持 multipart/form-data 格式的 POST 请求（从根本上解决 Tweetbot 不能关注和收藏的问题。）
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
   <p>http://Qusic.me/Tweetbot-API</p>
 - Twitter for Mac 和 Tweetbot for Mac
   <p>http://Qusic.me/Twitter-API</p>

使用方法
----

设置以下的环境变量后部署即可。

- ConsumerKey
- ConsumerSecret
- AccessToken
- AccessTokenSecret
