cd ../docker
docker network create brokers_app_network
for dir in */; do
    if [ -f "${dir}docker-compose.yml" ]; then
        echo "Creating service: ${dir%?}"
        cd "${dir}"
        docker-compose up -d
        cd -
    fi
done