xquery version "3.0";

declare function local:missing-required($one, $set) {
    let $tmp := for $item in $set return
                    if (ends-with($one, $item)) then
                        true()
                    else
                        ()
    return
        if ($tmp) then
            false()
        else
            true()
};

declare function local:missing-scripts() {
    let $scripts := ("modules/app.xql", "modules/api.xqm")
    return
        distinct-values(rest:resource-functions()/rest:resource-function/@xquery-uri[local:missing-required(string(.), $scripts)])
};

declare function local:resave-missing-scripts() {
    for $script in local:missing-scripts() return
        rest:register-resource-functions($script)
};


(: 
if (count(rest:resource-functions()/*) ne 14) then
    //try to fix by resaving
:)