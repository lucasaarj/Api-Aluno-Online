package br.com.alunoonline.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "api.security.token.secret=chave-de-teste-com-mais-de-32-bytes-123456")
class ApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
