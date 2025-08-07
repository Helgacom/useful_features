package com.helga.lib.objectmapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionsController {

    private final MapperServiceImpl mapperService;
    /**
     * Метод Get для передачи опций для панелей select.
     */
    @GetMapping(value = "/tts")
    public ResponseEntity<Options> getOptionsOnSelect() {
        var options = this.mapperService.getOptions();
        return ResponseEntity.ok().body(options);
    }
}
