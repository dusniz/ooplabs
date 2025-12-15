package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.dto.FactoryRequest;
import ru.ssau.tk.enjoyers.ooplabs.dto.FactoryResponse;
import ru.ssau.tk.enjoyers.ooplabs.services.OperationService;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingController {

    private static final Logger logger = LogManager.getLogger(SettingController.class);

    @Autowired
    private OperationService mathOperationsService;

    @GetMapping("/factory-type")
    public ResponseEntity<FactoryResponse> getFactoryType() {
        logger.info("GET запрос на получение типа фабрики");
        try {
            String factoryType = mathOperationsService.getFactoryType();

            FactoryResponse response = FactoryResponse.builder()
                    .factoryType(factoryType)
                    .description(factoryType.equals("TABULATED") ?
                            "Табулированные функции (на основе точек)" :
                            "Математические функции (на основе выражений)")
                    .build();

            logger.info("Тип фабрики успешно получен: {}", factoryType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("getFactoryType INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/factory-type")
    public ResponseEntity<Void> setFactoryType(@RequestBody FactoryRequest request) {
        logger.info("POST запрос на установку типа фабрики: {}", request.getFactoryType());
        try {
            mathOperationsService.setFactoryType(request.getFactoryType());
            logger.info("Тип фабрики успешно установлен: {}", request.getFactoryType());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("setFactoryType BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("setFactoryType INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
