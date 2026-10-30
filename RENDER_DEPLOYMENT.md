# Deploy EOHA Backend to Render

This is a Java 21 / Spring Boot API using PostgreSQL on AWS RDS. It does not create, migrate, or seed tables. AWS CLI and AWS SSO are not required in the container: the application uses PostgreSQL credentials.

## 1. Prepare GitHub

Merge this deployment PR into the branch you will deploy. The repository root must contain Dockerfile, .dockerignore, render.yaml, pom.xml, mvnw, .mvn, and src.

The multi-stage Dockerfile uses the pinned Maven wrapper, runs tests with `verify`, and copies only the executable JAR into the Java 21 runtime. It runs as an unprivileged user. Local secrets, Git history, target, and node_modules are excluded from the build context. Heap is capped at 65% of container RAM, leaving space for native memory.

## 2. Create the Render service

In the [Render dashboard](https://dashboard.render.com/), choose **New > Blueprint**, connect **MEDICONSORTIUM/eoha_backend**, select your deployment branch, and use the root `render.yaml`. Review the compute plan and region before creating resources. Choose a region near RDS and memory appropriate for a JVM application.

Enter these values when prompted:

| Variable | Value |
| --- | --- |
| DB_URL | `jdbc:postgresql://YOUR_RDS_ENDPOINT:5432/eoha?sslmode=require` |
| DB_USERNAME | PostgreSQL database user |
| DB_PASSWORD | PostgreSQL password, entered only in Render |
| DB_POOL_SIZE | Blueprint defaults to 5 |

Replace the hostname, port, and database name with your actual RDS settings. DB_URL must begin with `jdbc:postgresql://`, not `postgres://`. Do not use localhost. Use a database user with CONNECT on the database, USAGE on schema eoha, and SELECT on the API tables. Allow for the combined pool size across all replicas.

Alternatively choose **New > Web Service** and connect the repository and branch. Set Language to **Docker**, Root Directory to the repository root (leave blank), Dockerfile Path to `./Dockerfile`, Docker Context to `.`, and Health Check Path to `/api/health`. Add the same environment variables. Leave Docker Command unset; no separate build or start command is needed.

Render supplies PORT automatically. Spring already reads PORT, and the Docker image sets SERVER_ADDRESS to 0.0.0.0. Do not commit credentials or upload .env files as source code.

## 3. Connect Render to AWS RDS

In the Render service, open **Connect > Outbound** and copy every listed CIDR range. In AWS, open the RDS database's attached VPC security group and add inbound PostgreSQL TCP rules on the actual database port for those ranges. Do not use 0.0.0.0/0. Regional ranges are shared with other Render services; dedicated outbound IPs allow a narrower allowlist.

A direct public-internet connection requires publicly accessible RDS plus subnet routing and network ACLs that permit the connection. If RDS is private-only, arrange private connectivity separately with your AWS administrator. AWS SSO does not create a network route. Do not change public accessibility blindly.

Keep TLS enabled. `sslmode=require` encrypts traffic. For server identity verification, configure `verify-full` with the trusted AWS RDS CA certificate available inside the image.

## 4. Deploy and check the API

Create the service and follow the build/runtime logs. Maven verification must pass and Spring Boot must start. If networking was updated after the initial deploy, retry the database check or redeploy.

Replace YOUR-SERVICE with the assigned Render service hostname:

```powershell
Invoke-RestMethod https://YOUR-SERVICE.onrender.com/api/health
Invoke-RestMethod https://YOUR-SERVICE.onrender.com/api/health/database
Invoke-RestMethod 'https://YOUR-SERVICE.onrender.com/api/data-sources?limit=1'
```

The database check must return HTTP 200 and `ready=true`. HTTP 503 means database connectivity, permissions, or schema still needs attention. It checks schema eoha for regions, environmental_observations, environmental_variables, data_sources, and etl_runs.

Render's `/api/health` check measures liveness so a database outage does not repeatedly restart the JVM. A successful Render deploy alone does not confirm database access; run the database check yourself.

## 5. Connect the frontend

Set the frontend API base URL to `https://YOUR-SERVICE.onrender.com`, then rebuild/redeploy it. This backend currently has no explicit CORS policy or authentication. For a browser frontend on another origin, configure CORS to allow that frontend origin or use a same-origin proxy. Confirm that the read endpoints and schema metadata are intended to be public before routing production traffic here.

## Troubleshooting

- Build fails: inspect the first Maven error; the image provides Java 21 and downloads dependencies during build.
- No open port: remove Docker Command overrides and conflicting SERVER_PORT/SERVER_ADDRESS settings. The JVM must start and use Render's PORT.
- Database timeout / 503: verify every outbound CIDR, RDS endpoint/port, public accessibility or private routing, network ACLs, and permissions.
- Authentication failure: correct DB_USERNAME and DB_PASSWORD in Render.
- Missing tables: verify the database name and schema eoha. This application never creates tables.
- Container is killed: inspect JVM logs and memory metrics; increase instance memory as needed.
- API works in a terminal but fails in a browser: check the frontend URL, HTTPS, and CORS.

## Optional local Docker verification

On a machine with Docker, from the repository root:

```sh
docker build -t eoha-backend .
docker run --rm -p 10000:10000 --env-file .env.render.local eoha-backend
```

Create a Git-ignored `.env.render.local` containing DB_URL, DB_USERNAME, and DB_PASSWORD in plain `KEY=value` syntax. Docker env files do not support shell exports or Spring properties syntax. The database must allow your local machine's network address. Check `http://localhost:10000/api/health` and `/api/health/database`.

## References

- [Docker on Render](https://render.com/docs/docker)
- [Blueprint specification](https://render.com/docs/blueprint-spec)
- [Outbound IP addresses](https://render.com/docs/outbound-ip-addresses)
- [Health checks](https://render.com/docs/health-checks)
