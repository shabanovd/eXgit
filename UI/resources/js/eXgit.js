eXgit = (function() {
    var currentRepository = "";
	var contextPath = "";
    
    return {
        setup: function(collection, data) {
            $.ajax({
                url: "$context-path",
                dataType: 'text',
                success: function (data, status, xhr) {  
                    contextPath = $(data).text();
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant get context-path:\n\n"+thrownError);
                }
            });
        },
        createRepository: function(collection, data) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repository/create",
                data: { "collection" : collection, "data" : data},
                dataType: 'text',
                success: function (data, status, xhr) {                         
                    $( "#repo-create-collection" ).val("/db");
                    $( "#repo-create-dialog" ).dialog( "close" );
                    eXgit.viewRepositories();
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant create repository:\n\n"+thrownError);
                }
            });
        },
        cloneRepository: function(uri, collection, username, password, data) {
            // avoid '/' at the end of collection path
            if (collection.substr(collection.length - 1) == "/") {
                collection = collection.substr(0, collection.length - 1);
            }
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repository/clone",
                data: { "uri" : uri, "collection" : collection, "username" : username, "password" : password, "data" : data},
                dataType: 'text',
                success: function (data, status, xhr) {                        
                    $( "#repo-clone-collection" ).val("/db");
                    $( "#repo-clone-dialog" ).dialog( "close" );
                    eXgit.viewRepositories();
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant clone repository:\n\n"+thrownError);
                }
            });
        },
        commitFilesView: function() {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/commit/files",
                data: { 'collection' : eXgit.currentRepository},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    $( "#repo-commit-files" ).empty().append(data);
                    $( "#repo-commit-dialog" ).dialog( "open" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant get status for commit:\n\n"+thrownError);
                }
            });
        },
        commitAll: function(message) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/commitAll",
                data: { 'collection' : eXgit.currentRepository, 'message' : message},
                dataType: 'text',
                success: function(data) {
                    eXgit.viewRepository(eXgit.currentRepository, '');
                    $( "#repo-commit-dialog" ).dialog( "close" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant commit:\n\n"+thrownError);
                }
            });
        },
        commit: function(message, paths) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/commit",
                data: { 'collection' : eXgit.currentRepository, 'message' : message, 'files' : paths},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    eXgit.viewRepository(eXgit.currentRepository, '');
                    $( "#repo-commit-dialog" ).dialog( "close" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant commit:\n\n"+thrownError);
                }
            });
        },
        addFilesView: function() {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/add/files",
                data: { 'collection' : eXgit.currentRepository},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    $( "#repo-add-files" ).empty().append(data);
                    $( "#repo-add-dialog" ).dialog( "open" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant get statutes for add:\n\n"+thrownError);
                }
            });
        },
        add: function(files) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/add",
                data: { 'collection' : eXgit.currentRepository, 'files' : files},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    eXgit.viewRepository(eXgit.currentRepository, '');
                    $( "#repo-add-dialog" ).dialog( "close" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant add:\n\n"+thrownError);
                }
            });
        },
        viewRepositories: function() {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repositories/view",
                data: { "collection" : eXgit.currentRepository},
                dataType: 'text',
                success: function(data) {
                    $("#repos-view").empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant load repositories list:\n\n"+thrownError);
                }
            });
        },
        viewRepository: function(repository, path) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repository/view",
                data: { "collection" : repository, "path" : path},
                dataType: 'text',
                success: function(data) {
                    eXgit.currentRepository = repository;
                    $("#main-area").empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant view repository:\n\n"+thrownError);
                }
            });
        },
        viewResourceDiff: function(repository, path) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/diff/view",
                data: { "collection" : repository, "path" : path},
                dataType: 'text',
                success: function(data) {
                    $("#submain-area").empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant view repository:\n\n"+thrownError);
                }
            });
        },
        push: function(username, password) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/push",
                data: { "collection" : eXgit.currentRepository, "username" : username, "password" : password},
                dataType: 'text',
                success: function(data) {
                    alert(data);
                    $( "#repo-push-dialog" ).dialog( "close" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant push repository:\n\n"+thrownError);
                }
            });
        },
        pull: function(username, password) {
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/pull",
                data: { "collection" : eXgit.currentRepository, "username" : username, "password" : password},
                dataType: 'text',
                success: function(data) {
                    alert(data);
                    $( "#repo-pull-dialog" ).dialog( "close" );
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant pull repository:\n\n"+thrownError);
                }
            });
        },
    };
}());

window.onload = function() { eXgit.setup(); };