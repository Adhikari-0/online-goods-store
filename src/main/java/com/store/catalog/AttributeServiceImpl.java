package com.store.catalog;

import com.store.catalog.dto.AttributeRequest;
import com.store.catalog.dto.AttributeResponse;
import com.store.catalog.dto.AttributeValueResponse;
import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository valueRepository;

    public AttributeServiceImpl(AttributeRepository attributeRepository,
                                AttributeValueRepository valueRepository) {
        this.attributeRepository = attributeRepository;
        this.valueRepository = valueRepository;
    }

    @Override
    public AttributeResponse create(AttributeRequest request) {
        if (attributeRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Attribute code already exists: " + request.code());
        }
        Attribute a = new Attribute();
        a.setCode(request.code().toLowerCase().trim());
        a.setName(request.name());
        a.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        return AttributeResponse.from(attributeRepository.save(a));
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeResponse getById(Long id) {
        return AttributeResponse.from(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeResponse getByCode(String code) {
        return AttributeResponse.from(
            attributeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found: " + code))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeResponse> listAll() {
        return attributeRepository.findAll().stream().map(AttributeResponse::from).toList();
    }

    @Override
    public AttributeValueResponse addValue(Long attributeId, String value, Integer sortOrder) {
        Attribute attribute = getEntity(attributeId);
        AttributeValue av = new AttributeValue();
        av.setAttribute(attribute);
        av.setValue(value);
        av.setSortOrder(sortOrder != null ? sortOrder : 0);
        return AttributeValueResponse.from(valueRepository.save(av));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeValueResponse> listValues(Long attributeId) {
        return valueRepository.findAllByAttributeIdOrderBySortOrderAsc(attributeId).stream()
            .map(AttributeValueResponse::from)
            .toList();
    }

    @Override
    public void deleteValue(Long valueId) {
        valueRepository.deleteById(valueId);
    }

    private Attribute getEntity(Long id) {
        return attributeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute", id));
    }
}