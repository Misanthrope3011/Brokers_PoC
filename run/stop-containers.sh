cd ../docker
for dir in */; do
    if [ -f "${dir}docker-compose.yml" ]; then
        echo "Dropping service: ${dir%?}"
        docker-compose down
        cd -
    fi
done