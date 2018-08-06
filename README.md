# ScreenProc
GLSurfaceView、Opengl、SurfaceTexture

# 所做事情：
1) 学习opengl es在android实践编程
2) 使用Opengl、MediaCodec以及MediaMuxer对GLSurfaceView内容进行编码生成视频
3) 视频实时传输

# 注意:
> 使用Opengl接口前必须先使用EGL创建Opengl的上下文运行环境(即EGLContext)  
> 而且EGLContext和线程一一对应，线程之间可以共享EGLContext。  
> GLSurfaceView已经为我们创建好了EGL上下文运行环境。  
> 在使用OpenGL命令(如createProgram等)前需要确定EGL的环境(即EGLContext创建和EGLContext与EGLSurface建立联系(使用makeCurrent)。  
> 弄清楚不同线程之间是否共享等问题  

# 使用EGL的绘图的一般步骤：
1 .  获取 EGL Display对象：eglGetDisplay()  
2 .  初始化与 EGLDisplay之间的连接：eglInitialize()  
3 .  获取 EGLConfig对象：eglChooseConfig()  
4 .  创建 EGLContext实例：eglCreateContext()  
5 .  创建 EGLSurface实例：eglCreateWindowSurface()  
6 .  连接 EGLContext和EGLSurface：eglMakeCurrent()  
7 .  使用 OpenGL ES API 绘制图形：gl_*()  
8 .  切换 front buffer 和 back buffer送显：eglSwapBuffer()  
9 .  断开并释放与 EGLSurface 关联的 EGLContext 对象：eglRelease()  
10 . 删除 EGLSurface对象  
11 . 删除 EGLContext对象  
12 . 终止与 EGLDisplay之间的连接  

# 疑问
1 . 不同线程对全局变量EGLContext进行makeCurrent是不是可以达到不同线程之间共享上下文？  
==>不可以，因为EGLContext是单线程的，类似于JNI中的JNIENV变量。需要再通过eglCreateContext带sharecontext创建一个新的EGLContext。  
2 . 在同一个线程中eglGetCurrentContext和eglCreateContext得到的EGLContext有什么区别？

# 参考：
[Android系统图形栈OpenGLES和EGL介绍](https://woshijpf.github.io/android/2017/09/04/Android%E7%B3%BB%E7%BB%9F%E5%9B%BE%E5%BD%A2%E6%A0%88OpenGLES%E5%92%8CEGL%E4%BB%8B%E7%BB%8D.html)
