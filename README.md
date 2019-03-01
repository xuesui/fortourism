# 旅游必备
![image](https://github.com/xuesui/fortourism/raw/master/images/logo.jpg)
## 重庆邮电大学红岩移动开发部Android学员2018年寒假考核作业
### 应用介绍
这是一个天气app，同时能够看到用户的定位，以及简单的地图周边环境。  
* [天气api接口]https://free-api.heweather.com/s6/weather  
* [空气质量接口]https://free-api.heweather.com/s6/air/now  
* [城市列表接口]http://guolin.tech/api/china    
### api介绍  
* 接口获取请求用okHttp方法，返回格式为Json，用Gson和GsonFormat插件解析。
#### 1、实时天气
![](https://github.com/xuesui/fortourism/blob/master/images/1.png)
#### 2、空气质量
![](https://github.com/xuesui/fortourism/blob/master/images/3.png)
#### 3、生活指数
![]((https://github.com/xuesui/fortourism/blob/master/images/4.png)
### 功能介绍
* 进入界面为选择城市界面    
![](https://github.com/xuesui/fortourism/blob/master/images/imagesScreenshot_20190301-174853.jpg)  
* 选择完毕后进入天气界面（可滑动)     
左上的Button可以回到切换城市界面   
最下面的地图Button可以进入定位界面  
![](https://github.com/xuesui/fortourism/blob/master/images/imagesScreenshot_20190301-174826.jpg) 
* 定位地图界面  
![](https://github.com/xuesui/fortourism/blob/master/images/imagesScreenshot_20190301-174844.jpg)
* PS:空气质量接口解析尚未完善 有bug 之后会重新写好
