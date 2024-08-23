package toni.examplemod.foundation.config;

import toni.lib.config.ConfigBase;

public class CCommon extends ConfigBase {

    public final ConfigBool example = b(true, "example", "Example Boolean");


    @Override
    public String getName() {
        return "common";
    }
}
