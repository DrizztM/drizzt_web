package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/** 
*Description: <应用配置类>. <br>
*<p>
	<负责注册除Controller等web层以外的所有bean，包括aop代理，service层，dao层，缓存，等等>
 </p>
* @author Drizzt  
* @version V1.0                             
*/  

@Configuration
@ComponentScan(basePackages = "project", excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = { Controller.class }) })
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class ApplicationConfig {
	

}
