# ScreenProc
GLSurfaceView、Opengl、SurfaceTexture

# 所做事情：
1) 学习opengl es在android实践编程
2) 使用Opengl、MediaCodec以及MediaMuxer对GLSurfaceView内容进行编码生成视频
3) 视频实时传输

# 注意:
使用Opengl接口前必须先使用EGL创建Opengl的上下文运行环境。
GLSurfaceView已经为我们创建好了EGL上下文运行环境。

# 使用EGL的绘图的一般步骤：
1 .  获取 EGL Display 对象：eglGetDisplay()
2 .  初始化与 EGLDisplay 之间的连接：eglInitialize()
3 .  获取 EGLConfig 对象：eglChooseConfig()
4 .  创建 EGLContext 实例：eglCreateContext()
5 .  创建 EGLSurface 实例：eglCreateWindowSurface()
6 .  连接 EGLContext 和 EGLSurface：eglMakeCurrent()
7 .  使用 OpenGL ES API 绘制图形：gl_*()
8 .  切换 front buffer 和 back buffer 送显：eglSwapBuffer()
9 .  断开并释放与 EGLSurface 关联的 EGLContext 对象：eglRelease()
10 . 删除 EGLSurface 对象
11 . 删除 EGLContext 对象
12 . 终止与 EGLDisplay 之间的连接

# 参考：
[Android系统图形栈OpenGLES和EGL介绍](https://woshijpf.github.io/android/2017/09/04/Android%E7%B3%BB%E7%BB%9F%E5%9B%BE%E5%BD%A2%E6%A0%88OpenGLES%E5%92%8CEGL%E4%BB%8B%E7%BB%8D.html)
