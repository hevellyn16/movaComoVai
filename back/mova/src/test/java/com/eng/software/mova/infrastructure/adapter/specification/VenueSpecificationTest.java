package com.eng.software.mova.infrastructure.adapter.specification;

import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VenueSpecificationTest {

    @Mock
    private Root<VenueEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> namePath;

    @Mock
    private Path<String> cityPath;

    @Mock
    private Path<String> neighborhoodPath;

    @Mock
    private Predicate pred1;

    @Mock
    private Predicate pred2;

    @Mock
    private Predicate pred3;

    @Mock
    private Predicate combined;

    @Test
    public void shouldReturnConjunction_whenQIsNullOrBlank() {
        Specification<VenueEntity> spec = VenueSpecification.searchByText(null);

        when(cb.conjunction()).thenReturn(pred1);

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(pred1);
        verify(cb, times(1)).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    public void shouldCreateLikePredicates_whenQNotBlank() {
        String q = "Jazz";
        Specification<VenueEntity> spec = VenueSpecification.searchByText(q);

        doReturn(namePath).when(root).get("name");
        doReturn(neighborhoodPath).when(root).get("neighborhood");
        doReturn(cityPath).when(root).get("city");

        when(cb.lower(namePath)).thenReturn(namePath);
        when(cb.lower(neighborhoodPath)).thenReturn(neighborhoodPath);
        when(cb.lower(cityPath)).thenReturn(cityPath);

        when(cb.like(namePath, "%" + q.toLowerCase() + "%")).thenReturn(pred1);
        when(cb.like(neighborhoodPath, "%" + q.toLowerCase() + "%")).thenReturn(pred2);
        when(cb.like(cityPath, "%" + q.toLowerCase() + "%")).thenReturn(pred3);

        when(cb.or(pred1, pred2, pred3)).thenReturn(combined);

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(combined);
        verify(root).get("name");
        verify(root).get("neighborhood");
        verify(root).get("city");
        verify(cb).lower(namePath);
        verify(cb).lower(neighborhoodPath);
        verify(cb).lower(cityPath);
        verify(cb).like(namePath, "%" + q.toLowerCase() + "%");
        verify(cb).like(neighborhoodPath, "%" + q.toLowerCase() + "%");
        verify(cb).like(cityPath, "%" + q.toLowerCase() + "%");
        verify(cb).or(pred1, pred2, pred3);
    }
}


