package com.zpi.accommodationservice.accommodationservice.accommodation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
public class Accommodation {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "accommodation_sequence"
    )
    @SequenceGenerator(
            name = "accommodation_sequence",
            sequenceName = "accommodation_sequence", allocationSize = 10)
    @Column(name = "accommodation_id",unique = true, nullable = false)
    private Long accommodationId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "creator_id", nullable = false)
    private Long creator_id;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "source_link")
    private String sourceLink;

    @Column(name = "given_votes", nullable = false)
    private Integer givenVotes;

    @Column(name = "price")
    private BigDecimal price;

    public Accommodation(Long groupId, Long creator_id, String name, String address, String description,
                         String imageLink, String sourceLink, Integer givenVotes, BigDecimal price) {
        this.groupId = groupId;
        this.creator_id = creator_id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.imageLink = imageLink;
        this.sourceLink = sourceLink;
        this.givenVotes = givenVotes;
        this.price = price;
    }

    public Accommodation(Long groupId, Long creator_id, String name, String address, String description,
                         String imageLink, String sourceLink, BigDecimal price) {
        this.groupId = groupId;
        this.creator_id = creator_id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.imageLink = imageLink;
        this.sourceLink = sourceLink;
        this.price = price;
    }
}
