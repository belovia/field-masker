package ru.belovia.masklib;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void parseJson() {
        // Arrange
        String json = """
        {
            "name": "John Doe",
            "email": "john@example.com",
            "password": "secret123",
            "phones": ["123456789", "987654321"],
            "address": {
                "street": "123 Main St",
                "city": "Springfield"
            }
        }
        """;

        // Action
        Map<String, Object> result = JsonParser.parseJson(json);

        // Asserts
        assertNotNull(result);
        assertEquals("John Doe", result.get("name"));
        assertEquals("john@example.com", result.get("email"));
        assertEquals("secret123", result.get("password"));
    }

    @Test
    void parseBigJson() {
        // Arrange
        String json = """
                        {
                           "id": 12345,
                           "name": "John Doe",
                           "email": "john.doe@example.com",
                           "age": 30,
                           "isVerified": true,
                           "address": {
                             "street": "123 Main St",
                             "city": "Springfield",
                             "zipCode": 123456
                           },
                           "phones": ["+1234567890", "+0987654321"],
                           "orders": [
                             {
                               "orderId": 1,
                               "product": "Laptop",
                               "price": 1200.50,
                               "status": "Shipped"
                             },
                             {
                               "orderId": 2,
                               "product": "Mouse",
                               "price": 25.99,
                               "status": "Pending"
                             }
                           ],
                           "preferences": {
                             "newsletter": false,
                             "notifications": {
                               "email": true,
                               "sms": false
                             }
                           }
                         }
                         
                """;

        // Action
        Map<String, Object> result = JsonParser.parseJson(json);

        // Asserts
        assertNotNull(result);
        assertEquals("John Doe", result.get("name"));
        assertEquals("john.doe@example.com", result.get("email"));
        assertEquals("30", result.get("age"));

        Object address = result.get("address");
        if (address instanceof Map) {
            assertTrue(((Map<?, ?>) address).containsValue("123 Main St"));
            assertTrue(((Map<?, ?>) address).containsValue("Springfield"));
            assertTrue(((Map<?, ?>) address).containsValue("123456"));
        }
    }
}