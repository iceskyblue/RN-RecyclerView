# RN-RecyclerView
实现了可以UI复用的react native listview，解决RN本身listview的性能问题, 可以下拉刷新，上拉加载更多。
##项目配置
请在项目中的ReactNative文件夹里面放入RN模块，即平时初始化项目生成的node_modules，然后在node_modules\react-native\ReactAndroid目录放入<br>
ReactNative的源码，这里使用的时0.31版本的源码，最后目录结构如下图
![image](https://github.com/iceskyblue/RN-RecyclerView/blob/master/rn-source.png)
##项目运行
[可以直接下载app-debug.apk](https://github.com/iceskyblue/RN-RecyclerView/blob/master/app-debug.apk)，在pc上配置好你的react环境，拷贝index.android.js 和文件夹RecyclerList到react环境，执行react-native start，然后打开apk<br>设置服务器地址。<br>
示例程序见：ReactNative/index.android.js
##运行截图
 ![image](https://github.com/iceskyblue/RN-RecyclerView/blob/master/Screenshot.png)
##实现思路
http://blog.csdn.net/skyblue126/article/details/52062654
