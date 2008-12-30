package org.springcommerce.profile.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;

@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    protected EntityManager em;

    public EntityManager getEntityManager() {
        if (em == null) {
            em = ((EntityManagerFactory) applicationContext.getBean("entityManagerFactory")).createEntityManager();
        }
        return em;
    }

    @BeforeClass
    public void setup() {
        getEntityManager();
    }
}
