#vro-plugin-base

Base library for vRO plug-in generation using Monolithe.

#build

The following library needs to be built prior to building the plug-in:

* [java-bambou](https://github.com/nuagenetworks/java-bambou)

To build library: 

* mvn -Dmaven.wagon.http.ssl.insecure=true -Dvco.version={vro-version} -DrepoUrl=https://{vro-ip-address}:8281/vco-repo/ clean install

Example:

* mvn -Dmaven.wagon.http.ssl.insecure=true -Dvco.version=7.0.1 -DrepoUrl=https://192.168.1.15:8281/vco-repo/ clean install
