# Developing on CincoCloud

## Code Formatting

For CincoCloud, we adhere to the [google code styling][google-styleguide] conventions.
You should import one of the following files in your IDE to ensure your code is formatted properly:

* Eclipse: `.resources/eclipse-java-google-style.xml`
* Intellij: `.resources/eclipse-java-google-style.xml`

Since we lint our code in the CI/CD pipeline, please ensure that the code is formatted properly.

[google-styleguide]: https://github.com/google/styleguide

## Managing PostgreSQL

### Wipe the Database

To wipe the database, just stop Skaffold and then delete the corresponding
volume claim:

```
kubectl delete pvc/data-release-postgresql-0
```

The volume will be recreated on the next Skaffold startup.

### Database Access

It's possible to directly enter the PostgreSQL shell using this command:

```
kubectl exec release-postgresql-0 -it -- psql -U cc
```

To access the PostgreSQL database with a local database client, the port from
within the cluster must be forwarded to the localhost using kubectl:

```
kubectl port-forward release-postgresql-0 5432:5432
```

The database will be accessible under this address:

| Key      | Value     |
| -------- | --------- |
| Server   | localhost |
| Port     |      5432 |
| Database | cc        |
| Username | cc        |
| Password | cc        |

## Cleanup Skaffold Images

Skaffold builds a lot of Docker images that can pile up over time. Run

```
minikube ssh -- docker system prune
```

to clean them up.
