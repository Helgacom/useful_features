package com.helga.lib.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MapperServiceImpl implements MapperService {

    private final ObjectMapper objectMapper;

    // "item-options.json" for prod
    // "src/main/resources/item-options.json" for dev
    @Override
    public Options getOptions() {
        Options options = null;
        try {
            options = objectMapper.readValue(
                    new File("item-options.json"), Options.class);
        } catch (IOException e) {
            log.error("ошибка получения опций {}", e.getMessage());
        }
        return options;
    }
}
