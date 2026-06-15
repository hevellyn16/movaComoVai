package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AnswerRepositoryPort {
    Optional<Answer> findById(UUID id);
    Page<Answer> findByCommentId(UUID answerId, Pageable pageable);
    Answer save(Answer answer);
    Answer update(Answer answer);
    void delete(UUID answerId);
}
