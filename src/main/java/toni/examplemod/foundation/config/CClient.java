package toni.examplemod.foundation.config;

import toni.lib.config.ConfigBase;

public class CClient extends ConfigBase {

    public final ConfigGroup client = group(0, "client", "Client-only settings - If you're looking for general settings, look inside your world's serverconfig folder!");

    public final ConfigBool example = b(true, "example", "Example Boolean");

    @Override
    public String getName() {
        return "client";
    }
}
