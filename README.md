
eXgit
=====

Module for interacting with the git repository.

Building & Installing
---------------------

Prerequisites:

- eXist-db >= 2.1
- Maven > 2
- Ant > 1.8.0


Compile and package with Maven using the command:

	mvn clean package
	
The jar files will have been created in /target.

Create an expath package with ant:

	ant

The package will have been created in /build.

Install the package into eXist-db.


functions
---------

```xquery
git:create($localPath as xs:string) as xs:boolean
```

$localPath as xs:string - Local path

```xquery
git:clone($remotePath as xs:string, $localPath as xs:string, $username as xs:string, $password as xs:string) as xs:boolean
```

$remotePath as xs:string - Remote path
$localPath as xs:string - Local path
$username as xs:string - Username
$password as xs:string - Password

```xquery
git:add($localPath as xs:string, $filepattern as xs:string+) as xs:boolean
```

$localPath as xs:string - Local path
$filepattern as xs:string+ - File pattern

```xquery
git:commit($localPath as xs:string, $message as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$message as xs:string - Message

```xquery
git:commit($localPath as xs:string, $message as xs:string, $files as xs:string*) as xs:boolean
```

$localPath as xs:string - Local path
$message as xs:string - Message
$files as xs:string* - Files to commit

```xquery
git:tag($localPath as xs:string, $tagName as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$tagName as xs:string - Tag name

```xquery
git:checkout($localPath as xs:string, $branch-name as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$branch-name as xs:string - The name of the branch

```xquery
git:merge($localPath as xs:string, $branch-name as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$branch-name as xs:string - The name of the branch

```xquery
git:diff($localPath as xs:string) as xs:boolean
```

$localPath as xs:string - Local path

```xquery
git:push($localPath as xs:string, $username as xs:string, $password as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$username as xs:string - Username
$password as xs:string - Password

```xquery
git:pull($localPath as xs:string, $username as xs:string, $password as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$username as xs:string - Username
$password as xs:string - Password

```xquery
git:branch-create($localPath as xs:string, $branch-name as xs:string, $start-point as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$branch-name as xs:string - The name of the branch
$start-point as xs:string - 

```xquery
git:branch-create($localPath as xs:string, $branch-name as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$branch-name as xs:string - The name of the branch

```xquery
git:branch-delete($localPath as xs:string, $branch-name as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$branch-name as xs:string - The name of the branch

```xquery
git:branch-list($localPath as xs:string) as xs:string?
```

$localPath as xs:string - Local path

```xquery
git:branch-rename($localPath as xs:string, $old-branch-name as xs:string, $new-branch-name as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$old-branch-name as xs:string - The old name of the branch
$new-branch-name as xs:string - The new name of the branch

```xquery
git:log($localPath as xs:string) as node()*
```

$localPath as xs:string - Local path

```xquery
git:reset($localPath as xs:string, $type as xs:string) as xs:boolean
```

$localPath as xs:string - Local path
$type as xs:string - 

```xquery
git:status($localPath as xs:string, $gitPath as xs:string, $recursive as xs:boolean) as xs:string*
```

$localPath as xs:string - Local path
$gitPath as xs:string - Repository path
$recursive as xs:boolean - Recursive

```xquery
git:status-untracked($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-added($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-changed($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-conflicting($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-ignored($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-missing($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-modified($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-removed($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:status-untracked-folders($localPath as xs:string) as xs:string*
```

$localPath as xs:string - Local path

```xquery
git:cat($localPath as xs:string, $Path as xs:string) as xs:base64Binary
```

$localPath as xs:string - Local path
$Path as xs:string - File path

```xquery
git:cat-working-copy($localPath as xs:string, $Path as xs:string) as xs:base64Binary
```

$localPath as xs:string - Local path
$Path as xs:string - File path
