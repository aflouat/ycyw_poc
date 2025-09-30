package com.openclassrooms.ycywapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class WebConfigTest {

    @Test
    void getters_shouldReturnInjectedValues() {
        WebConfig config = new WebConfig();
        ReflectionTestUtils.setField(config, "hostname", "localhost");
        ReflectionTestUtils.setField(config, "port", 8080);
        ReflectionTestUtils.setField(config, "protocol", "http");

        assertEquals("localhost", config.getHostname());
        assertEquals(8080, config.getPort());
        assertEquals("http", config.getProtocol());
    }
}
