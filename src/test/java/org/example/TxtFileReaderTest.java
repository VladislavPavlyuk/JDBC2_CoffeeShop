package org.example;

import org.example.exception.FileException;
import org.example.service.TxtFileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.setProperty;

public class TxtFileReaderTest {

    @BeforeAll
    static void createProperty() {
        setProperty("test", "true");
    }

    @Test
    void readFile_ShouldReturnListOfStringsFromFile_WenCalled() {

        TxtFileReader txtFileReader = new TxtFileReader("data.coffeeshops");
        try {
            List<String> actual =  txtFileReader.readFile();
            List<String> expected = new ArrayList<>();
            expected.add("mathematics");
            expected.add("biology");
            expected.add("physics");

            Assertions.assertEquals(expected,actual, "read text");
        } catch (FileException e) {
            throw new RuntimeException(e);
        }
    }
}
