/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rhomobile.rhoconnect.java;

import java.util.List;
import java.util.Map;

/**
 *
 * @author gporemba
 */
public interface RhoConnectClient {
    boolean authenticate(String username, String password, Map<String, String> params);
    Map<String, Object> query(String resource, String partition);
    boolean create(String resource, String partition, Map<String, Object> data);
    boolean update(String resource, String partition, Map<String, Object> data);
    boolean delete(String resource, String partition, List objectIds);
}
