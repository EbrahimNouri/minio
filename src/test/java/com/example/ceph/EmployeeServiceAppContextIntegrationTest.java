package com.example.ceph;

import com.sajayanegar.storage.service.school.S3ServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration
//@SpringBootTest
public class EmployeeServiceAppContextIntegrationTest {

    private S3ServiceImpl service = new S3ServiceImpl();


    @Test
    public void whenContextLoads_thenServiceISNotNull() {
        assertThat(service).isNotNull();
    }

}