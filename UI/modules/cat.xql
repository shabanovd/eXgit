xquery version "3.0";

(: declare option exist:serialize "method=text media-type=text/plain omit-xml-declaration=yes"; :)
declare option exist:serialize "indent=yes";

let $repo := request:get-parameter("collection", ())
let $path := request:get-parameter("path", ())
let $direct := request:get-parameter("direct", "no")

let $uri := $repo || "/" || $path
let $mime := xmldb:get-mime-type($uri)
return
    if ($direct eq "no") then
        (: util:binary-to-string( git:cat($repo, $path) ) :)
        let $binary := git:cat($repo, $path)
        return response:stream-binary($binary, $mime, ())
    else
        if (util:is-binary-doc($uri)) then
            let $binary := util:binary-doc($uri)
            return response:stream-binary($binary, $mime, ())
        else
            doc($uri)
    