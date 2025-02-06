FROM postgres:latest

USER root

RUN  apt-get update && apt-get install -y postgis

ENV POSTGRES_USER postgresUser
ENV POSTGRES_PASSWORD password
ENV POSTGRES_DB postgres

EXPOSE 5432