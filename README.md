swagger + springboot 1.5.9 + springsecurity
====================================================

公司要做项目，以前一直用的shiro。这回直接用的springboot。所以就花时间实现了一下springsecurity的实现。

##调试已经通过，详细情况请springboot 启动打断点访问。


##### 主要配置文件。
#####  WebSecurityConfig extends WebSecurityConfigurerAdapter
#####  UrlUserService implements UserDetailsService
#####  UrlAccessDecisionManager implements AccessDecisionManager
#####  GenerateTokenForUserFilter extends AbstractAuthenticationProcessingFilter
