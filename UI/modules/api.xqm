xquery version "3.0";

module namespace api = 'eXgit/api';

declare namespace restxq="http://exquery.org/ns/restxq";
import module namespace git="http://exist-db.org/git";

declare %restxq:path("eXgit/repository/create")
        %restxq:query-param("collection", "{$uri}", "")
        %restxq:query-param("data", "{$data}", "/db/data")
        %restxq:GET
        function api:create($uri as xs:string*, $data as xs:string*) {
       
    <response>{util:log-system-out($uri)}{
    if ($uri) then
        if (git:create($uri)) then
            let $id := util:uuid()
            let $tmp := xmldb:store($data, "uuid-" || $id || ".xml", <config:repository xmlns:config="eXgit/config" id="{$id}"><config:location>{$uri}</config:location></config:repository>)
            return
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/repository/clone")
        %restxq:query-param("uri", "{$uri}", "")
        %restxq:query-param("collection", "{$collection}", "")
        %restxq:query-param("username", "{$username}", "")
        %restxq:query-param("password", "{$password}", "")
        %restxq:query-param("data", "{$data}", "")
        %restxq:GET
        function api:clone($uri as xs:string*, $collection as xs:string*, $username as xs:string*, $password as xs:string*, $data as xs:string*) {
       
    <response>{
    if ($uri) then
        if (git:clone($uri, $collection, $username, $password)) then
            let $id := util:uuid()
            let $tmp := xmldb:store($data, "uuid-" || $id || ".xml", <config:repository xmlns:config="eXgit/config" id="{$id}"><config:location>{$collection}</config:location></config:repository>)
            return
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/push")
        %restxq:query-param("collection", "{$uri}", "")
        %restxq:query-param("username", "{$username}", "")
        %restxq:query-param("password", "{$password}", "")
        %restxq:GET
        function api:push($uri as xs:string*, $username as xs:string*, $password as xs:string*) {
       
    <response>{
    if ($uri) then
        if (git:push($uri, $username, $password)) then
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/pull")
        %restxq:query-param("collection", "{$uri}", "")
        %restxq:query-param("username", "{$username}", "")
        %restxq:query-param("password", "{$password}", "")
        %restxq:GET
        function api:pull($uri as xs:string*, $username as xs:string*, $password as xs:string*) {
       
    <response>{
    if ($uri) then
        if (git:pull($uri, $username, $password)) then
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/commit")
        %restxq:query-param("collection", "{$uri}", "")
        %restxq:query-param("message", "{$message}", "")
        %restxq:query-param("files", "{$files}", "")
        %restxq:GET
        function api:commit($uri as xs:string*, $message as xs:string*, $files as xs:string*) {
       
    <response>{
    if ($uri) then
        if (git:commit($uri, $message, $files)) then
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/commitAll")
        %restxq:query-param("collection", "{$uri}", "")
        %restxq:query-param("message", "{$message}", "")
        %restxq:GET
        function api:commit($uri as xs:string*, $message as xs:string*) {
       
    <response>{
    if ($uri) then
        let $tmp := util:log-system-out($uri)
        let $tmp := util:log-system-out($message)
        return
        if (git:commit($uri, $message)) then
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/add")
        %restxq:query-param("collection", "{$uri}", "")
        %restxq:query-param("files", "{$files}", "")
        %restxq:GET
        function api:add($uri as xs:string*, $files as xs:string*) {
       
    <response>{
    if ($uri) then
        let $tmp := util:log-system-out($uri)
        let $tmp := util:log-system-out($files)
        return
        if (git:add($uri, $files)) then
            <status>OK</status>
        else
            <status>ERROR</status>
    else
        (
        <status>ERROR</status>,
        <message>No 'collection' parameter. '{$uri}'</message>
        )
    }</response>
};

declare %restxq:path("eXgit/cat")
        %restxq:query-param("collection", "{$repo}", "")
        %restxq:query-param("path", "{$path}", "")
        %restxq:GET
        function api:cat($repo as xs:string*, $path as xs:string*) {
    
    (: %output:media-type("text/plain") :)
    (: %restxq:produces("application/text") :)
    util:binary-to-string( git:cat($repo, $path) )

};