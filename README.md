# Nahida

`Nahida` 是一个使用 `Java` 实现的轻量级 `Web 框架`，可以方便地使用 `中间件` 进行功能扩展 。在设计上借鉴了 [Gin](https://github.com/gin-gonic/gin)，[Express](https://github.com/expressjs/express) 和 [Koa](https://github.com/koajs/koa) 等框架。

## 安装

运行如下命令将 Nahida 安装到 Maven 本地仓库：

```bash
git clone https://github.com/VincilLau/nahida.git
cd nahida
git checkout v0.1.0
mvn install
```

## Hello, world!

首先创建一个 Maven 项目：

```bash
mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

Nahida 仅支持 `Java 17` 及以上的版本，所以你可能需要修改 `pom.xml` 中编辑器版本。例如：

```xml
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

将 Nahida 添加到项目依赖：

```xml
<dependency>
  <groupId>nahida</groupId>
  <artifactId>nahida</artifactId>
  <version>0.1.0</version>
</dependency>
```

在项目的入口文件中编写如下代码：

```java
// 根据你的项目修改包名。
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    // simple 是 Nahida 类的静态方法，用于创建一个 Nahida 对象。
    // 使用 simple 创建的对象安装了几个 Nahida 内置的中间件。
    // 如果要创建一个没有安装任何中间件的 Nahida 实例，请使用：
    // var app = new Nahida();
    var app = Nahida.simple();
    // 注册一个 lambda 表达式作为回调函数，用于处理以 GET 方法请求网站根目录的 HTTP 请求。
    app.get(
        "/",
        // ctx 是 Context 类的实例，表示一次请求-响应的上下文。
        // 通过 ctx.req 和 ctx.resp 可以访问 HTTP 请求和响应。
        // ctx 对象还提供了一系列方便编写 Web 应用的方法。
        ctx -> {
          // 发送一个字符串表示响应内容。
          ctx.send("Hello, Nahida!");
        });
    // 绑定到 localhost:8080
    app.run(8080);
  }
}
```

执行以下命令编译并运行代码：

```bash
mvn compile
# 根据你的 Maven 项目修改包名和类名
mvn exec:java -Dexec.mainClass='hello.App'
```

在浏览器中打开 [http://localhost:8080](http://localhost:8080)，如果浏览器窗口中显示了 `“Hello, Nahida!”` 的文本，则说明运行成功。

也可以使用 `curl` 在命令行中测试，例如：

```bash
curl 'http://localhost:8080' -v
```

curl 的用法可以参考[这里](https://www.ruanyifeng.com/blog/2019/09/curl-reference.html)。

调用 `app.run(port)` 默认绑定到 `localhost`，你也可以使用 `app.run(host, port)` 指定绑定的 IP 地址。例如调用 `app.run("0.0.0.0", 8080)` 可以让网络中的其他主机也可以访问这个网页。

## 请求和响应对象

### 请求对象

```java
package hello;

import nahida.Nahida;

public class Main {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          // 可以通过 ctx.req 或 ctx 提供的方法访问 HTTP 请求的各个字段。
          System.out.println(ctx.req.ip);
          System.out.println(ctx.ip());
          System.out.println(ctx.req.method);
          System.out.println(ctx.method());
          System.out.println(ctx.req.url);
          System.out.println(ctx.url());
          System.out.println(ctx.req.path);
          System.out.println(ctx.path());

          // req.headers 类似于 HashMap<String, ArrayList<String>>
          // 允许重复的 HTTP 标头。
          // HTTP 标头的名称不区分大小写，Nahida 将统一转换为每个单词首字母大写的形式，例如
          // content-type 转换为 Content-Type
          for (var entry : ctx.headers()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
          }

          System.out.println(ctx.req.content);
          System.out.println(ctx.content());
        });
    app.run(8080);
  }
}
```

### 响应对象

响应对象的定义类似于：

```java
public class Response {
  public Status status;
  public Headers headers;
  public ArrayList<Cookie> cookies;
  public byte[] content;
}
```

你可以通过 `ctx.resp` 设置响应的各个属性，但更推荐使用 ctx 对象提供的 API 操作响应对象。

## 路由

```java
package hello;

import nahida.Context;
import nahida.Middleware;
import nahida.Nahida;

// Middleware 是一个函数式接口，除了使用 lambda 表达式之外，
// 我们也可以通过实现 Middleware 接口处理 HTTP 请求。
class Handler implements Middleware {
  @Override
  public void call(Context ctx) throws Exception {
    var content = String.format("%s %s", ctx.method(), ctx.path());
    ctx.send(content);
  }
}

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get("/", new Handler());
    app.head("/", new Handler());
    app.post("/", new Handler());
    app.put("/", new Handler());
    app.patch("/", new Handler());
    app.delete("/", new Handler());
    app.options("/", new Handler());
    app.connect("/", new Handler());
    app.trace("/", new Handler());
    // all 是一个特殊的方法，它可以匹配所有的 HTTP 方法。
    // 如果某个路径没有注册其他方法的回调函数，则将由 all 方法注册的回调函数处理 HTTP 请求
    app.all("/", new Handler());
    app.run(8080);
  }
}
```

各种路由方法返回 Nahida 对象本身，因此可以进行链式调用。

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
            "/a",
            ctx -> {
              ctx.send("/a\n");
            })
        .get(
            "/b",
            ctx -> {
              ctx.send("/b\n");
            });
    app.run(8080);
  }
}
```

Nahida 还支持 `路由分组`，当你的 Web 项目较复杂时，可以对路由分组。

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    var group1 = app.group("/abc");
    group1.get(
        "/xxx",
        ctx -> {
          ctx.send("xxx");
        });
    group1.get(
        "/yyy",
        ctx -> {
          ctx.send("yyy");
        });
    var group2 = app.group("/def");
    group2.post(
        "/xxx",
        ctx -> {
          ctx.send("xxx");
        });
    group2.post(
        "/yyy",
        ctx -> {
          ctx.send("yyy");
        });
    app.run(8080);
  }
}
```

## 路径参数

路由中的路径支持路径参数，语法是 `<type:name>`。目前参数的类型支持 `str`, `int`, `uint`，`float` 和 `uuid`。如果省略类型，则假设类型为 `str`。`name` 必须是一个合法的 Java 标识符。如果匹配成功，你可以通过 `ctx.param(name)` 访问路径参。`ctx.param(name)` 的返回类型为 `Object`，你需要使用强制类型转换将 Object 转换为对应的类型。注意，int 型和 uint 路径参数对应的 Java 类型是 `Long`，可以匹配超过 `2^32 -1` 的整数。uint 型路径参数只能匹配 `0` 和 `正整数`，不能匹配 `负数`。float 型路径参数对应的 Java 类型是 `Double`。

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/<int:a>/<uuid:b>/<str:c>/<d>",
        ctx -> {
          System.out.println((Long) ctx.param("a"));
          System.out.println((String) ctx.param("b"));
          System.out.println((String) ctx.param("c"));
          System.out.println((String) ctx.param("d"));
        });
    app.run(8080);
  }
}
```

执行以下命令测试是否可以正常工作：

```bash
curl 'localhost:8080/12345/a9fea24b-1c69-43d1-ac40-05c51dfe204b/nahida/hello' -v
```

## 查询字符串

可以通过 `ctx.query(name)` 访问查询字符串。查询字符串的名称 `可以重复`，通过 `ctx.queryAll(name)` 获取指定名称的所有查询字符串的值。`ctx.defaultQuery(name)` 可以在查询字符串不存在时返回默认值。

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/query",
        ctx -> {
          System.out.println(ctx.query("a"));
          // 返回 ArrayList<String>
          System.out.println(ctx.queryAll("b"));
          System.out.println(ctx.defaultQuery("c", "ttt"));
        });
    app.run(8080);
  }
}
```

执行以下命令测试是否可以正常工作：

```bash
curl 'localhost:8080/query?a=xxx&b=yyy&b=zzz' -v
```

## 发送各种格式的响应

ctx 对象提供了一系列方法自定义响应内容。这些方法均遵循 HEAD 方法的语义。也就是说，如果请求方法为 HEAD，这些方法会将只添加 `Content-Length` 标头，不设置 `ctx.resp.content`。

### HTML

```java
package hello;

import nahida.Nahida;
import nahida.http.Status;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          // 使用 ctx.html(text) 发送 HTML。
          ctx.html("<h1>Hello, Nahida!</h1>");
        });
    app.get(
        "/404",
        ctx -> {
          // 可以指定响应状态码。默认为 Status.OK。
          ctx.html(Status.NOT_FOUND, "<h1>404 NOT FOUND</h1>");
        });
    app.run(8080);
  }
}
```

### JSON

Nahida 使用 [Jackson](https://github.com/FasterXML/jackson-core) 处理 JSON。

```java
package hello;

import nahida.Nahida;

class Student {
  public int id;
  public String name;
  public double score;

  public Student(int id, String name, double score) {
    this.id = id;
    this.name = name;
    this.score = score;
  }
}

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          var stu = new Student(12345, "Tom", 98.5);
          ctx.json(stu);
        });
    app.run(8080);
  }
}
```

### 自定义

`ctx.send` 提供了多个重载，可以根据你的需求指定状态码，内容，类型，字符集等属性。

```java
public void send(Status status, byte[] content, String contentType);
public void send(byte[] content, String contentType);
public void send(Status status, String text, Charset charset, String contentType);
public void send(String text, Charset charset, String contentType);
public void send(String text, String contentType);
public void send(String text);
public void send(Status status, String text, String contentType);
public void send(Status status, String text);
```

## 设置 MIME 类型和默认字符集

Nahida 对象有两个 public 字段 `defaultMimeType`，`defaultCharset` ，可以指定默认的 MIME 类型和字符集。

### 发送文件

使用 `ctx.sendFile(path)` 发送文件，支持相对路径。如果不指定 MIME 类型，Nahida 将根据文件的扩展名推断 MIME 类型。

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          ctx.sendFile("logo.png");
        });
    app.run(8080);
  }
}
```

### 重定向

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
            "/",
            ctx -> {
              ctx.redirect("/home");
            })
        .get(
            "/home",
            ctx -> {
              ctx.html("<h1>Home</h1>");
            });
    app.run(8080);
  }
}
```

## 数据绑定

ctx 对象提供了一系列方法，可以方便地将 HTTP 请求中的`负载数据`、`路径参数`、`查询字符串` 和`标头`转换为易于操作的 Java 对象。这些功能是使用 [Jackson](https://github.com/FasterXML/jackson-core) 实现的。如果转换失败，将抛出 `Http400Exception`。

### 绑定请求数据

Nahida 支持将请求中的 JSON 转换为 Java 对象。

```java
package hello;

import nahida.Nahida;

class Student {
  public int id;
  public String name;
  public double score;
}

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.post(
        "/",
        ctx -> {
          var stu = ctx.bindJson(Student.class);
          System.out.printf("id=%d, name=%s, score=%f\n", stu.id, stu.name, stu.score);
        });
    app.run(8080);
  }
}
```

测试命令：

```bash
curl 'localhost:8080' -X POST -d '{"id":12345,"name":"Tom","score":98.5}'
```

### 绑定路径参数

```java
package hello;

import nahida.Nahida;

class Student {
  public int id;
  public String name;
  public double score;
}

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/<uint:id>/<str:name>/<float:score>",
        ctx -> {
          var stu = ctx.bindParam(Student.class);
          System.out.printf("id=%d, name=%s, score=%f\n", stu.id, stu.name, stu.score);
        });
    app.run(8080);
  }
}
```

测试命令：

```bash
curl 'localhost:8080/12345/Tom/98.5'
```

### 绑定查询字符串

```java
package hello;

import nahida.Nahida;

class Student {
  public int id;
  public String name;
  public double score;
}

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          var stu = ctx.bindQuery(Student.class);
          System.out.printf("id=%d, name=%s, score=%f\n", stu.id, stu.name, stu.score);
        });
    app.run(8080);
  }
}
```

测试命令：

```bash
curl 'localhost:8080/?id=12345&name=Tom&score=98.5'
```

### 绑定 HTTP 标头

```java
package hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nahida.Nahida;

// 忽略该类中没有的字段
@JsonIgnoreProperties(ignoreUnknown = true)
class UserAgent {
  @JsonProperty("Host")
  public String host;

  @JsonProperty("User-Agent")
  public String userAgent;
}

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          var ua = ctx.bindHeader(UserAgent.class);
          System.out.println(ua.host);
          System.out.println(ua.userAgent);
        });
    app.run(8080);
  }
}
```

测试命令：

```bash
curl 'localhost:8080/' -v
```

## Cookies

ctx 对象提供了读取请求中的 Cookies 以及在响应中添加 Cookies 的方法。Cookies 的名称可以重复。

```java
package hello;

import nahida.Cookie;
import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          var cookie = ctx.cookie("nahida");
          if (cookie == null) {
            ctx.addCookie(Cookie.builder("nahida").value("hello, world!").maxAge(300).build());
            ctx.html("<h1>no cookie</h1>");
          } else {
            ctx.html("<h1>cookie</h1>");
          }
        });
    app.run(8080);
  }
}
```

## 挂载静态文件

使用 `app.mount(target, source)` 将本地文件系统上的文件或目录挂载到指定的 URL 上。挂载的静态文件是只读的，只支持 `GET` 和 `HEAD` 方法。

例如将用户目录挂载到网站的根目录上：

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.mount("/", "/home/admin");
    app.run(8080);
  }
}
```

访问 [http://localhost:8080](http://localhost:8080) 查看效果。

也可以挂载文件：

```java
package hello;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    // 支持相对路径。
    app.mount("/logo.png", "logo.png");
    app.run(8080);
  }
}
```

如果某个 URL 映射到了一个目录，那么将会查找该目录下是否有名为 `index.html` 的文件。如果该文件存在，则将该文件作为响应内容。如果不存在，则为该目录自动生成索引。使用 `MountFlags.NO_INDEX` 和 `MountFlags.NO_DIR` 可以分别禁用这两种行为：

```java
package hello;

import static nahida.MountFlags.NO_DIR;
import static nahida.MountFlags.NO_INDEX;

import nahida.Nahida;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.mount("/", "/home/admin", NO_INDEX | NO_DIR);
    app.run(8080);
  }
}
```

## 异常

Nahida 中定义了五个异常类，作用如下：

|       类名       |                                              作用                                              |
| :--------------: | :--------------------------------------------------------------------------------------------: |
| AbsPathException | Nahida 中的 URL 必须是绝对路径。如果进行路由注册，挂载静态文件时使用了相对路径将会抛出此异常。 |
| Http400Exception |                              无法解析 HTTP 请求，常见于数据绑定。                              |
| Http403Exception |                         访问被禁止，常见于没有权限访问挂载的静态文件。                         |
| Http404Exception |                没有找到 URL 对应的资源，常见于路由阶段以及访问挂载的静态文件。                 |
| Http405Exception |                    请求方法不被支持，常见于路由阶段以及访问挂载的静态文件。                    |

Nahida 不支持 URL 相对路径，捕获到 AbsPathException 异常一般意味着编程错误。

Nahida 默认安装的 `Recovery` 中间件将会处理后四种异常。你可以通过安装自定义中间件处理 `Http*` 异常。也可以在中间件中抛出此异常让上层的中间件处理。

## 中间件

Nahida 支持使用中间件机制灵活地扩展功能。Nahida 中间件机制的设计受到了 [Gin](https://github.com/gin-gonic/gin)，[Express](https://github.com/expressjs/express) 和 [Koa](https://github.com/koajs/koa) 等框架的启发。

有关中间件机制可以参考[这篇文章](https://juejin.cn/post/7012031464237694983)。

在 Nahida 中，使用 `app.use` 方法添加全局中间件，使用 `ctx.next()` 调用下一个中间件。例如可以使用如下方法自定义 404 页面：

```java
package hello;

import nahida.Nahida;
import nahida.except.Http404Exception;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.use(
        ctx -> {
          try {
            ctx.next();
          } catch (Http404Exception e) {
            System.out.println(e.toString());
            ctx.html("<h1>404 Not Found</h1>");
          }
        });
    app.get(
        "/",
        ctx -> {
          throw new Http404Exception(ctx.path());
        });
    app.run(8080);
  }
}
```

Nahida 还支持定义局部中间件：

```java
package hello;

import nahida.Nahida;
import nahida.except.Http404Exception;

public class App {
  public static void main(String[] args) throws Exception {
    var app = Nahida.simple();
    app.get(
        "/",
        ctx -> {
          try {
            ctx.next();
          } catch (Http404Exception e) {
            System.out.println(e.toString());
            ctx.html("<h1>404 Not Found</h1>");
          }
        },
        ctx -> {
          throw new Http404Exception(ctx.path());
        });
    app.run(8080);
  }
}
```

Nahida 处理一个 HTTP 请求的流程如下：

1. 使用 [Netty](https://github.com/netty/netty) 接受一个 HTTP 请求；
2. 将该 HTTP 请求转换为 `nahida.http.Request` 对象；
3. 创建一个 `nahida.http.Response` 对象表示 HTTP 响应；
4. 创建一个 `nahida.Context` 表示请求-响应上下文；
5. 依次调用全局中间件（挂载静态文件是使用全局中间件实现的）；
6. 路由，获得对应的局部中间件（可能会抛出 `Http404Exception` 和 `Http405Exception`）；
7. 依次调用局部中间件；
8. 将 `nahida.http.Response` 对象转换为 Netty 中的响应对象；
9. 通过 Netty 将响应发送给客户端。

使用 `Nahida.simple()` 创建的 Nahida 对象默认依次安装了如下全局中间件：

|       名称        |                         功能                         |
| :---------------: | :--------------------------------------------------: |
|      Logging      |               输出请求-响应信息的日志                |
|     Recovery      |             处理下级中间件抛出的各种异常             |
| PathNormalization |   将 URL 路径规格化，去掉 `/.`，`/..` 和重复的 `/`   |
|    DateHeader     |               在响应中加入 `Date` 标头               |
|   ServerHeader    | 在响应中加入 `Server: Nahida/${Nahida.VERSION}` 标头 |

## 维护者

[@Vincil Lau](https://github.com/VincilLau)

## 使用许可

[MIT](./LICENSE)
