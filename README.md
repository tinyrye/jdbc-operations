# jdbc-operations will make something about as level of an abstraction as Spring JDBC template

SQL access made at the reuseable statement object level.  Statement objects take a data source so that each call with parameters obtains a connection and is thread-safe/independent of other threads/callers to a service.

