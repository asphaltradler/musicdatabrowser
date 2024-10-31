FROM node:slim AS deps
#LABEL authors="CosmasLang"
#WORKDIR /usr/src/app
WORKDIR /build
#COPY . /usr/src/app

FROM deps AS package
COPY . /build

FROM package AS install
RUN npm install -g @angular/cli --force --loglevel verbose
RUN npm install --force --loglevel verbose

#FROM install AS start
#CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]

# ENTRYPOINT ["top", "-b"]
