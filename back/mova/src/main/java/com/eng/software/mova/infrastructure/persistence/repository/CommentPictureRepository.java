package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.CommentPicture;
import com.eng.software.mova.domain.port.CommentPictureRepositoryPort;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.infrastructure.persistence.entity.CommentPictureEntity;
import com.eng.software.mova.shared.utils.CommentPictureConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentPictureRepository implements CommentPictureRepositoryPort {
    private final CommentPictureJpaRepository commentPictureJpaRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public Optional<CommentPicture> findById(UUID id) {
        return commentPictureJpaRepository.findById(id).map(CommentPictureConverter::entityToDomain);
    }

    @Override
    public Page<CommentPicture> findByCommentId(UUID commentId, Pageable pageable) {
        return commentPictureJpaRepository.findByCommentId(commentId, pageable)
                .map(CommentPictureConverter::entityToDomain);
    }

    @Override
    public CommentPicture save(CommentPicture commentPicture) {
        CommentPictureEntity entity = CommentPictureConverter.domainToEntity(commentPicture);
        return CommentPictureConverter.entityToDomain(commentPictureJpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        CommentPictureEntity pictureEntity = commentPictureJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment picture not found with id: " + id));

        commentPictureJpaRepository.delete(pictureEntity);
        fileStoragePort.deleteFile(pictureEntity.getPictureUrl());
    }
}

