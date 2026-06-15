package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Answer;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.domain.port.AnswerRepositoryPort;
import com.eng.software.mova.infrastructure.persistence.entity.AnswerEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.shared.utils.AnswerConverter;
import com.eng.software.mova.shared.utils.CommentConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnswerRepository implements AnswerRepositoryPort {
    private final AnswerJpaRepository answerJpaRepository;

    @Override
    public Optional<Answer> findById(UUID answerId) {
        return answerJpaRepository.findById(answerId)
                .map(AnswerConverter::entityToDomain);
    }

    @Override
    public Page<Answer> findByCommentId(UUID answerId, Pageable pageable) {
        return answerJpaRepository.findByCommentId(answerId, pageable)
                .map(AnswerConverter::entityToDomain);
    }

    @Override
    public Answer save(Answer answer) {
        AnswerEntity answerEntity = AnswerConverter.domainToEntity(answer);
        answerEntity = answerJpaRepository.save(answerEntity);

        return AnswerConverter.entityToDomain(answerEntity);
    }

    @Override
    public Answer update(Answer answer) {
        AnswerEntity answerEntity = AnswerConverter.domainToEntity(answer);
        answerEntity = answerJpaRepository.save(answerEntity);
        return AnswerConverter.entityToDomain(answerEntity);
    }

    @Override
    public void delete(UUID answerId) {
        answerJpaRepository.deleteById(answerId);
    }
}
