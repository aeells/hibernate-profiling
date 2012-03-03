Hibernate Profiling
===================

One of the main drivers behind this project was to provide a standardised and purposefully limited approach to data access, and to raise awareness by profiling of what is going on under the hood.

Data Access
-----------
The query API is currently limited as follows:

``` java
void create(final Createable object);
void update(final Updateable object);
void delete(final Deleteable object);
T findById(final Class daoClass, final String id);
T findUnique(final DetachedCriteria criteria);
T findFirstOrderedBy(final DetachedCriteria criteria);
List<T> find(final DetachedCriteria criteria);
List<T> find(final DetachedCriteria criteria, final int firstResult, final int maxResults);
```

There's no reason this cannot be extended, but there's purposefully no ``` java find(String) ``` option, which might improve confidence depending on a team's proficiency with databases.

Profiling
---------
A simple way to observe behaviour beyond the development environment where the effects of indexing and tuning might not be obvious.

Configuration
-------------
Profiling is currently enabled via log4j configuration. This provides the benefit of dynamic Runtime log configuration, not detailed here.

```properties
log4j.appender.persistence=org.apache.log4j.RollingFileAppender
log4j.appender.persistence.File=${catalina.base}/logs/profile-persistence.log
log4j.appender.persistence.MaxFileSize=10MB
log4j.appender.persistence.MaxBackupIndex=10
log4j.appender.persistence.layout=org.apache.log4j.PatternLayout
log4j.appender.persistence.layout.ConversionPattern=%d{ISO8601} %m%n

log4j.logger.com.qmetric.hibernate.profiling.HibernateProfilingInterceptor=TRACE, persistence
log4j.additivity.com.qmetric.hibernate.profiling.HibernateProfilingInterceptor=false
```

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop" xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.0.xsd">

    <bean id="dateTimeSource" class="com.qmetric.utilities.time.DefaultDateTimeSource"/>

    <bean id="persistenceProfiler" class="com.qmetric.hibernate.profiling.HibernateProfilingInterceptor">
        <constructor-arg ref="dateTimeSource"/>
    </bean>

    <aop:config>
        <aop:aspect ref="persistenceProfiler">

            <aop:pointcut id="persistenceWritePointcut"
                          expression="execution(* com.aeells.hibernate.service.HibernateServiceImpl.create(com.aeells.hibernate.Createable)) and args (model) ||
                                      execution(* com.aeells.hibernate.service.HibernateServiceImpl.update(com.aeells.hibernate.Updateable)) and args (model) ||
                                      execution(* com.aeells.hibernate.service.HibernateServiceImpl.delete(com.aeells.hibernate.Deleteable)) and args (model)"/>

            <aop:around pointcut-ref="persistenceWritePointcut"
                        method="profileWrites"/>

            <aop:pointcut id="persistenceFindUniquePointcut"
                          expression="execution(* com.aeells.hibernate.service.HibernateServiceImpl.findUnique(org.hibernate.criterion.DetachedCriteria))"/>

            <aop:around pointcut-ref="persistenceFindUniquePointcut"
                        method="profileFind"/>

            <aop:pointcut id="persistenceFindFirstOrderedByPointcut"
                          expression="execution(* com.aeells.hibernate.service.HibernateServiceImpl.findFirstOrderedBy(org.hibernate.criterion.DetachedCriteria))"/>

            <aop:around pointcut-ref="persistenceFindFirstOrderedByPointcut"
                        method="profileFind"/>

            <aop:pointcut id="persistenceFindListPointcut"
                          expression="execution(* com.aeells.hibernate.service.HibernateServiceImpl.find(org.hibernate.criterion.DetachedCriteria))"/>

            <aop:around pointcut-ref="persistenceFindListPointcut"
                        method="profileFindList"/>

            <aop:pointcut id="persistenceFindListLimitPointcut"
                          expression="execution(* com.aeells.hibernate.service.HibernateServiceImpl.find(org.hibernate.criterion.DetachedCriteria, int, int))"/>

            <aop:around pointcut-ref="persistenceFindListLimitPointcut"
                        method="profileFindList"/>

        </aop:aspect>
    </aop:config>
</beans>
```

Further Development
-------------------
It might be an option to have a nicer DSL than Hibernate's DetachedCriteria also if anyone would like to fork:

``` java
queryFor(Account.class).with("surname", "Smith").with("enabled", true).sortAsc("email").build();
```

``` java
DetachedCriteria.forClass(Account.class)
  .add(Restrictions.eq("surname", "Smith")
  .add(Restrictions.eq("enabled", true))
  .addSort(Order.asc("email"));
```

Contributing
------------
1. [Fork][0] Hibernate profiling
2. Create a topic branch - `git checkout -b my_branch`
3. Push to your branch - `git push origin my_branch`
4. Create a [Pull Request][1] from your branch
5. That's it!

Author
------
Andrew Eells :: ame@andrew-eells.com :: @agile_cto

[0]: http://help.github.com/forking/
[1]: http://help.github.com/pull-requests/
