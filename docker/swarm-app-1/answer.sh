# Creating Docker swarm
docker swarm init --advertise-addr 172.31.89.22
docker swarm join --token SWMTKN-1-2fq9u1r5omam8dgd0z2iecvfs4no0uhunu83pe9i3vby3gwiql-59iltylsybh59z20lqodxbhah 172.31.89.22:2377

# Creating network
docker network create --driver overlay backend
docker network create --driver overlay frontend

# Creating services
# Backend
docker service create --name db --network backend --mount type=volume,source=db-data,target=/var/lib/postgresql/data -e POSTGRES_PASSWORD=mypassword postgres:9.4
docker service create --name result --network backend -p 5001:80 dockersamples/examplevotingapp_result:before

#Worker
docker service create --name worker --network frontend --network backend dockersamples/examplevotingapp_worker
docker service update --network-add frontend worker

# Frontend
docker service create --name redis --network frontend redis:3.2
docker service create --name vote --network frontend -p 80:80 --replicas 2 dockersamples/examplevotingapp_vote:before
