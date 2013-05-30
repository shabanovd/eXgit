xquery version "3.0";

module namespace app="eXgit/templates";

declare namespace restxq="http://exquery.org/ns/restxq";

(: import module namespace templates="http://exist-db.org/xquery/templates"; :)
import module namespace config="eXgit/config" at "config.xqm";

import module namespace git="http://exist-db.org/git";

(:~
 : @param $node the HTML node with the class attribute which triggered this call
 : @param $model a map containing arbitrary data - used to pass information between template calls
 :)
declare function app:repos($node as node(), $model as map(*)) {
    <div class="row-fluid">
        <div class="btn-group"><!-- btn-toolbar -->
            <a class="btn btn-small" href="#" id="repos-reload"><i class="icon-repeat"></i> Reload</a>
            <script>
                $( "#repos-reload" )
                .button()
                .click(function() {{
                    eXgit.viewRepositories();
                }});
            </script>
            <a class="btn btn-small" href="#" id="repo-create"><i class="icon-certificate"></i> Create</a>
            <a class="btn btn-small" href="#" id="repo-clone"><i class="icon-download-alt"></i> Clone</a>
        </div>
        <div id="repos-view">
            {
                local:repos(())
            }
        </div>
        <div id="repo-create-dialog" title="Create a New Git Repository">
            <form class="form-horizontal">
                <fieldset>
                    <legend>Please determine the collection for the new repository</legend>
                    
                    <div class="control-group">
                        <label class="control-label" for="repo-create-collection">Collection:</label><!--Parent collection-->
                        <div class="controls">
                            <input id="repo-create-collection" name="repo-create-collection" type="text" value="/db" required="required"/>
                        </div>
                    </div>
                </fieldset>
            </form>
        </div>
        <script>
            $(function() {{
                $( "#repo-create-dialog" ).dialog({{
                    autoOpen: false,
                    height: 400,
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttons: {{
                        "Finish": function() {{
                            eXgit.createRepository(
                                $( "#repo-create-collection" ).val(), 
                                "{$config:data-root}"
                            );
                        }},
                        Cancel: function() {{
                            $( this ).dialog( "close" );
                        }}
                    }}
                }});
            }});

             $( "#repo-create" )
                .button()
                .click(function() {{
                    $( "#repo-create-dialog" ).dialog( "open" );
                }});
        </script>
        <div id="repo-clone-dialog" title="Clone Git Repository">
            <form class="form-horizontal">
                <!-- <legend>Enter the location of the source repository</legend> -->
                <fieldset>
                    <legend>Locations</legend>
                    
                    <div class="control-group">
                        <label class="control-label" for="repo-clone-collection">Collection:</label>
                        <div class="controls">
                            <input id="repo-clone-collection" name="repo-clone-collection" type="text" required="required"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label" for="repo-clone-URI">URI:</label>
                        <div class="controls">
                            <input class="input-xlarge" id="repo-clone-URI" name="repo-clone-URI" type="text" placeholder="https://..." required="required"/>
                        </div>
                    </div>
                    
                </fieldset>
                <fieldset>
                    <legend>Authentication</legend>
                    
                    <div class="control-group">
                        <label class="control-label" for="repo-clone-user">User:</label>
                        <div class="controls">
                            <input class="input-xlarge" id="repo-clone-username" name="repo-username" type="text" required="required"/>
                        </div>
                    </div>
                    
                    <div class="control-group">
                        <label class="control-label" for="repo-clone-password">Password:</label>
                        <div class="controls">
                            <input class="input-xlarge" id="repo-clone-password" name="repo-clone-password" type="password" required="required"/>
                        </div>
                    </div>
                </fieldset>
            </form>
        </div>
        <script>
            $(function() {{
                $( "#repo-clone-dialog" ).dialog({{
                    autoOpen: false,
                    height: 500,
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttons: {{
                        "Finish": function() {{
                            eXgit.cloneRepository(
                                $( "#repo-clone-URI" ).val(), 
                                $( "#repo-clone-collection" ).val(), 
                                $( "#repo-clone-username" ).val(), 
                                $( "#repo-clone-password" ).val(), 
                                "{$config:data-root}");
                        }},
                        Cancel: function() {{
                            $( this ).dialog( "close" );
                        }}
                    }}
                }});
            }});

             $( "#repo-clone" )
                .button()
                .click(function() {{
                    $( "#repo-clone-dialog" ).dialog( "open" );
                }});
        </script>
    </div>
};

declare %restxq:path("eXgit/repositories/view")
        %restxq:query-param("collection", "{$col}", "")
        %restxq:GET
        function local:repos($col) {

    <ul class="nav nav-list">
      <li id="repositories" class="nav-header">Repositories</li>
      {
          for $repo in collection($config:data-root)//config:repository return
              let $path := $repo/config:location/text()
              return
              <li><a href="#" id="{$repo/@id}" onClick="eXgit.viewRepository('{$path}', '')">{$path}</a></li>
      }
      <!--<li class="active"><a href="#">Link</a></li> -->
    </ul>
};

declare function local:status-to-icon($status as xs:string) {
    switch ($status)
        case "conflict" return "icon-exchange"
        case "changed" return "icon-edit"
        case "unchanged" return "icon-ok"
        case "removed" return "icon-minus"
        case "untracked" return "icon-question-sign"
        case "added" return "icon-plus"
        case "missing" return "icon-check-empty"
        case "modified" return "icon-pencil"
        case "ignored" return "icon-eye-close"
        default return ""
};

declare function local:name($path as xs:string, $full-path as xs:string) {
    if (string-length($path) > 0) then
        substring-after($full-path, $path || "/")
    else
        $full-path
};

(:~
 : @param $node the HTML node with the class attribute which triggered this call
 : @param $model a map containing arbitrary data - used to pass information between template calls
 :)
declare function app:browser($node as node(), $model as map(*)) {
    <div class="row-fluid" id="main-area">
        (: app:repository-view("") :)
    </div>
};

declare %restxq:path("eXgit/repository/tools")
        %restxq:GET
        function app:repository-tools() {

    <div class="btn-group"><!-- btn-toolbar -->
        <a class="btn btn-small" href="#" id="repo-push"><i class="icon-upload"></i> Push</a>
        <div id="repo-push-dialog" title="Push to Upstream">
            <div>Push</div>
            <fieldset>
                <legend>Authentication</legend>
                
                <div class="control-group">
                    <label class="control-label" for="repo-push-user">User:</label>
                    <div class="controls">
                        <input class="input-xlarge" id="repo-push-username" name="repo-username" type="text" required="required"/>
                    </div>
                </div>
                
                <div class="control-group">
                    <label class="control-label" for="repo-push-password">Password:</label>
                    <div class="controls">
                        <input class="input-xlarge" id="repo-push-password" name="repo-push-password" type="password" required="required"/>
                    </div>
                </div>
            </fieldset>
        </div>
        <script>
            $(function() {{
                $( "#repo-push-dialog" ).dialog({{
                    autoOpen: false,
                    height: 600,
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttons: {{
                        "Push": function() {{
                            eXgit.push(
                                $( "#repo-push-username" ).val(), 
                                $( "#repo-push-password" ).val()
                            );
                        }},
                        Cancel: function() {{
                            $( this ).dialog( "close" );
                        }}
                    }}
                }});
            }});

            $( "#repo-push" )
                .button()
                .click(function() {{
                    $( "#repo-push-dialog" ).dialog( "open" );
                    
            }});
        </script>
        
        <a class="btn btn-small" href="#" id="repo-pull"><i class="icon-download"></i> Pull</a>
        <div id="repo-pull-dialog" title="Pull from Upstream">
            <div>Pull</div>
            <fieldset>
                <legend>Authentication</legend>
                
                <div class="control-group">
                    <label class="control-label" for="repo-pull-user">User:</label>
                    <div class="controls">
                        <input class="input-xlarge" id="repo-pull-username" name="repo-username" type="text" required="required"/>
                    </div>
                </div>
                
                <div class="control-group">
                    <label class="control-label" for="repo-pull-password">Password:</label>
                    <div class="controls">
                        <input class="input-xlarge" id="repo-pull-password" name="repo-pull-password" type="password" required="required"/>
                    </div>
                </div>
            </fieldset>
        </div>
        <script>
            $(function() {{
                $( "#repo-pull-dialog" ).dialog({{
                    autoOpen: false,
                    height: 600,
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttons: {{
                        "Pull": function() {{
                            eXgit.pull(
                                $( "#repo-pull-username" ).val(), 
                                $( "#repo-pull-password" ).val()
                            );
                        }},
                        Cancel: function() {{
                            $( this ).dialog( "close" );
                        }}
                    }}
                }});
            }});

            $( "#repo-pull" )
                .button()
                .click(function() {{
                    $( "#repo-pull-dialog" ).dialog( "open" );
                    
            }});
        </script>
        {app:commit-form()}
        {app:add-form()}
        {app:reset-buttons()}
    </div>
};

declare function local:lastAfter($str, $sep) {
    let $tns := fn:tokenize($str,$sep)
    return
    if ($tns[last()]) then
        $tns[last()]
    else
        $tns[last()-1]
};

declare %restxq:path("eXgit/repository/view")
        %restxq:query-param("collection", "{$col}", "")
        %restxq:query-param("path", "{$path}", "")
        %restxq:GET
        function app:repository-view($col as xs:string*, $path as xs:string*) {
    
    <div>        
        {app:repository-tools()}
        <div>
            {
                (<a href="#" onClick="eXgit.viewRepository('{$col}', '')">{local:lastAfter($col, "/")}</a>, " / ")
            }
            {
                let $tns := fn:tokenize($path,"/")
                return
                for $tn at $pos in $tns return
                    (<a href="#" onClick="eXgit.viewRepository('{$col}', '{string-join($tns[position() <= $pos], "/")}')">{$tn}</a>, " / ")
            }
        </div>
        <div id="submain-area" class="">
            <table class="table table-hover" id="repository-view">
                <!-- <caption>...</caption> -->
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Ago</th>
                        <th>Comment</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        let $statuses := git:status($col, $path, false())
                        return
                      (
                            for $collection in $statuses/git:collection return
                                let $full-path := $collection/@git:path/xs:string(.)
                                return
                                <tr gitPath="{$full-path}">
                                    <td>
                                        <i class="{local:status-to-icon($collection/@git:status/xs:string(.))}"></i> 
                                        <i class="icon-folder-close-alt"></i> 
                                        {local:name($path, $full-path)}
                                    </td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            ,
                            for $resource in $statuses/git:resource return
                                let $full-path := $resource/@git:path/xs:string(.)
                                return
                                <tr gitPath="{$full-path}" gitType="resource">
                                    <td>
                                        <i class="{local:status-to-icon($resource/@git:status/xs:string(.))}"></i> 
                                        <i class="icon-file"></i> 
                                        {local:name($path, $full-path)}
                                    </td>
                                    <td></td>
                                    <td></td>
                                </tr>
                        )
                    }
                </tbody>
            </table>
            <script>
                $(document).ready(function() {{
                    $('#repository-view tr').click(function() {{
                        var path = $(this).attr("gitPath");
                        if(path) {{
                            var gitType = $(this).attr("gitType");
                            if(gitType) {{
                                eXgit.viewResourceDiff('{$col}', path);
                            }} else {{
                                eXgit.viewRepository('{$col}', path);
                            }}
                        }}
                    }});
                }});
            </script>
        </div>
    </div>
};

declare function app:commit-form() {
    (
        <a class="btn btn-small" href="#" id="repo-commit"><i class="icon-picture"></i> Commit</a>
        ,
        <div id="repo-commit-dialog" title="Commit Changes to Git Repository">
            <form class="form-horizontal">
                <!-- <legend>Enter the location of the source repository</legend> -->
                <fieldset>
                    <!-- <legend>Commit message</legend> -->
                    
                    <label for="repo-commit-message">Commit message:</label>
                    <textarea class="input-block-level" rows="4" id="repo-commit-message" name="repo-commit-message" required="required" />
                    <!--
                    <div class="control-group">
                        <label class="control-label" for="repo-commit-author">Author:</label>
                        <div class="controls">
                            <input class="input-xlarge" id="repo-commit-author" name="repo-commit-author" type="text" required="required"/>
                        </div>
                    </div>
                    
                    <div class="control-group">
                        <label class="control-label" for="repo-commit-committer">Committer:</label>
                        <div class="controls">
                            <input class="input-xlarge" id="repo-commit-committer" name="repo-commit-committer" type="text" required="required"/>
                        </div>
                    </div>
                    -->
                    <table class="table table-hover" id="repo-commit-files">
                    <!-- app:commit-files($col, $path) -->
                    </table>
                </fieldset>
            </form>
        </div>
        ,
        <script>
            $(function() {{
                $( "#repo-commit-dialog" ).dialog({{
                    autoOpen: false,
                    height: 600,
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttons: {{
                        "Commit": function() {{
                            var files = [];
                            $("input:checked").each(function() {{ files.push( $(this).attr("gitPath") ) }} ); 
                            
                            eXgit.commit($( "#repo-commit-message" ).val(), files);
                        }},
                        Cancel: function() {{
                            $( "#repo-commit-files" ).empty();
                            $( this ).dialog( "close" );
                        }}
                    }}
                }});
            }});

             $( "#repo-commit" )
                .button()
                .click(function() {{
                    eXgit.commitFilesView();
                }});
        </script>
    )
};

declare %restxq:path("eXgit/commit/files")
        %restxq:query-param("collection", "{$col}", "")
        %restxq:GET
        function app:commit-files($col as xs:string*) {
    (
        <caption>
            Files (?/?) <bt/>
            <a class="btn btn-small" href="#" id="repo-commit-select-all"><i class="icon-plus-sign"></i> Select all</a>
            <a class="btn btn-small" href="#" id="repo-commit-deselect-all"><i class="icon-minus-sign"></i> Deselect all</a>
        </caption>
        ,
        <thead>
            <tr width="100%">
                <th width="10%"></th>
                <th width="10%">Status</th>
                <th width="80%">Path</th>
            </tr>
        </thead>
        ,
        let $statuses := git:status($col, "", true())
        return
        for $resource in $statuses/git:resource return
            let $full-path := $resource/@git:path/xs:string(.)
            return
            <tr gitPath="{$full-path}">
                <td><input gitPath="{$full-path}" type="checkbox" checked="checked"/></td>
                <td>
                    <i class="{local:status-to-icon($resource/@git:status/xs:string(.))}"></i> 
                    <i class="icon-file"></i> 
                </td>
                <td>
                    {$full-path}
                </td>
            </tr>
        ,
        <script>
            $(document).ready(function() {{
                $('#repo-commit-files tr').click(function(e) {{
                    if (e.target.tagName != "INPUT") {{
                        var checkbox = $(this).children("td").children("input");
                        checkbox.prop('checked', !checkbox.prop('checked'));
                    }};
                }});
            }});
             $( "#repo-commit-select-all" )
                .button()
                .click(function() {{
                    $('#repo-commit-files input:checkbox:not(:checked)').each(function(index) {{
                        $(this).prop('checked', true);
                    }});
                }});
             $( "#repo-commit-deselect-all" )
                .button()
                .click(function() {{
                    $('#repo-commit-files input:checkbox:checked').each(function(index) {{
                        $(this).prop('checked', false);
                    }});
                }});
        </script>
    )
};

declare function app:add-form() {
    (
        <a class="btn btn-small" href="#" id="repo-add"><i class="icon-plus-sign-alt"></i> Add</a>
        ,
        <div id="repo-add-dialog" title="Add to Git Repository">
            <form>
                <fieldset>
                    <table class="table table-hover" id="repo-add-files">
                    <!-- app:add-files($col, $path) -->
                    </table>
                </fieldset>
            </form>
        </div>
        ,
        <script>
            $(function() {{
                $( "#repo-add-dialog" ).dialog({{
                    autoOpen: false,
                    height: 600,
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttons: {{
                        "Add": function() {{
                            var files = [];
                            $("input:checked").each(function() {{ files.push( $(this).attr("gitPath") ) }} ); 
                            alert(files);
                            eXgit.add(files);
                        }},
                        Cancel: function() {{
                            $( "#repo-add-files" ).empty();
                            $( this ).dialog( "close" );
                        }}
                    }}
                }});
            }});

             $( "#repo-add" )
                .button()
                .click(function() {{
                    eXgit.addFilesView();
                }});
        </script>
    )
};

declare %restxq:path("eXgit/add/files")
        %restxq:query-param("collection", "{$col}", "")
        %restxq:GET
        function app:add-files($col as xs:string*) {
    (
        <caption>
            Files (?/?) <br/>
            <a class="btn btn-small" href="#" id="repo-add-select-all"><i class="icon-plus-sign"></i> Select all</a>
            <a class="btn btn-small" href="#" id="repo-add-deselect-all"><i class="icon-minus-sign"></i> Deselect all</a>
        </caption>
        ,
        <thead>
            <tr width="100%">
                <th width="10%"></th>
                <th width="10%">Status</th>
                <th width="80%">Path</th>
            </tr>
        </thead>
        ,
        let $statuses := git:status($col, "", true())
        return
        for $resource in $statuses/git:resource[@git:status eq "untracked"] return
            let $full-path := $resource/@git:path/xs:string(.)
            return
            <tr gitPath="{$full-path}">
                <td><input gitPath="{$full-path}" type="checkbox" checked="checked"/></td>
                <td>
                    <i class="{local:status-to-icon($resource/@git:status/xs:string(.))}"></i> 
                    <i class="icon-file"></i> 
                </td>
                <td>
                    {$full-path}
                </td>
            </tr>
        ,
        <script>
            $(document).ready(function() {{
                $('#repo-add-files tr').click(function(e) {{
                    if (e.target.tagName != "INPUT") {{
                        var checkbox = $(this).children("td").children("input");
                        checkbox.prop('checked', !checkbox.prop('checked'));
                    }}
                }});
            }});
             $( "#repo-add-select-all" )
                .button()
                .click(function() {{
                    $('#repo-add-files input:checkbox:not(:checked)').each(function(index) {{
                        $(this).prop('checked', true);
                    }});
                }});
             $( "#repo-add-deselect-all" )
                .button()
                .click(function() {{
                    $('#repo-add-files input:checkbox:checked').each(function(index) {{
                        $(this).prop('checked', false);
                    }});
                }});
        </script>
    )
};

declare function app:reset-buttons() {
    <div class="btn-group">
        <a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="#" id="repo-reset">
            <i class="icon-picture"></i> Reset
            <span class="caret"></span>
        </a>
        <ul class="dropdown-menu">
            <li>
                <a id="repo-reset-soft" onClick="eXgit.reset('SOFT')">Soft</a>
            </li>
            <li>
                <a id="repo-reset-mixed" onClick="eXgit.reset('MIXED')">Mixed</a>
            </li>
            <li>
                <a id="repo-reset-hard" onClick="eXgit.reset('HARD')">Hard</a>
            </li>
            <!--
            <li>
                <a id="repo-reset-keep" onClick="eXgit.reset('KEEP')">Keep</a>
            </li>
            <li>
                <a id="repo-reset-merge" onClick="eXgit.reset('MERGE')">Merge</a>
            </li>
            -->
        </ul>
    </div>
};

declare %restxq:path("eXgit/diff/view")
        %restxq:query-param("collection", "{$col}", "")
        %restxq:query-param("path", "{$path}", "")
        %restxq:GET
        function app:add-files($col as xs:string*, $path as xs:string*) {
    <div style="width:850px;">
        <div id="compare"></div>
        <script>
            $(document).ready(function () {{
                $('#compare').mergely({{
            		cmsettings: {{ readOnly: true, lineNumbers: true }},
                    lhs: function(setValue) {{
                        $.ajax({{
                            url: "cat",
                            data: {{ "collection" : "{$col}", "path" : "{$path}" }},
                            dataType: 'text',
                            success: function (data, status, xhr) {{
                        		setValue(data);
                            }},
                            error: function (xhr, textStatus, thrownError) {{
                                alert("error: cant get cat:\n\n"+thrownError);
                            }}
                        }});
            		}},
            		rhs: function(setValue) {{
                        $.ajax({{
                            url: "cat",
                            data: {{ "collection" : "{$col}", "path" : "{$path}", "direct" : "yes" }},
                            dataType: 'text',
                            success: function (data, status, xhr) {{
                            	setValue(data);
                            }},
                            error: function (xhr, textStatus, thrownError) {{
                                alert("error: cant get cat:\n\n"+thrownError);
                            }}
                        }});
            		}}
            	}});
            }});            
        </script>
    </div>
};
