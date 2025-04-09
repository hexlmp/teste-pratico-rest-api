FROM openjdk:jre-alpine

EXPOSE 389

EXPOSE 10389

RUN apk update \                                                                                                                                                                                                                        
    && apk add ca-certificates \                                                                                                                                                                                                      
    && update-ca-certificates \
    && apk add --no-cache openssl \
    && mkdir /ldap \
    && keytool -validity 365 -genkey -alias ldap -keyalg EC -sigalg SHA384WithECDSA -keystore /tmp/ldaps.keystore  \
    -storepass 123456 -keypass 123456 -dname cn=ldap

ADD https://github.com/intoolswetrust/ldap-server/releases/download/v.1.0.0/ldap-server.jar /ldap/

COPY usuarios.ldif /ldap/users.ldif

WORKDIR /ldap

CMD ["java", "-Djavax.net.debug=all", "-jar","ldap-server.jar", "users.ldif", "-sp", "389", "-skf", "/tmp/ldaps.keystore", "-skp", "123456"]
