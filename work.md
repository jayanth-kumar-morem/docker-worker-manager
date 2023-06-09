# Postgres Setup
1. Install postgres 15 on mac m1
   `brew install postgres@15`
2. Login to postgresql
   `psql -d postgres`
3. Create new user with name `openfabric`
   `create user openfabric`
4. Create new database with name `test`
   `create database test`
5. Grant all privileges to `test` db to `openfabric` user
   `grant all privileges on database test to openfabric`
6. Grant all privileges to public schema to openfabric
   `GRANT ALL PRIVILEGES ON SCHEMA public TO openfabric;`

# Start application
1. Started Application with IDEA conf
2. Test the application
   1. Open Postman and create a new request. 
   2. Set the HTTP method to POST.
   3. Enter the exisintg URL
      `http://localhost:8080/${node.api.path}/worker/hello` ~ `http://localhost:8080/api/v1/worker/hello`
   4. Select the "raw" option and choose the data type as "Text".
   5. In the request body, enter the name you want to send as a string. For example: "Jayanth".
   6. We should be able to see `Hello!jayanth` as response and `200 status OK`
3. Got to know the swagger-ui little late. (Thanks!)


