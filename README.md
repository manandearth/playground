# playground

For personal experimentation.

## Development Cider 
Jack-in a repl `C-c M-j` 

load the dev namespace `C-c C-k`

in repl:

```
(do
 (clojure.core/require 'clojure.tools.namespace.repl)
  (clojure.core/require 'com.stuartsierra.component.repl)
  ((clojure.core/resolve 'clojure.tools.namespace.repl/set-refresh-dirs) "src" "test")
  (try
    ((clojure.core/resolve 'com.stuartsierra.component.repl/reset))
    (catch java.lang.Throwable v
      (clojure.core/when (clojure.core/instance? java.io.FileNotFoundException v)
        ((clojure.core/resolve 'clojure.tools.namespace.repl/clear)))
      (clojure.core/when ((clojure.core/resolve 'com.stuartsierra.component/ex-component?) v)
        (clojure.core/let [stop (clojure.core/resolve 'com.stuartsierra.component/stop)]
          (clojure.core/some-> v clojure.core/ex-data :system stop)))
      (throw v))))

```
This is the reset and must be used after each change.

It requires to have the following namespaces required:
```
[com.stuartsierra/component.repl "0.2.0"]
[org.clojure/tools.namespace "0.2.11"]
[org.clojure/tools.nrepl "0.2.13" :exclusions [org.clojure/clojure]]
```

## DB

Playground is using `[juxt.modular/postgres "0.0.1-SNAPSHOT"]` which works on PostgreSQL upto verion 9.6 and **not** later..

### Migration

The original creation of the tables and migrations are managed with [joplin](https://github.com/juxt/joplin) 

To first set up (for example a dev postgres environment) run the following in a shell:
`lein reset dev psql-dev`

For the tests create a parallel db then the tables using:
`lein reset test psql-test` 

The db connection url is configured in: `resources/jopin.edn`

## TESTS

### etaoin

`etaoin` tests uses `user/test-system`.

Create a parallel db with same tables (i.e users, register).

populate the register table with:

| username | password | role  |
|----------+----------+-------|
| admin    | admin    | admin |
| user     | user     | user  |




