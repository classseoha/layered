package com.example.layered.repository;

import com.example.layered.dto.MemoResponseDto;
import com.example.layered.entity.Memo;

import java.util.List;
import java.util.Optional;

public interface MemoRepository {

    MemoResponseDto saveMemo(Memo memo); //ID값이 없는 상태로 repository에 전달, sql mapper 사용하면 memoresponse 형태로 바로 mapping 받을 수 있음

    List<MemoResponseDto> findAllMemos();

    Optional<Memo> findMemoById(Long id);

    Memo findMemoByIdOrElseThrow(Long id); //Optional 검증 메서드

    int updateMemo(Long id, String title, String contents);

    int updateTitle(Long id, String title);

    int deleteMemo(Long id);




}
