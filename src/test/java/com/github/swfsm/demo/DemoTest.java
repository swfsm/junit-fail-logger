package com.github.swfsm.demo;

import org.junit.Test;

import static org.junit.Assert.*;

public class DemoTest {

    @Test
    public void testAssertTrue() {
        assertFalse(Boolean.valueOf("true"));
    }

    @Test
    public void testAssertEquals() {
        assertEquals(1, 1);
        assertEquals(true, Boolean.valueOf("false"));
    }

}
