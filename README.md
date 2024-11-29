# Browser for Music File Data

Consists of
* server ([musicdataserver](musicdataserver/README.md))
* and client ([musicdataclient](musicdataclient/README.md))
subprojects which can be used together or singularly.

[musicdataserver](musicdataserver/README.md) is a Java Spring Boot server which uses Hibernate/JPA to access a bundled
PostgreSQL database.
At startup a configurable directory tree is scanned for music files
with tagged data: FLAC, mp3, ogg and so on.

[musicdataclient](musicdataclient/README.md) is an Angular web app which retrieves data from the server
in different categories and supports search for composer, artist, etc.

For details see subproject READMEs.
