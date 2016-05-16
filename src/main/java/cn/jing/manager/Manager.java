package cn.jing.manager;

import cn.jing.exception.MaxConnectionException;
import cn.jing.exception.ModuleNotFoundException;
import cn.jing.exception.NoFreeConnectionException;

import java.sql.Connection;

/**
 * Created by dubby on 16/5/7.
 */
public interface Manager {
    Connection getConnection() throws NoFreeConnectionException, MaxConnectionException;

    Connection getConnection(long timeout) throws NoFreeConnectionException, MaxConnectionException;

    Connection getConnection(String moduleName) throws ModuleNotFoundException, NoFreeConnectionException, MaxConnectionException;

    Connection getConnection(String moduleName, long timeout) throws ModuleNotFoundException, NoFreeConnectionException, MaxConnectionException;
}
