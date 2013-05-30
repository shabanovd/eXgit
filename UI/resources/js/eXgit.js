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
                    eXgit.hideLoading();
                },
                error: function (xhr, textStatus, thrownError) {
                    alert("error: cant get context-path:\n\n"+thrownError);
                    eXgit.hideLoading();
                }
            });
        },
        createRepository: function(collection, data) {
            $( "#repo-create-dialog" ).dialog( "close" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repository/create",
                data: { "collection" : collection, "data" : data},
                dataType: 'text',
                success: function (data, status, xhr) {                         
                    eXgit.hideLoading();
                    $( "#repo-create-collection" ).val("/db");
                    eXgit.viewRepositories();
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant create repository:\n\n"+thrownError);
                }
            });
        },
        cloneRepository: function(uri, collection, username, password, data) {
            $( "#repo-clone-dialog" ).dialog( "close" );
            eXgit.showLoading();
            // avoid '/' at the end of collection path
            if (collection.substr(collection.length - 1) == "/") {
                collection = collection.substr(0, collection.length - 1);
            }
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repository/clone",
                data: { "uri" : uri, "collection" : collection, "username" : username, "password" : password, "data" : data},
                dataType: 'text',
                success: function (data, status, xhr) {                        
                    eXgit.hideLoading();
                    $( "#repo-clone-collection" ).val("/db");
                    eXgit.viewRepositories();
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant clone repository:\n\n"+thrownError);
                }
            });
        },
        commitFilesView: function() {
            $( "#repo-commit-dialog" ).dialog( "open" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/commit/files",
                data: { 'collection' : eXgit.currentRepository},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    $( "#repo-commit-files" ).empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant get status for commit:\n\n"+thrownError);
                }
            });
        },
        commitAll: function(message) {
            $( "#repo-commit-dialog" ).dialog( "close" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/commitAll",
                data: { 'collection' : eXgit.currentRepository, 'message' : message},
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    eXgit.viewRepository(eXgit.currentRepository, '');
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant commit:\n\n"+thrownError);
                }
            });
        },
        commit: function(message, paths) {
            $( "#repo-commit-dialog" ).dialog( "close" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/commit",
                data: { 'collection' : eXgit.currentRepository, 'message' : message, 'files' : paths},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    eXgit.viewRepository(eXgit.currentRepository, '');
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant commit:\n\n"+thrownError);
                }
            });
        },
        addFilesView: function() {
            $( "#repo-add-dialog" ).dialog( "open" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/add/files",
                data: { 'collection' : eXgit.currentRepository},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    $( "#repo-add-files" ).empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant get statutes for add:\n\n"+thrownError);
                }
            });
        },
        add: function(files) {
            $( "#repo-add-dialog" ).dialog( "close" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/add",
                data: { 'collection' : eXgit.currentRepository, 'files' : files},
                traditional: 'true',
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    eXgit.viewRepository(eXgit.currentRepository, '');
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant add:\n\n"+thrownError);
                }
            });
        },
        viewRepositories: function() {
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repositories/view",
                data: { "collection" : eXgit.currentRepository},
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    $("#repos-view").empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant load repositories list:\n\n"+thrownError);
                }
            });
        },
        viewRepository: function(repository, path) {
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/repository/view",
                data: { "collection" : repository, "path" : path},
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    eXgit.currentRepository = repository;
                    $("#main-area").empty().append(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant view repository:\n\n"+thrownError);
                }
            });
        },
        viewResourceDiff: function(repository, path) {
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/diff/view",
                data: { "collection" : repository, "path" : path},
                dataType: 'text',
                success: function(data) {
                    $("#submain-area").empty().append(data);
                    eXgit.hideLoading();
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant view repository:\n\n"+thrownError);
                }
            });
        },
        push: function(username, password) {
            $( "#repo-push-dialog" ).dialog( "close" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/push",
                data: { "collection" : eXgit.currentRepository, "username" : username, "password" : password},
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    alert(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant push repository:\n\n"+thrownError);
                }
            });
        },
        pull: function(username, password) {
            $( "#repo-pull-dialog" ).dialog( "close" );
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/pull",
                data: { "collection" : eXgit.currentRepository, "username" : username, "password" : password},
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    alert(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant pull repository:\n\n"+thrownError);
                }
            });
        },
        
        reset: function(resetType) {
            eXgit.showLoading();
            $.ajax({
                url: ""+contextPath+"/restxq/eXgit/reset",
                data: { "collection" : eXgit.currentRepository, "type" : resetType},
                dataType: 'text',
                success: function(data) {
                    eXgit.hideLoading();
                    alert(data);
                },
                error: function (xhr, textStatus, thrownError) {
                    eXgit.hideLoading();
                    alert("error: cant reset repository:\n\n"+thrownError);
                }
            });
        },

showLoading: function() {
            //$('#loading').style.display = "block";
            $('#loading').show();
        },
        hideLoading: function() {
            //$('#loading').style.display = "none";
            $('#loading').hide();
        },
    };
}());

window.onload = function() { eXgit.setup(); };