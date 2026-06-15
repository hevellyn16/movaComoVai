package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.answer.AnswerCreateDTO;
import com.eng.software.mova.application.dto.answer.AnswerResponseDTO;
import com.eng.software.mova.domain.model.Answer;
import com.eng.software.mova.infrastructure.persistence.repository.AnswerRepository;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.AnswerConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public AnswerResponseDTO findById(UUID id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));

        return AnswerConverter.domainToResponseDTO(answer);
    }

    @Transactional(readOnly = true)
    public Page<AnswerResponseDTO> findByCommentId(UUID commentId, Pageable pageable) {
        Page<Answer> answers = answerRepository.findByCommentId(commentId, pageable);
        return answers.map(AnswerConverter::domainToResponseDTO);
    }

    public AnswerResponseDTO create(AnswerCreateDTO answerCreateDTO, UUID authorId , UUID commentId) {
        Answer savedAnswer = AnswerConverter.createDTOToDomain(answerCreateDTO, authorId, commentId);
        savedAnswer =  answerRepository.save(savedAnswer);
        return AnswerConverter.domainToResponseDTO(savedAnswer);
    }

    public AnswerResponseDTO update(AnswerCreateDTO answerUpdateDTO, UUID answerId, UUID authorId) {
        Answer savedAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        if (!savedAnswer.getAuthorId().equals(authorId))
            throw new ResourceNotFoundException("Answer not found for this author");

        AnswerConverter.updateAnswer(savedAnswer, answerUpdateDTO);
        return AnswerConverter.domainToResponseDTO(answerRepository.save(savedAnswer));
    }

    public void deleteById(UUID answerId,  UUID authorId) {
        Answer savedAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        if (!savedAnswer.getAuthorId().equals(authorId))
            throw new ResourceNotFoundException("Answer not found for this author");

        answerRepository.delete(answerId);
    }
}
