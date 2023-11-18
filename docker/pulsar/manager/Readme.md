Useful commands:

### First time password set to pulsar manager

CSRF_TOKEN=$(curl http://localhost:7750/pulsar-manager/csrf-token)
curl \
-H 'X-XSRF-TOKEN: $CSRF_TOKEN' \
-H 'Cookie: XSRF-TOKEN=$CSRF_TOKEN;' \
-H "Content-Type: application/json" \
-X PUT http://localhost:7750/pulsar-manager/users/superuser \
-d '{"name": "admin", "password": "apachepulsar", "description": "test", "email": "username@test.org"}'


## Pulsar
# bin/pulsar-admin topic create public/default/string-topic
# bin/pulsar-admin tenants create apache
# bin/pulsar-admin schemas upload --filename sample.json public/default/string-topic2
# bin/pulsar-admin namespaces create apache/pulsar
# bin/pulsar-client produce apache/pulsar/test-topic  -m "---------hello apache pulsar-------" -n 10
# bin/pulsar-client produce public/default/string-topic -f sample.json -n 10