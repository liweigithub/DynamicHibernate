#
项目不存在商业价值，广大学习爱好者可根据自己的喜好重构融入到自己的hibernate项目中，项目就是一个简单的basedao的封装查询 方便快捷 和生硬的hibernate 相比较 优点在于 ：“灵活的查询方式、灵活的ql语句管理、自动实现sql/hql语句的切换执行。” 因为模板采用的是freemarker技术,所以:语句模板块类似于mybatis 配置文件方式，支持各种if for相关标签语法 。 相关用法请参见 freemarker官方语法，简单易懂 分分钟上手。

项目简单的实现方式就是 使用freemarke  api来读取模板缓存文件节点的内容 读取到之后就是对应的sql 或hql 然后由hibernate 来执行语句来执行。项目中大多使用了泛型技术，比如返回值的封装 等等。 支持各种各样的方式查询。
实现了简单的“CRUD” 不像硬编码的方式 将sql或者hql写死到代码里面 维护起来很复杂。使得项目代码看起来也比较杂乱。

這样封装以后代码是代码 hql/sql 单独存在 便于管理。


作者简介：

QQ：1099532501

职业：JAVA DEVELOPER

个人网站：https://blog.hyaroma.com

个人摄影：https://tuchong.com/1649042/
