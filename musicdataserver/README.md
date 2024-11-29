## musicdataserver
is a Java Spring Boot server which uses Hibernate/JPA to access a bundled
PostgreSQL database.

At startup a configurable directory tree is scanned for music files
with tagged data: FLAC, mp3, ogg and so on.

REST API supports searches for artist, composer etc. with different mappings.
Apart from tracks and albums, artists, composers, genres and works are kept in their
own tables to build up a many-to-many relationship with tracks which uses them.
