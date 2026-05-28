#!/bin/bash

echo "DB_URL=jdbc:postgresql://localhost:5432/oficina" > .env
echo "DB_USERNAME=user" >> .env
echo "DB_PASSWORD=password" >> .env

echo "KEYCLOAK_URL=http://sso.postech.com.br" >> .env
echo "KEYCLOAK_REALM=Fiap" >> .env
echo "KEYCLOAK_CLIENT_ID=oficina" >> .env
