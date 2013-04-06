
eXgit = (function() {
    var currentRepository = "";
    
    return {
        createRepository: function(collection, data) {
            $.ajax({
                url: "/restxq/eXgit/repository/create",
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
            $.ajax({
                url: "/restxq/eXgit/repository/clone",
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
        commitAll: function(message) {
            $.ajax({
                url: "/restxq/eXgit/commitAll",
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
                url: "/restxq/eXgit/commit",
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
        add: function(files) {
            $.ajax({
                url: "/restxq/eXgit/add",
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
                url: "/restxq/eXgit/repositories/view",
                data: { "collection" : currentRepository},
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
                url: "/restxq/eXgit/repository/view",
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
                url: "/restxq/eXgit/diff/view",
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
    };
}());