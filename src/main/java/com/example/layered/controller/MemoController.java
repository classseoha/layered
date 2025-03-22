package com.example.layered.controller;

import com.example.layered.dto.MemoRequestDto;
import com.example.layered.dto.MemoResponseDto;
import com.example.layered.entity.Memo;
import com.example.layered.service.MemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController //@Controller + @ResponseBody
@RequestMapping("/memos") //Prefix
public class MemoController {

    // 최초로 생성된(주입된) memoService가 의존성을 변경할 수 없어 객체의 상태를 안전하게 유지할 수 있다.
    private  final MemoService memoService;

    /**
     * 생성자 주입
     * 클래스가 필요로 하는 의존성을 생성자를 통해 전달하는 방식
     * @param memoService @Service로 등록된 MemoService 구현체인 Impl
     */
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    } //컨트롤러에 메모서비스를 주입시키기 위해서는 위에 코드 항상 유지해야함(private final~~ 생성자까지)

    //요청(controller) 역할
    @PostMapping
    public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto dto) {

        //응답 역할(Controller)
        return new ResponseEntity<>(memoService.saveMemo(dto), HttpStatus.CREATED); //Service Layer 호출 및 응답
    }

    @GetMapping
    public List<MemoResponseDto> findAllMemos() {

        return memoService.findAllMemos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {

        return new ResponseEntity<>(memoService.findMemoById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto dto) {

        return new ResponseEntity<>(memoService.updateMemo(id, dto.getTitle(), dto.getContents()), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(@PathVariable Long id, @RequestBody MemoRequestDto dto) {

        return new ResponseEntity<>(memoService.updateTitle(id, dto.getTitle(), dto.getContents()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        memoService.deleteMemo(id);

        return new ResponseEntity<>(HttpStatus.OK); //void지만 상태코드 반환하기 위해 return 사용
    }

}
