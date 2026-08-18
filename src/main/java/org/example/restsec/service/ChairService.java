package org.example.restsec.service;

import lombok.RequiredArgsConstructor;
import org.example.restsec.entity.ChairEntity;
import org.example.restsec.repository.ChairJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 컴포넌트 스캔 및 트랜잭션 대응
@RequiredArgsConstructor // 생성자 주입을 편하게
@Transactional(readOnly = true) // <- 기본적으로는 read로 하고, insert/update 작업은 별도로 transactional을 추가 기술
public class ChairService {
    private final ChairJpaRepository chairJpaRepository;

    // 생성자 주입 -> 필수 내용 (final로 되어 있는 패러미터들을 알아서 생성자로 만들어주는 것)
//    public ChairService(ChairJpaRepository chairJpaRepository) {
//        this.chairJpaRepository = chairJpaRepository;
//    }

    // findAll
    public List<ChairEntity> findAll() {
        return chairJpaRepository.findAll();
    }

    // save
    @Transactional
    public ChairEntity save(ChairEntity chairEntity) {
        return chairJpaRepository.save(chairEntity);
    }
}