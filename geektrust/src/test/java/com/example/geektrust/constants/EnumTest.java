package com.example.geektrust.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnumTest {

    @Test
    public void testCategoryEnumValues() {
        assertEquals(3, Category.values().length);
        assertArrayEquals(new Category[] { Category.KID, Category.ADULT, Category.SENIOR_CITIZEN }, Category.values());
    }

    @Test
    public void testDestinationEnumValues() {
        assertEquals(2, Destination.values().length);
        assertArrayEquals(new Destination[] { Destination.CENTRAL, Destination.AIRPORT }, Destination.values());
    }
    
    @Test
    public void testInputCommandsEnumValues() {
        assertEquals(3, InputCommands.values().length);
        assertArrayEquals(new InputCommands[] { InputCommands.BALANCE, InputCommands.CHECK_IN, InputCommands.PRINT_SUMMARY }, InputCommands.values());
    }
}
