package org.example.menu;

import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.staffDAO.StaffDao;
import org.example.model.Shift;
import org.example.model.Staff;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MenuExecutorTest {

    @Test
    void menuItem1Execute_WhenValidInputProvided_ShouldNotThrowException() {
        // Given
        String input = "5\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem1Execute(scanner);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem2Execute_WhenValidInputProvided_ShouldNotThrowException() {
        // Given
        String input = "Coffee Shop 1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem2Execute(scanner);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem3Execute_WhenValidInputProvided_ShouldNotThrowException() {
        // Given
        String input = "Morning Shift\nИван\nПетров\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem3Execute(scanner);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem4Execute_WhenValidInputProvided_ShouldNotThrowException() {
        // Given
        String input = "1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem4Execute(scanner);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem5Execute_WhenValidInputProvided_ShouldNotThrowException() {
        // Given
        String input = "Coffee Shop 1\n1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem5Execute(scanner);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem6Execute_WhenValidInputProvided_ShouldNotThrowException() {
        // Given
        String input = "1\nCoffee Shop 1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem6Execute(scanner);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem7Execute_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem7Execute();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem8Execute_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem8Execute();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem9Execute_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem9Execute();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void menuItem10Execute_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            MenuExecutor.menuItem10Execute();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}










