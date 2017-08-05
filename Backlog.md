# Backlog

## now
* introduce all examples from (https://github.com/maxmind/GeoIP2-java) that work for my 3 lite databses 
* actually expose static/* files in the Docker-deployed version 

## next
* relicense later AGPLv3 for a dual-license option https://choosealicense.com/licenses/agpl-3.0/
* introduce parameter to fetch whois info for any specific IP
* /mirror endpoint to mirror a call to the requester setting method, query, param, and payload
* integrate in Blog as user info
* reverse proxy, e.g. for service-kit
* IP resolved with geodata like https://ifconfig.co/json 
* replace “response.readEntity(javaClass<String>())” with
    asyncResponse.resume(Response.fromResponse(response).build()) for GET resp.
    asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build()) for DELETE
* usage ImageJ? to create a series of iOS App Store images http://rsbweb.nih.gov/ij/
* Mirror return via GET what was POSTED or provided via GET
## dream / future
* statuspage.io provider, uptime monitoring
* Voting/Polling/Ballot service
    Planning poker service
* Marathon ranking service
* Something to check the "Reisepass readiness" status
* getVoucher (UUID), getVisitCount


