package io.synkronize.scheduler.utils.net;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AddressResolver {

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    String port;

    public String resolveHostname() {
        String addressResolverStrategy = ConfigProvider.getConfig()
                .getValue("synkronize.address-resolver.strategy", String.class);

        String name = "";
        if (addressResolverStrategy.equals("local")) {
            name = "localhost";
        } else if (addressResolverStrategy.equalsIgnoreCase("k8s")) {
            name = System.getenv("POD_NAME") + "." + System.getenv("SVC_NAME");
        }

        return name + ":" + port;

    }

}
