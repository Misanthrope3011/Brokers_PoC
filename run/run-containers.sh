cd ../docker
containers=("pulsar" "rabbit" "kafka" "postgres")
docker network inspect brokers_app_network || docker network create brokers_app_network
for service in $containers; do
  compose_location=$(pwd)/${service}
  echo ${compose_location}/docker-compose.yml
    if [ -f "${compose_location}/docker-compose.yml" ]; then
        echo "Creating service: ${service}"
        cd ${compose_location} && docker-compose up -d
        cd -
    fi
done