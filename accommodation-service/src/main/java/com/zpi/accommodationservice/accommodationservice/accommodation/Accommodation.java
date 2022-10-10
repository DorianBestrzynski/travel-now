package com.zpi.accommodationservice.accommodationservice.accommodation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.zpi.accommodationservice.accommodationservice.comons.Utils.DEFAULT_VOTES;

@Entity
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "name")
    private String name;
    @Column(name = "street_address")
    private String streetAddress;
    @Column(name = "country", length = 80)
    private String country;
    @Column(name = "region", length = 100)
    private String region;
    @Column(name = "description")
    private String description;
    @Column(name = "image_link")
    private String imageLink;
    @Column(name = "source_link", columnDefinition = "TEXT")
    private String sourceLink;
    @Column(name = "given_votes", nullable = false)
    private Integer givenVotes;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;

    public Accommodation(Long groupId, Long creator_id, String name, String streetAddress, String country, String region, String description, String imageLink, String sourceLink, BigDecimal price, Double latitude, Double longitude) {
        this.groupId = groupId;
        this.creator_id = creator_id;
        this.name = name;
        this.streetAddress = streetAddress;
        this.country = country;
        this.region = region;
        this.description = description;
        this.imageLink = imageLink;
        this.sourceLink = sourceLink;
        this.price = price;
        this.givenVotes = DEFAULT_VOTES;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
