package org.inoventory.inventoryservice.Configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Mapper {

    @Bean
    public ModelMapper getMapper(){
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true);  // 👈 important for partial update

        return modelMapper;
    }

}