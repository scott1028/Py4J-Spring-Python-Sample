package com.taisys.server;

import com.taisys.app.ssd.daoService.IAuditLogDaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class AppTest {
    @Resource(name = "auditLogDaoService")
    protected IAuditLogDaoService auditLogDaoService;

    @Test
    public void demo() throws Exception {
        assert true;
    }
}