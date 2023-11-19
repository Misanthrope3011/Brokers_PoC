cd ../docker
containers=("pulsar" "rabbit" "kafka" "postgres")
docker network inspect brokers_app_network || docker network rm brokers_app_network
for service in "${containers[@]}"; do
  compose_location=$(pwd)/${service}
    if [ -f "${compose_location}/docker-compose.yml" ]; then
        echo "Dropping service: ${service}"
        cd ${compose_location} && docker-compose down
        cd -
    fi
done