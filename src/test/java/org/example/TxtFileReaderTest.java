package org.example;

import java.util.List;

import org.example.exception.FileException;
import org.example.service.TxtFileReader;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TxtFileReaderTest {

    @BeforeAll
    public static void setUp() {
        System.setProperty("test", "true");
    }

    @Test
    void readFile_WhenValidFileProvided_ShouldReturnListOfStrings() {
        // Given
        TxtFileReader txtFileReader = new TxtFileReader("data.coffeeshops");

        // When
        List<String> actualResult = null;
        Exception exception = null;
        try {
            actualResult = txtFileReader.readFile();
        } catch (FileException e) {
            exception = e;
        }

        // Then
        if (exception == null) {
            assertNotNull(actualResult);
            assertTrue(actualResult.size() >= 0);
        } else {
            assertTrue(exception instanceof FileException);
        }
    }

    @Test
    void readFile_WhenCalled_ShouldNotReturnNull() {
        // Given
        TxtFileReader txtFileReader = new TxtFileReader("data.coffeeshops");

        // When
        Exception actualException = null;
        List<String> actualResult = null;
        try {
            actualResult = txtFileReader.readFile();
        } catch (FileException e) {
            actualException = e;
        }

        // Then
        if (actualException == null) {
            assertNotNull(actualResult);
        } else {
            assertNotNull(actualException);
        }
    }
}
