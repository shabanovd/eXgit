xquery version "3.0";

declare variable $exist:path external;
declare variable $exist:resource external;
declare variable $exist:controller external;
declare variable $exist:prefix external;
declare variable $exist:root external;

let $resource := 
    if ($exist:resource eq "logout") then
        session:invalidate()
    else if (sm:is-authenticated() and ends-with($exist:resource, "login.html")) then
        "index.html"
    else
        $exist:resource
(: 
let $tmp := util:log-system-out(xmldb:get-current-user() || " " || $exist:path || " " || $exist:resource || " " || $exist:controller || " " || $resource)
:)
return

(: Resource paths with '$shared' are loaded from the shared-resources app :)
if ($exist:resource eq "logout") then
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <redirect url="login.html"/>
    </dispatch>
    
(: 
else if (contains($exist:path, "/api/")) then
:)

else if (contains($exist:path, "/$context-path")) then
    <result>
        {request:get-context-path()}
    </result>
    
else if (contains($exist:path, "/$shared/")) then
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <forward url="/shared-resources/{substring-after($exist:path, '/$shared/')}">
            <set-header name="Cache-Control" value="max-age=3600, must-revalidate"/>
        </forward>
    </dispatch>

else if (contains($exist:path, "/resources/")) then
    (: Resources is passed through :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <cache-control cache="yes"/>
    </dispatch>

else if ($resource eq "security_check") then
    let $tmp := util:log-system-out("security_check")
    return
        <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
            <forward url="j_security_check" method="get">
                <add-parameter name="j_username" value="{request:get-parameter("username", "")}"/>
                <add-parameter name="j_password" value="{request:get-parameter("password", "")}"/>
            </forward>
            <redirect url="index.html"/>
        </dispatch>

else if (not(sm:is-authenticated()) and not(ends-with($resource, "login.html"))) then
    (: forward root path to index.xql :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <redirect url="login.html"/>
    </dispatch>

else if ($exist:path eq "/") then
    (: forward root path to index.xql :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <redirect url="index.html"/>
    </dispatch>
else if (ends-with($resource, ".html")) then
    (: the html page is run through view.xql to expand templates :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <view>
    		<forward url="{$exist:controller}/{$resource}" method="get"/>
            <forward url="{$exist:controller}/modules/view.xql"/>
        </view>
		<error-handler>
			<forward url="{$exist:controller}/error-page.html" method="get"/>
			<forward url="{$exist:controller}/modules/view.xql"/>
		</error-handler>
    </dispatch>
else if (ends-with($resource, "cat")) then
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <forward url="{$exist:controller}/modules/cat.xql"/>
    </dispatch>
else
    (: everything else is passed through :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
        <cache-control cache="yes"/>
    </dispatch>
