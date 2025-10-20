package com.application.altenshop.models;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryStatusTest {

    @Test
    void testEnumValues() {
        assertNotNull(InventoryStatus.valueOf("INSTOCK"));
        assertEquals(3, InventoryStatus.values().length);
    }
}