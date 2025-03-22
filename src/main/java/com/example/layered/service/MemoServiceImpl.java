package com.example.layered.service;

import com.example.layered.dto.MemoRequestDto;
import com.example.layered.dto.MemoResponseDto;
import com.example.layered.entity.Memo;
import com.example.layered.repository.MemoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Service
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;

    public MemoServiceImpl(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    @Override
    public MemoResponseDto saveMemo(MemoRequestDto dto) {

        //요청받은 데이터로 MEMO 객체 생성, ID 값은 없음
        Memo memo = new Memo(dto.getTitle(), dto.getContents()); //세미콜론 앞쪽에 커서 두고 Ctrl+Alt+V 누르면 자동으로 변수 선언해줌

        return memoRepository.saveMemo(memo);
    }

    @Override
    public List<MemoResponseDto> findAllMemos() {

        return memoRepository.findAllMemos();
    }

    @Override
    public MemoResponseDto findMemoById(Long id) {

        Memo memo = memoRepository.findMemoByIdOrElseThrow(id); //메모가 있는 경우에만 항상 값이 반환되기 때문에 하단 검증 필요없어짐

//        if(optionalMemo.isEmpty()) { //Optional을 사용하면 항상 isEmpty로 검증하는 메서드가 필요함
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
//        }

        return new MemoResponseDto(memo);
    }

    @Transactional //수정되면 조회도 되고 수정안되면 조회도 안되게 하는 어노테이션(하단의 메서드가 아예 트랜잭션화 됨)
    @Override
    public MemoResponseDto updateMemo(Long id, String title, String contents) {

        if(title == null || contents == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        int updatedRow = memoRepository.updateMemo(id, title, contents); //이걸 통해 실제 DB에 있는 메모 데이터가 수정됨

        if(updatedRow == 0) { //해당 id값을 가진 Row 수가 0이라면 notFound
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

//        Optional<Memo> optionalMemo = memoRepository.findMemoById(id); //수정된 DB에 접근해서 메모를 조회함
        Memo memo = memoRepository.findMemoByIdOrElseThrow(id);

        return new MemoResponseDto(memo);
    }

    @Transactional //이걸 넣어줘야 논리적인 작업 단위가 되면서 수정이 성공하고 조회가 실패하는 일을 방지해줌
    @Override
    public MemoResponseDto updateTitle(Long id, String title, String contents) {

        if(title == null || contents != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        int updatedRow = memoRepository.updateTitle(id, title);

        if(updatedRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

//        Optional<Memo> optionalMemo = memoRepository.findMemoById(id);
        Memo memo = memoRepository.findMemoByIdOrElseThrow(id);

        return new MemoResponseDto(memo); //메모리 상(memoList)에 존재하는 Memo를 직접 수정하기 때문에 DB 접근이 필요없음
    }

    @Override
    public void deleteMemo(Long id) {

        int deletedRow = memoRepository.deleteMemo(id);

        if(deletedRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }
    }
}
