package org.laundry.config;
import org.laundry.entity.Machine;
import org.laundry.repository.MachineRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToMachineConverter implements Converter<String, Machine>{
    private final MachineRepository machineRepository;
    public StringToMachineConverter(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }
    @Override
    public Machine convert(String source) {
        try {
            Long id = Long.parseLong(source);
            return machineRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
