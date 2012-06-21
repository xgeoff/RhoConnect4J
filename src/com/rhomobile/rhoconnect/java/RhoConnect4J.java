/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rhomobile.rhoconnect.java;

import com.rhomobile.rhoconnect.restclient.RestClient4J;
import com.rhomobile.rhoconnect.restclient.RestResponse;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author gporemba
 */
public class RhoConnect4J implements RhoConnectClient {
    
    protected static final String LOGIN = "login";
    protected static final String PASSWORD = "password";
    
    protected static final String COOKIE_KEY = "Set-Cookie";
    protected static final String COOKIE_PARAM = "Cookie";
    
    protected static final String LOGIN_URL_APPEND = "/login";
    protected static final String QUERY_URL_APPEND = "/api/get_db_doc";
    protected static final String UPDATE_URL_APPEND = "/api/push_objects";
    protected static final String API_TOKEN_URL_APPEND = "/api/get_api_token";
    protected static final String DELETE_URL_APPEND = "/api/push_deletes";
    
    protected static final String API_TOKEN_PARAM = "api_token";
    protected static final String DOC_PARAM = "doc";
    protected static final String CONTENT_TYPE_PARAM = "content-type";
    protected static final String OBJECTS_PARAM = "objects";
    protected static final String SOURCE_ID_PARAM = "source_id";
    protected static final String USER_ID_PARAM = "user_id";
    protected static final String REBUILD_MD_PARAM = "rebuild_md";
    
    protected static final String JSON_CONTENT_TYPE = "application/json";
    
    protected String serverUrl = null;
    protected String authToken = null;
    
    public RhoConnect4J(String url) {
        serverUrl = url;
    }

    @Override
    public boolean authenticate(String username, String password, Map<String, String> params) {
        
        Map<String, String> credentials;
        String content;
        RestResponse response = null;
        String cookie;
        boolean success = false;
        
        try {
        
            credentials = new HashMap<String, String>();
            credentials.put(LOGIN, username);
            credentials.put(PASSWORD, password);

            content = toJSON(credentials);

            response = RestClient4J.post(serverUrl + LOGIN_URL_APPEND, content, params);
            List<String> cookieList = (List<String>) response.headers().get(COOKIE_KEY);
            
            if (cookieList.size() > 0) {
                cookie = cookieList.get(0);
                
                params = new HashMap<String, String>();
                params.put(COOKIE_PARAM, cookie);
                response = RestClient4J.post(serverUrl + API_TOKEN_URL_APPEND, "", params);
                authToken = response.body();
                
                if (authToken != null && authToken.length() > 0) {
                    success = true;
                }
            }
            
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
        return success;     
    }

    @Override
    public boolean create(String resource, String partition, Map<String, Object> data) {
        return update(resource, partition, data);
    }

    @Override
    public boolean delete(String resource, String partition, List objectIds) {
        RestResponse response;
        String doc;
        Map<String, String> params;
        Map<String, Object> deleteObjects;
        boolean success = false;
        
        try {
            deleteObjects = new HashMap<String, Object>();
            doc = buildDocString(resource, partition);
            deleteObjects.put(API_TOKEN_PARAM, authToken);
            deleteObjects.put(DOC_PARAM, doc);
            deleteObjects.put(OBJECTS_PARAM, objectIds);
            deleteObjects.put(SOURCE_ID_PARAM, resource);
            deleteObjects.put(USER_ID_PARAM, partition);
            String content = toJSON(deleteObjects);
            
            params = new HashMap<String, String>();
            params.put(CONTENT_TYPE_PARAM, JSON_CONTENT_TYPE);
            response = RestClient4J.post(serverUrl + DELETE_URL_APPEND, content, params);
            
            if (response.code() == HttpURLConnection.HTTP_OK) {
                success = true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return success;
    }

    @Override
    public Map<String, Object> query(String resource, String partition) {
        
        RestResponse response;
        String doc;
        Map<String, Object> data = null;
        Map<String, String> params;
        
        try {
            params = new HashMap<String, String>();
            doc = buildDocString(resource, partition);
            params.put(API_TOKEN_PARAM, authToken);
            params.put(DOC_PARAM, doc);
            String content = toJSON(params);
            
            params = new HashMap();
            params.put(CONTENT_TYPE_PARAM, JSON_CONTENT_TYPE);
            response = RestClient4J.post(serverUrl + QUERY_URL_APPEND, content, params);
            data = toJSON(response.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
            
        return data;
    }

    @Override
    public boolean update(String resource, String partition, Map<String, Object> data) {
        
        RestResponse response;
        String doc;
        Map<String, String> params;
        Map<String, Object> pushObjects;
        boolean success = false;
        
        try {
            pushObjects = new HashMap<String, Object>();
            doc = buildDocString(resource, partition);
            pushObjects.put(API_TOKEN_PARAM, authToken);
            pushObjects.put(DOC_PARAM, doc);
            pushObjects.put(OBJECTS_PARAM, data);
            pushObjects.put(SOURCE_ID_PARAM, resource);
            pushObjects.put(USER_ID_PARAM, partition);
            pushObjects.put(REBUILD_MD_PARAM, Boolean.FALSE);
            String content = toJSON(pushObjects);
            params = new HashMap<String, String>();
            params.put(CONTENT_TYPE_PARAM, JSON_CONTENT_TYPE);
            response = RestClient4J.post(serverUrl + UPDATE_URL_APPEND, content, params);
            
            if (response.code() == HttpURLConnection.HTTP_OK) {
                success = true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return success;
    }

    /* The following convenience method is utilized in case someone wants to utilize
     *  a different json parser. In that case you could simply inherit from this
     *  class and override this one method to provide a completely different
     *  json parsing mechanism
     */
    protected String toJSON(Map attributes) {
        
        return JSONObject.toJSONString(attributes);
    }

    protected Map toJSON(String content) {
        return (Map)JSONValue.parse(content);
    }
    
    protected String buildDocString(String resource, String partition) {
        StringBuilder doc;
        
        doc = new StringBuilder();
        doc.append("source:application:");
        doc.append(resource);
        doc.append(":");
        doc.append(partition);
        doc.append(":md");
        
        return doc.toString();
    }
    
}
