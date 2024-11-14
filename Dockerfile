FROM node:alpine AS deps
LABEL authors="CosmasLang"

WORKDIR /build

FROM deps AS package
COPY . /build

FROM package AS install
RUN npm config set strict-ssl false
#ci ist schneller als install, produziert aber größeres Image und geht nur lokal statt global
RUN npm ci @angular/cli --force #--loglevel verbose
#RUN npm install -g @angular/cli --force #--loglevel verbose
RUN npm ci --loglevel verbose

FROM install AS build
#RUN npm run build-prod
#RUN ng build --configuration production
CMD ["npm", "run", "build"]

# Production-Stage / Verwende NGINX als Basis-Image für die Produktionsumgebung
#FROM nginx:alpine AS deploy
# Kopiere die gebaute App in das NGINX-Verzeichnis
#COPY --from=build /build/dist/musikserverclient /usr/share/nginx/html
# Kopiere deine angepasste NGINX-Konfiguration (optional, aber empfohlen. Eine Beispiel Konfiguration findest du weiter unten)
#COPY nginx.conf /etc/nginx/nginx.conf
# Port 80 freigeben, nginx läuft auf diesem Port
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]

