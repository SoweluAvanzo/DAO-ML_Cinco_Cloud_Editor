@DEPRECATED
Start Wildfly and deploy DyWA

in dywa-config/
mvn install -Pdefault -Pembedded -Pdeploy -Pstartwildfly -Dwildfly.path=/Applications/wildfly-10.1.0.Final

Clean Wildfly and DyWA

mvn clean -Pdefault -Pembedded -Dwildfly.path=/Applications/wildfly-10.1.0.Final

Deploy Pyro

mvn install -Pembedded -X -Ddart.path=/usr/local/Cellar/dart/1.23.0/libexec -Dwildfly.path=/Applications/wildfly-10.1.0.Final