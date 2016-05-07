package cn.jing.manager;

import cn.jing.exception.ModuleNotFoundException;
import cn.jing.exception.NoFreeConnectionException;

import java.sql.Connection;

/**
 * Created by dubby on 16/5/7.
 */
public interface Manager {
    Connection getConnection() throws NoFreeConnectionException;

    Connection getConnection(long timeout) throws NoFreeConnectionException;

    Connection getConnection(String moduleName) throws ModuleNotFoundException, NoFreeConnectionException;

    Connection getConnection(String moduleName, long timeout) throws ModuleNotFoundException, NoFreeConnectionException;
}
