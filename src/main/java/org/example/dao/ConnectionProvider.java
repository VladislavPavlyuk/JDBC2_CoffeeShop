package org.example.dao;

import org.example.exception.ConnectionDBException;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection() throws ConnectionDBException;
}













