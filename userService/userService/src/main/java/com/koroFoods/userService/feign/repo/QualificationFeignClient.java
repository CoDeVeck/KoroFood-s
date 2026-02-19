package com.koroFoods.userService.feign.repo;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.feign.dto.GraficoSeisDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "qualificationService")
public interface QualificationFeignClient {

    @GetMapping("/calificacion/feign/graficoSeis")
    ResultadoResponse<GraficoSeisDto> graficoSeisList(@RequestParam("mes")Integer mes);
}
