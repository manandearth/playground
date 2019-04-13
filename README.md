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

`[juxt.modular/postgres "0.0.1-SNAPSHOT"]` works on PostgreSQL upto verion 9.6 and **not** later..

For the service.invoices ns create a table:

`CREATE TABLE users (id SERIAL, email VARCHAR(50))`

and register table that keeps auth login data:

`CREATE TABLE register ( username VARCHAR(20), password VARCHAR(100), role VARCHAR(20) DEFAULT 'user');`

to alter role to admin:

`UPDATE register SET ROLE = 'admin' WHERE username = 'foo';`

