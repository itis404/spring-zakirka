package org.laundry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToMachineConverter stringToMachineConverter;
    private final StringToRoleConverter stringToRoleConverter;

    public WebConfig(StringToMachineConverter stringToMachineConverter,
                     StringToRoleConverter stringToRoleConverter) {
        this.stringToMachineConverter = stringToMachineConverter;
        this.stringToRoleConverter = stringToRoleConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToMachineConverter);
        registry.addConverter(stringToRoleConverter);
    }
}
