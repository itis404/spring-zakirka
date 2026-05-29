package org.laundry.config;

import org.laundry.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRoleConverter implements Converter<String, User.Role> {

    @Override
    public User.Role convert(String source) {
        try {
            return User.Role.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return User.Role.USER;
        }
    }
}
