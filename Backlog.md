# Backlog

## now
* introduce all examples from (https://github.com/maxmind/GeoIP2-java) that work for my 3 lite databses 
* actually expose static/* files in the Docker-deployed version 

## next
* reverse proxy
* IP resolved with geodata like https://ifconfig.co/json 
* replace “response.readEntity(javaClass<String>())” with
    asyncResponse.resume(Response.fromResponse(response).build()) for GET resp.
    asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build()) for DELETE
* usage ImageJ? to create a series of iOS App Store images http://rsbweb.nih.gov/ij/
* Mirror return via GET what was POSTED or provided via GET
## dream / future
* Voting/Polling/Ballot service
    Planning poker service
* Marathon ranking service
* Something to check the "Reisepass readiness" status
* getVoucher (UUID), getVisitCount


