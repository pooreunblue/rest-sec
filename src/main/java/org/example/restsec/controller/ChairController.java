package org.example.restsec.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.restsec.entity.ChairEntity;
import org.example.restsec.service.ChairService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody
// @Controller -> 뷰 이름 (String) -> 타임리프
// 메서드 -> @ResponseBody -> String, 객체 등을 직접 반환
// 컨트롤러 전체 범위로 적용하는 것 -> @RestController (컴포넌트 스캔도 겸함)
@RequestMapping("/chair")
// DispatcherServlet <- 중앙에서 모든 요청을 RequestMapping을 통해 배분
// -> 각각 세부 메서드 앞에 prefix(접두사)로 /chair -> /chair/{id}...
@RequiredArgsConstructor // 생성자 주입
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ChairController {
    private final ChairService chairService;

    @GetMapping
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    public List<ChairEntity> getChairs() {
        return chairService.findAll();
    }
    // 인증 없이는 CrossOrigin으로 직접 오픈하면 되는데

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public void deleteChair() {
        // 403 권한 테스트용
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ChairEntity saveChair(
            @RequestBody ChairRequest dto) {
        return chairService.save(dto.toEntity());
    }

    public record ChairRequest(String name, int price) {
        public ChairEntity toEntity() {
            return ChairEntity.builder()
                    .name(name)
                    .price(price)
                    .build();
        }
    }
}