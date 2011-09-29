<%-- 
    Document   : index
    Created on : Sep 8, 2011, 8:02:32 PM
    Author     : stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSONP Discovery feed for Eid 2.0 Page</title>
    </head>
    <body>
        <div style="margin-left: 50px">                
            <h1 style="color: #be7429">Jsonp Discovery Feed for Eid 2.0</h1>
            <div style="background-color: #f0f0f0;width: 950px">
                <div style="margin: 30px"><br/>
                    <p>This discovery feed service allows retrieval of discovery data and setting of a cookie, which stores the selected IdP.
                        This is done using jsonp, allowing this feed to be located on another domain than the domain of the web page
                        accessing this information</p>
                    <p>Access the feed through:</p> 
                    <a href="feed?action=discoFeed&callback=callbackid"
                       >/feed?action=discoFeed&callback=callbackid</a>
                    <p>Specify your own custom source of json discovery data (the data in the discoFeed element in the return structure)
                        by adding a <b>source</b> parameter in the query string:</p> 
                    <a href="feed?action=discoFeed&source=https://eid2.3xasecurity.com/Shibboleth.sso/DiscoFeed&callback=callbackid"
                       >/feed?action=discoFeed&source=https://eid2.3xasecurity.com/Shibboleth.sso/DiscoFeed&callback=callbackid</a>
                    <p>Set a cookie that lasts for 100 days which remembers your last IdP choice as the SUNET reference IdP through:</p>
                    <a href="feed?action=setCookie&entityID=https://idp.test.eid2.se/idp/shibboleth&maxAge=100&callback=callbackid"
                       >/feed?action=setCookie&entityID=https://idp.test.eid2.se/idp/shibboleth&maxAge=100&callback=callbackid</a><br/><br/>
                    <p><b style="color: #330000">jquery JavaScript code for accessing json data from this feed:</b></p>
                    <xmp>   $.getJSON(urlToThisFeed+'/feed?action=discoFeed&callback=?', function(json) {
       var IdpEntityIDfromCookie = json.last[0].entityID;
       var firstListedIdpEntityID = json.discoFeed[0].entityID;
       var firstListedIdpDisplayName = json.discoFeed[0].DisplayNames[0].value;
   });
                    </xmp>
                    <p><b style="color: #330000">jquery JavaScript for settting a 100 day cookie remembering a selected IdP sessionID</b></p>
                    <xmp>   $.getJSON(urlToThisFeed+'/feed?action=setCookie&entityID='
       +entityID+'&maxAge=100&callback=?', function(data) {
   });
                    </xmp>
                    <p><a href="doc/Common_discovery_datafeed.htm">Technical documentation</a></p>
                    <br/></br>
                </div>
            </div>
        </div>

    </body>
</html>
