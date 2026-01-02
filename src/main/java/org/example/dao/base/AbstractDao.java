package org.example.dao.base;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.DaoException;
import org.example.dao.exception.ExceptionHandler;
import org.example.dao.mapper.ResultSetMapper;
import org.example.dao.repository.CrudRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements CrudRepository<T, ID> {

    protected final ConnectionProvider connectionProvider;
    protected final ResultSetMapper<T> mapper;
    
    protected ResultSetMapper<T> getMapper() {
        return mapper;
    }

    protected AbstractDao(ConnectionProvider connectionProvider, ResultSetMapper<T> mapper) {
        this.connectionProvider = connectionProvider;
        this.mapper = mapper;
    }

    protected abstract String getInsertSql();

    protected abstract String getUpdateSql();

    protected abstract String getFindAllSql();

    protected abstract String getFindByIdSql();

    protected abstract String getDeleteByIdSql();

    protected abstract String getDeleteAllSql();

    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;

    protected abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;

    @Override
    public T save(T entity) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getInsertSql(), PreparedStatement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(ps, entity);
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setId(entity, generatedKeys.getLong(1));
                }
            }
            return entity;
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "save");
            throw ExceptionHandler.handleException(e);
        }
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getInsertSql())) {
            
            for (T entity : entities) {
                setInsertParameters(ps, entity);
                ps.addBatch();
            }
            ps.executeBatch();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                int index = 0;
                while (generatedKeys.next() && index < entities.size()) {
                    setId(entities.get(index), generatedKeys.getLong(1));
                    index++;
                }
            }
            return entities;
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "saveAll");
            throw ExceptionHandler.handleException(e);
        }
    }

    @Override
    public void update(T entity) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUpdateSql())) {
            setUpdateParameters(ps, entity);
            ps.executeUpdate();
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "update");
            throw ExceptionHandler.handleException(e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getFindByIdSql())) {
            ps.setObject(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(getMapper().map(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "findById");
            throw ExceptionHandler.handleException(e);
        }
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getFindAllSql());
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                result.add(getMapper().map(rs));
            }
            return result;
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "findAll");
            throw ExceptionHandler.handleException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getDeleteByIdSql())) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "deleteById");
            throw ExceptionHandler.handleException(e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(getDeleteAllSql())) {
            ps.executeUpdate();
        } catch (Exception e) {
            ExceptionHandler.handleAndLog(e, "deleteAll");
            throw ExceptionHandler.handleException(e);
        }
    }

    protected abstract void setId(T entity, Long id);
}

