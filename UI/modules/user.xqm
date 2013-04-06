xquery version "3.0";

module namespace user="eXgit/user/templates";

import module namespace templates="http://exist-db.org/xquery/templates" ;
import module namespace config="eXgit/config" at "config.xqm";

(:~
 : @param $node the HTML node with the class attribute which triggered this call
 : @param $model a map containing arbitrary data - used to pass information between template calls
 :)
declare function user:display($node as node(), $model as map(*)) {
    <div class="pull-right nav"><a>{xmldb:get-current-user()}</a> | <a id="logout-btn" class="btn"><i class="icon-signout"/></a>
        <script>
            $(function() {{  
                $("#logout-btn").click(function() {{
                    window.location.href="logout";
                }});
            }});
        </script>
    </div>
};