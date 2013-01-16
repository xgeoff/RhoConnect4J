RhoConnect4J is a generic java library for communicating with a subset of RhoConnect's RESTful api's.

RhoConnect4J exposes api's in a simple Query-Create-Update-Delete format, so making calls from any java based program is simple and straightforward.

The library has dependencies on the Simple JSON parser

http://code.google.com/p/json-simple/

which appears to be included with Eclipse (at least it was for me).  So if you have eclipse installed, you may already have the simple json lib.

The only other dependency is my RestClient Library, whose source is available here:

    https://github.com/xgeoff/RestClient4J

So in order to use the RhoConnect4J client, first you will want to authenticate with the server. You do that as follows
    
    Map<String, String> params = new HashMap();
    params.put("content-type", "application/json");

    RhoConnectClient client = new RhoConnect4J(url);
    // The url is a String pointing to your RhoConnect Server
    client.authenticate("rhoadmin", "", params);
    // The above authenticate method takes Strings for admin username and password. The first parameter is the admin
    //  username and the second parameter is the password.

Note that the url will be the url for your RhoConnect server, and the credentials will be your admin user and password.

Now let's say that you have a model called 'Product', with data in a canonical 'Hash of Hashes' format required by RhoConnect (See RhoConnect docs if you don't understand).  You would do the following to communicate an update with the server. We'll reuse the instance of the client we created above, otherwise we will have to authenticate again.

    boolean success = client.update("x", "Product", data);

where 'x' is the resource name and 'Product' is the partition (i.e. model).  Again, data is a HashMap and contains the data you are pushing to the server.

the update and create methods work in the same way, and actually call the same api under the hood.  The underlying push_objects api works whether you are doing a create or an update.


    
