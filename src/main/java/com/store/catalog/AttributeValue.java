package com.store.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "attribute_values",
    uniqueConstraints = @UniqueConstraint(columnNames = {"attribute_id", "value"})
)
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false, length = 100)
    private String value; // "Red", "XL", "Cotton"

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}